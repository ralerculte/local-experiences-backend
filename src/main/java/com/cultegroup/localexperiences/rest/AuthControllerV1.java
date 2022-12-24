package com.cultegroup.localexperiences.rest;

import com.cultegroup.localexperiences.DTO.AuthRequestDTO;
import com.cultegroup.localexperiences.DTO.TokenDTO;
import com.cultegroup.localexperiences.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {

    private final AuthService service;

    public AuthControllerV1(AuthService service) {
        this.service = service;
    }

    @Transactional
    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequestDTO request) {
        return service.getAuthResponse(request);
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> registration(@RequestBody AuthRequestDTO request) {
        return service.register(request);
    }

    @Transactional
    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody TokenDTO dto) {
        return service.updateAccessToken(dto);
    }

    @Transactional
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenDTO dto) {
        return service.refresh(dto);
    }

    @PostMapping("/signout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
    }
}
