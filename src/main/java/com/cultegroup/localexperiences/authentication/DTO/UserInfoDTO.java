package com.cultegroup.localexperiences.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее данные пользователя.")
public record UserInfoDTO(
        @Schema(name = "Идентификатор пользователя. [На данный момент только email]") String email,
        @Schema(name = "Пароль пользователя.") String password
) {
    public UserInfoDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
