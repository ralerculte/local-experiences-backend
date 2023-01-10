package com.cultegroup.localexperiences.authentication.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DTO, содержащее адрес электронной почты.")
public record EmailDTO(@Schema(name = "Адрес электронной почты.") String email) {
}
