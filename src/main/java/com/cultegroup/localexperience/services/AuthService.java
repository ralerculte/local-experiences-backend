package com.cultegroup.localexperience.services;

import com.cultegroup.localexperience.model.Status;
import com.cultegroup.localexperience.model.User;
import com.cultegroup.localexperience.repo.UserRepository;
import com.cultegroup.localexperience.security.JwtTokenProvider;
import com.cultegroup.localexperience.DTO.AuthRequestDTO;
import com.cultegroup.localexperience.DTO.TokenDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final String EMAIL_REGEX = "^(.+)@(.+)$";

    private final AuthenticationManager manager;
    private final UserRepository userRepository;
    private final JwtTokenProvider provider;
    private final BCryptPasswordEncoder encoder;
    private final MailService mailService;

    // TODO CHANGE TO DATABASE, REDIS, FOR EXAMPLE
    private final HashMap<String, String> refreshStorage = new HashMap<>();

    public AuthService(AuthenticationManager manager,
                       UserRepository userRepository,
                       JwtTokenProvider provider,
                       BCryptPasswordEncoder encoder, MailService mailService) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.provider = provider;
        this.encoder = encoder;
        this.mailService = mailService;
    }

    public User getUserByIdentifier(String identifier) {
        return identifier.matches(EMAIL_REGEX)
                ? userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с такой почтой не найден!"))
                : userRepository.findByPhoneNumber(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким номером телефона не найден!"));
    }

    public ResponseEntity<?> getAuthResponse(AuthRequestDTO request) {
        try {
            String identifier = request.getIdentifier();
            manager.authenticate(new UsernamePasswordAuthenticationToken(identifier, request.getPassword()));

            User user = getUserByIdentifier(identifier);
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
                    User user = getUserByIdentifier(identifier);
                    System.out.println("!");
                    String access = provider.createAccessToken(identifier, user.getId());
                    System.out.println("!");

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
                    User user = getUserByIdentifier(identifier);
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
        if (identifier.matches(EMAIL_REGEX)) {
            user = new User(null, null, identifier, null, password);
        } else {
            user = new User(null, null, null, identifier, password);
        }
        userRepository.save(user);

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            String message = "Для вступления в культ перейди по ссылке:\n"
                    + "http://localhost:8080/api/v1/auth/activate/" + user.getId();

            mailService.sendMail(user.getEmail(), "Активация аккаунта на experiences.com", message);
        }
        // TODO ADD ACTIVATION BY PHONE NUMBER
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    public void activate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователя не существует"));
        user.setStatus(Status.ACTIVE);
    }
}
