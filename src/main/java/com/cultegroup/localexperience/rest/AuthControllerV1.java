package com.cultegroup.localexperience.rest;

import com.cultegroup.localexperience.services.AuthService;
import com.cultegroup.localexperience.utils.AuthRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/signout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
    }

    @Transactional
    @PostMapping("/signup")
    public ResponseEntity<?> registration(@RequestBody AuthRequestDTO request) {
        return service.register(request);
    }

    @GetMapping("/activate/{id}")
    public String activate(@PathVariable Long id) {
        // TODO redirect if mistake
        service.activate(id);
        return "redirect:/activated";
    }

}
