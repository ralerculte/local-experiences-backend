package com.cultegroup.localexperiences.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее UUID-токен для верификации аккаунта.")
public record ActivateDTO(@Schema(name = "Токен верификации.") String token) {
}
