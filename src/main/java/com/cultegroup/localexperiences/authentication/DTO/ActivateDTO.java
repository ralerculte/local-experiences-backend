package com.cultegroup.localexperiences.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее UUID-токен для верификации аккаунта.")
public class ActivateDTO {

    @Schema(name = "Токен верификации.")
    private String token;

    public String getToken() {
        return token;
    }

}
