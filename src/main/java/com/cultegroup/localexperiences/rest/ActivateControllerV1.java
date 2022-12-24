package com.cultegroup.localexperiences.rest;

import com.cultegroup.localexperiences.DTO.ActivateDTO;
import com.cultegroup.localexperiences.services.ActivateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/activate")
@Tag(name = "Activate controller",
        description = "Контроллер, обрабатывающий запросы, связанные с активацией пользователей по email.")
public class ActivateControllerV1 {

    private final ActivateService service;

    public ActivateControllerV1(ActivateService service) {
        this.service = service;
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Обработка активации пользователя.")
    public ResponseEntity<?> activate(
            @Parameter(description = "DTO, содержащее в себе токен верификации.")
            @RequestBody ActivateDTO activate) {
        return service.activate(activate);
    }

}
