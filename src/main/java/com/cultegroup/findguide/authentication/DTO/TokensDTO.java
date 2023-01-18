package com.cultegroup.findguide.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее в себе access & refresh токены.")
public record TokensDTO(
        @Schema(name = "Access токен.") String accessToken,
        @Schema(name = "Refresh токен.") String refreshToken
) {
}
