package com.cultegroup.localexperiences.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее данные пользователя.")
public class UserInfoDTO {

    @Schema(name = "Идентификатор пользователя. [На данный момент только email]")
    private String email;
    @Schema(name = "Пароль пользователя.")
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
