package com.cultegroup.localexperiences.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее access & refresh токены.")
public class TokenDTO {

    @Schema(name = "Access токен.")
    private String accessToken;
    @Schema(name = "Refresh токен.")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
