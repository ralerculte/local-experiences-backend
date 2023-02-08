package com.cultegroup.findguide.authentication.services;

import com.cultegroup.findguide.authentication.DTO.AuthResponseDTO;
import com.cultegroup.findguide.authentication.DTO.EmailDTO;
import com.cultegroup.findguide.authentication.DTO.TokensDTO;
import com.cultegroup.findguide.authentication.DTO.UserInfoDTO;
import com.cultegroup.findguide.authentication.exceptions.InvalidEmailException;
import com.cultegroup.findguide.authentication.repo.RedisRepo;
import com.cultegroup.findguide.authentication.security.JwtTokenProvider;
import com.cultegroup.findguide.authentication.utils.ValidatorUtils;
import com.cultegroup.findguide.data.model.User;
import com.cultegroup.findguide.data.repo.UserRepository;
import com.cultegroup.findguide.shared.exceptions.HttpStatusException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Duration REFRESH_TIMEOUT = Duration.ofDays(25);

    private final AuthenticationManager manager;
    private final UserRepository userRepository;
    private final JwtTokenProvider provider;
    private final BCryptPasswordEncoder encoder;
    private final ValidatorUtils validatorUtils;
    private final ActivateService activateService;
    private final RedisRepo refreshRepo;

    public AuthService(
            AuthenticationManager manager,
            UserRepository userRepository,
            JwtTokenProvider provider,
            BCryptPasswordEncoder encoder,
            ValidatorUtils validatorUtils,
            ActivateService activateService,
            RedisRepo redis) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.provider = provider;
        this.encoder = encoder;
        this.validatorUtils = validatorUtils;
        this.activateService = activateService;
        this.refreshRepo = redis;
    }

    public ResponseEntity<?> isExist(EmailDTO email) {
        if (userRepository.existsByEmail(email.email().toLowerCase())) {
            return ResponseEntity.ok(HttpEntity.EMPTY);
        }
        return new ResponseEntity<>("Учётная запись не найдена.", HttpStatus.NOT_FOUND);
    }

    @Transactional
    public ResponseEntity<?> signIn(UserInfoDTO request) {
        try {
            validateUserInfo(request);

            String email = request.email().toLowerCase();
            manager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));

            User user = validatorUtils.getUserByEmail(email);

            String accessToken = provider.createAccessToken(user.getEmail());
            String refreshToken = provider.createAccessToken(user.getEmail());

            refreshRepo.set(email, refreshToken, REFRESH_TIMEOUT);
            AuthResponseDTO response = new AuthResponseDTO(user.getId(), accessToken, refreshToken);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public ResponseEntity<?> signUp(UserInfoDTO request) {
        try {
            validateUserInfo(request);

            String password = encoder.encode(request.password());
            String email = request.email().toLowerCase();
            String activationCode = UUID.randomUUID().toString();

            User user = new User(email, password, activationCode);

            String accessToken = provider.createAccessToken(user.getEmail());
            String refreshToken = provider.createRefreshToken(user.getEmail());

            userRepository.save(user);
            activateService.sendActivationMessage(user);
            refreshRepo.set(request.email(), refreshToken, REFRESH_TIMEOUT);

            AuthResponseDTO response = new AuthResponseDTO(user.getId(), accessToken, refreshToken);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    @Transactional
    public ResponseEntity<?> updateAccessToken(TokensDTO dto) {
        String refreshToken = dto.refreshToken();
        if (provider.validateRefreshToken(refreshToken)) {
            String email = provider.getUsernameByRefreshToken(refreshToken);
            String refresh = refreshRepo.get(email);

            if (refresh != null && refresh.equals(refreshToken)) {
                String accessToken = provider.createAccessToken(email);
                return new ResponseEntity<>(new TokensDTO(accessToken, null), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Невалидный refresh токен", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public ResponseEntity<?> refresh(TokensDTO dto) {
        String token = dto.refreshToken();
        if (token != null && dto.accessToken() != null
                && provider.validateRefreshToken(token)) {

            String email = provider.getUsernameByRefreshToken(token);
            String refresh = refreshRepo.get(email);

            if (refresh != null && refresh.equals(token)) {
                String accessToken = provider.createAccessToken(email);
                String refreshToken = provider.createRefreshToken(email);

                refreshRepo.set(email, refreshToken, REFRESH_TIMEOUT);
                return ResponseEntity.ok(new TokensDTO(accessToken, refreshToken));
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
}
