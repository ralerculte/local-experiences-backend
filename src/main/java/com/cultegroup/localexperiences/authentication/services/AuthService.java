package com.cultegroup.localexperiences.authentication.services;

import com.cultegroup.localexperiences.authentication.DTO.EmailDTO;
import com.cultegroup.localexperiences.authentication.DTO.TokenDTO;
import com.cultegroup.localexperiences.authentication.DTO.UserInfoDTO;
import com.cultegroup.localexperiences.authentication.model.RefreshToken;
import com.cultegroup.localexperiences.shared.model.User;
import com.cultegroup.localexperiences.authentication.repo.RefreshRepository;
import com.cultegroup.localexperiences.authentication.repo.UserRepository;
import com.cultegroup.localexperiences.authentication.security.JwtTokenProvider;
import com.cultegroup.localexperiences.authentication.utils.ValidatorUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager manager;
    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final JwtTokenProvider provider;
    private final BCryptPasswordEncoder encoder;
    private final ValidatorUtils validatorUtils;
    private final ActivateService activateService;

    public AuthService(AuthenticationManager manager,
                       UserRepository userRepository,
                       RefreshRepository refreshRepository, JwtTokenProvider provider,
                       BCryptPasswordEncoder encoder, ValidatorUtils validatorUtils, ActivateService activateService) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.refreshRepository = refreshRepository;
        this.provider = provider;
        this.encoder = encoder;
        this.validatorUtils = validatorUtils;
        this.activateService = activateService;
    }

    public ResponseEntity<?> isExist(EmailDTO email) {
        if (userRepository.existsByEmail(email.getEmail())) {
            return ResponseEntity.ok(HttpEntity.EMPTY);
        }
        return new ResponseEntity<>("Учётная запись не найдена.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> signIn(UserInfoDTO request) {
        try {
            String email = request.getEmail().toLowerCase();
            manager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));

            User user = validatorUtils.getUserByEmail(email);

            Map<String, String> response = createTokens(user, email);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Некорректные данные!", HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<?> updateAccessToken(TokenDTO dto) {
        String refreshToken = dto.getRefreshToken();
        if (provider.validateRefreshToken(refreshToken)) {
            String email = provider.getUsernameByRefreshToken(refreshToken);
            User user = validatorUtils.getUserByEmail(email);
            RefreshToken refresh = refreshRepository.findByUser(user).orElse(null);

            if (refresh != null && refresh.getToken().equals(refreshToken)) {
                String access = provider.createAccessToken(email, user.getId());

                Map<Object, Object> response = new HashMap<>();
                response.put("accessToken", access);
                return ResponseEntity.ok(response);
            }
        }
        return new ResponseEntity<>("Невалидный refresh токен", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> refresh(TokenDTO dto) {
        String refreshToken = dto.getRefreshToken();
        if (refreshToken != null && dto.getAccessToken() != null
                && provider.validateRefreshToken(refreshToken)) {
            String email = provider.getUsernameByRefreshToken(refreshToken);
            User user = validatorUtils.getUserByEmail(email);
            RefreshToken refresh = refreshRepository.findByUser(user).orElse(null);

            if (refresh != null && refresh.getToken().equals(refreshToken)) {
                Map<String, String> response = createTokens(user, email);

                refreshRepository.delete(refresh);
                refreshRepository.save(new RefreshToken(response.get("refreshToken"), user));

                return ResponseEntity.ok(response);
            }
        }
        return new ResponseEntity<>("Невалидный refresh токен", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> signUp(UserInfoDTO request) {
        String password = encoder.encode(request.getPassword());
        User user = new User(null, null, request.getEmail().toLowerCase(), null, password);

        userRepository.save(user);
        activateService.sendActivationMessage(user);

        Map<String, String> response = createTokens(user, request.getEmail());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Map<String, String> createTokens(User user, String email) {
        String accessToken = provider.createAccessToken(email, user.getId());
        String refreshToken = provider.createRefreshToken(email, user.getId());

        refreshRepository.save(new RefreshToken(refreshToken, user));
        return new HashMap<>() {{
            put("email", email);
            put("accessToken", accessToken);
            put("refreshToken", refreshToken);
        }};
    }
}
