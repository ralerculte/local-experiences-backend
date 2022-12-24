package com.cultegroup.localexperiences.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Статус активации аккаунта.")
public enum Status {
    INACTIVE,
    ACTIVE
}
