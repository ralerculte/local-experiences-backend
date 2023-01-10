package com.cultegroup.localexperiences.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее email, access & refresh токены.")
public record TokensDTO(
        @Schema(name = "Адрес электронной почты.") String email,
        @Schema(name = "Access токен.") String accessToken,
        @Schema(name = "Refresh токен.") String refreshToken
) {

    public TokensDTO(String email, String accessToken, String refreshToken) {
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
