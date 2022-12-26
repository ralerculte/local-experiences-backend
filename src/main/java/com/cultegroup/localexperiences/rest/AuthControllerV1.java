package com.cultegroup.localexperiences.rest;

import com.cultegroup.localexperiences.DTO.UserInfoDTO;
import com.cultegroup.localexperiences.DTO.TokenDTO;
import com.cultegroup.localexperiences.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication controller",
        description = "Контроллер, обрабатывающй запросы, связанные с регистрацией, авторизацией, обновлением access/refresh токенов.")
public class AuthControllerV1 {

    private final AuthService service;

    public AuthControllerV1(AuthService service) {
        this.service = service;
    }

    @Transactional
    @PostMapping("/signin")
    @Operation(summary = "Авторизация пользователя. В теле ответа возвращаются access и refresh токены.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Невалидные данные.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "200", description = "Успешная авторизация.", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<?> authenticate(
            @Parameter(description = "DTO, содержащее в себе идентификатор и пароль.")
            @RequestBody UserInfoDTO request) {
        return service.getAuthResponse(request);
    }

    @Transactional
    @PostMapping("/signup")
    @Operation(summary = "Регистрация пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Аккаунт уже создан.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "201", description = "Аккаунт создан.", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<?> registration(
            @Parameter(description = "DTO, содержащее в себе идентификатор и пароль.")
            @RequestBody UserInfoDTO request) {
        return service.register(request);
    }

    @Transactional
    @PostMapping("/update")
    @Operation(summary = "Обновление access токена. В теле ответа возвращается access токен.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Невалидный refresh токен.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "200", description = "Успешное обновление access токена.", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<?> update(
            @Parameter(description = "DTO, содержащее в себе токены.")
            @RequestBody TokenDTO dto) {
        return service.updateAccessToken(dto);
    }

    @Transactional
    @PostMapping("/refresh")
    @Operation(summary = "Получение новых refresh & access токенов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Невалидные токены.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "200", description = "Успешное обновление токенов.", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<?> refresh(
            @Parameter(description = "DTO, содержащее в себе токены.")
            @RequestBody TokenDTO dto) {
        return service.refresh(dto);
    }

    @PostMapping("/signout")
    @Operation(summary = "Выход из учётной записи.")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
    }
}
