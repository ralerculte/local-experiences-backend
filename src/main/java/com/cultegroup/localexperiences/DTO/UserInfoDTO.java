package com.cultegroup.localexperiences.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее данные пользователя.")
public class UserInfoDTO {

    @Schema(name = "Идентификатор пользователя. [На данный момент только email]")
    private String identifier;
    @Schema(name = "Пароль пользователя.")
    private String password;

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }
}
