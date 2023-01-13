package com.cultegroup.findguide.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее email, access & refresh токены.")
public record TokensDTO(
        @Schema(name = "Id пользователя.") Long id,
        @Schema(name = "Access токен.") String accessToken,
        @Schema(name = "Refresh токен.") String refreshToken
) {

    public TokensDTO(Long id, String accessToken, String refreshToken) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
