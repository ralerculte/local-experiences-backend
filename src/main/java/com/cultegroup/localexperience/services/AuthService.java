package com.cultegroup.localexperience.services;

import com.cultegroup.localexperience.model.User;
import com.cultegroup.localexperience.repo.UserRepository;
import com.cultegroup.localexperience.security.JwtTokenProvider;
import com.cultegroup.localexperience.utils.AuthRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final String EMAIL_REGEX = "^(.+)@(.+)$";

    private final AuthenticationManager manager;
    private final UserRepository userRepository;
    private final JwtTokenProvider provider;

    public AuthService(AuthenticationManager manager, UserRepository userRepository, JwtTokenProvider provider) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.provider = provider;
    }

    public ResponseEntity<?> getAuthResponse(AuthRequestDTO request) {
        try {
            String identifier = request.getIdentifier();
            manager.authenticate(new UsernamePasswordAuthenticationToken(identifier, request.getPassword()));
            User user = getUserByIdentifier(identifier, userRepository);
            String token = provider.createToken(identifier, user.getId());

            Map<Object, Object> response = new HashMap<>() {{
                put("identifier", identifier);
                put("token", token);
            }};
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Некорректные данные", HttpStatus.FORBIDDEN);
        }
    }

    public static User getUserByIdentifier(String identifier, UserRepository userRepository) {
        return identifier.matches(EMAIL_REGEX)
                ? userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"))
                : userRepository.findByPhoneNumber(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
    }

}
