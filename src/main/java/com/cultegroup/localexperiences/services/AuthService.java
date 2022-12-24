package com.cultegroup.localexperiences.services;

import com.cultegroup.localexperiences.DTO.AuthRequestDTO;
import com.cultegroup.localexperiences.DTO.TokenDTO;
import com.cultegroup.localexperiences.model.Status;
import com.cultegroup.localexperiences.model.User;
import com.cultegroup.localexperiences.repo.UserRepository;
import com.cultegroup.localexperiences.security.JwtTokenProvider;
import com.cultegroup.localexperiences.utils.ValidatorUtils;
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
    private final JwtTokenProvider provider;
    private final BCryptPasswordEncoder encoder;
    private final ValidatorUtils validatorUtils;
    private final ActivateService activateService;

    // TODO CHANGE TO DATABASE, REDIS, FOR EXAMPLE
    private final HashMap<String, String> refreshStorage = new HashMap<>();

    public AuthService(AuthenticationManager manager,
                       UserRepository userRepository,
                       JwtTokenProvider provider,
                       BCryptPasswordEncoder encoder, ValidatorUtils validatorUtils, ActivateService activateService) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.provider = provider;
        this.encoder = encoder;
        this.validatorUtils = validatorUtils;
        this.activateService = activateService;
    }

    public ResponseEntity<?> getAuthResponse(AuthRequestDTO request) {
        try {
            String identifier = request.getIdentifier();
            manager.authenticate(new UsernamePasswordAuthenticationToken(identifier, request.getPassword()));

            User user = validatorUtils.getUserByIdentifier(identifier);
            if (user.getStatus().equals(Status.INACTIVE)) {
                // TODO ADD ACTIVATION BY PHONE NUMBER
                return new ResponseEntity<>("Аккаунт не активирован!", HttpStatus.UNAUTHORIZED);
            }

            String accessToken = provider.createAccessToken(identifier, user.getId());
            String refreshToken = provider.createRefreshToken(identifier, user.getId());

            refreshStorage.put(identifier, refreshToken);
            Map<Object, Object> response = new HashMap<>() {{
                put("identifier", identifier);
                put("accessToken", accessToken);
                put("refreshToken", refreshToken);
            }};
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Некорректные данные!", HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<?> updateAccessToken(TokenDTO dto) {
        try {
            String refreshToken = dto.getRefreshToken();
            if (provider.validateRefreshToken(refreshToken)) {
                String identifier = provider.getUsernameByRefreshToken(refreshToken);
                String refresh = refreshStorage.get(identifier);

                if (refresh != null && refresh.equals(refreshToken)) {
                    User user = validatorUtils.getUserByIdentifier(identifier);
                    String access = provider.createAccessToken(identifier, user.getId());

                    Map<Object, Object> response = new HashMap<>();
                    response.put("accessToken", access);
                    return ResponseEntity.ok(response);
                }
            }
            return new ResponseEntity<>("Невалидный refresh token", HttpStatus.BAD_REQUEST);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Некорректные данные!", HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<?> refresh(TokenDTO dto) {
        try {
            String refreshToken = dto.getRefreshToken();
            if (refreshToken != null && dto.getAccessToken() != null
                    && provider.validateRefreshToken(refreshToken)) {
                String identifier = provider.getUsernameByRefreshToken(refreshToken);
                String refresh = refreshStorage.get(identifier);

                if (refresh != null && refresh.equals(refreshToken)) {
                    User user = validatorUtils.getUserByIdentifier(identifier);
                    String access = provider.createAccessToken(identifier, user.getId());
                    String newRefresh = provider.createRefreshToken(identifier, user.getId());

                    refreshStorage.put(identifier, newRefresh);
                    Map<Object, Object> response = new HashMap<>() {{
                        put("accessToken", access);
                        put("refreshToken", newRefresh);
                    }};
                    return ResponseEntity.ok(response);
                }
            }
            return new ResponseEntity<>("Невалидный refresh token", HttpStatus.BAD_REQUEST);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Некорректные данные!", HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<?> register(AuthRequestDTO request) {
        String identifier = request.getIdentifier();
        if (userRepository.existsByEmail(identifier)) {
            return new ResponseEntity<>("Этот адрес электронной почты уже используется!",
                    HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByPhoneNumber(identifier)) {
            return new ResponseEntity<>("Этот номер телефона уже используется!",
                    HttpStatus.BAD_REQUEST);
        }

        User user;
        String password = encoder.encode(request.getPassword());
        if (validatorUtils.isEmail(identifier)) {
            user = new User(null, null, identifier, null, password);
        } else {
            user = new User(null, null, null, identifier, password);
        }

        userRepository.save(user);
        activateService.sendActivationMessage(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
