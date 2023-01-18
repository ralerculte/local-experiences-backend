package com.cultegroup.findguide.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее id, access & refresh токены.")
public record AuthResponseDTO(
        @Schema(name = "Id пользователя.") Long id,
        @Schema(name = "Access токен.") String accessToken,
        @Schema(name = "Refresh токен.") String refreshToken
) {
}
