package com.cultegroup.localexperiences.authentication.services;

import com.cultegroup.localexperiences.authentication.DTO.EmailDTO;
import com.cultegroup.localexperiences.authentication.DTO.TokensDTO;
import com.cultegroup.localexperiences.authentication.DTO.UserInfoDTO;
import com.cultegroup.localexperiences.authentication.exceptions.InvalidEmailException;
import com.cultegroup.localexperiences.authentication.model.RefreshToken;
import com.cultegroup.localexperiences.authentication.repo.RefreshRepository;
import com.cultegroup.localexperiences.authentication.repo.UserRepository;
import com.cultegroup.localexperiences.authentication.security.JwtTokenProvider;
import com.cultegroup.localexperiences.authentication.utils.ValidatorUtils;
import com.cultegroup.localexperiences.shared.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private final static Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

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
        if (userRepository.existsByEmail(email.email().toLowerCase())) {
            return ResponseEntity.ok(HttpEntity.EMPTY);
        }
        return new ResponseEntity<>("Учётная запись не найдена.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> signIn(UserInfoDTO request) {
        try {
            validateUserInfo(request);

            String email = request.email().toLowerCase();
            manager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

            User user = validatorUtils.getUserByEmail(email);

            TokensDTO response = createTokens(user, email);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    public ResponseEntity<?> signUp(UserInfoDTO request) {
        try {
            validateUserInfo(request);

            String password = encoder.encode(request.password());
            User user = new User(request.email().toLowerCase(), password);

            userRepository.save(user);
            activateService.sendActivationMessage(user);

            TokensDTO response = createTokens(user, request.email());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidEmailException e) {
            return new ResponseEntity<>(e.getMessage(), e.getHttpStatus());
        }
    }

    public ResponseEntity<?> updateAccessToken(TokensDTO dto) {
        String refreshToken = dto.refreshToken();
        if (provider.validateRefreshToken(refreshToken)) {
            String email = provider.getUsernameByRefreshToken(refreshToken);
            User user = validatorUtils.getUserByEmail(email);
            RefreshToken refresh = refreshRepository.findByUser(user).orElse(null);

            if (refresh != null && refresh.getToken().equals(refreshToken)) {
                String accessToken = provider.createAccessToken(email, user.getId());

                return ResponseEntity.ok(new TokensDTO(email, accessToken, null));
            }
        }
        return new ResponseEntity<>("Невалидный refresh токен", HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<?> refresh(TokensDTO dto) {
        String refreshToken = dto.refreshToken();
        if (refreshToken != null && dto.accessToken() != null
                && provider.validateRefreshToken(refreshToken)) {
            String email = provider.getUsernameByRefreshToken(refreshToken);
            User user = validatorUtils.getUserByEmail(email);
            RefreshToken refresh = refreshRepository.findByUser(user).orElse(null);

            if (refresh != null && refresh.getToken().equals(refreshToken)) {
                TokensDTO response = createTokens(user, email);

                refreshRepository.delete(refresh);
                refreshRepository.save(new RefreshToken(response.refreshToken(), user));

                return ResponseEntity.ok(response);
            }
        }
        return new ResponseEntity<>("Невалидный refresh токен", HttpStatus.BAD_REQUEST);
    }

    private static void validateUserInfo(UserInfoDTO request) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(request.email());
        if (!matcher.find()) {
            throw new InvalidEmailException("Невалидный адрес электронной почты.", HttpStatus.BAD_REQUEST);
        }
    }

    private TokensDTO createTokens(User user, String email) {
        String accessToken = provider.createAccessToken(email, user.getId());
        String refreshToken = provider.createRefreshToken(email, user.getId());

        refreshRepository.save(new RefreshToken(refreshToken, user));
        return new TokensDTO(email, accessToken, refreshToken);
    }
}
