package com.cultegroup.localexperiences.rest;

import com.cultegroup.localexperiences.DTO.ActivateDTO;
import com.cultegroup.localexperiences.services.ActivateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/activate")
@Tag(name = "Activate controller",
        description = "Контроллер, обрабатывающий запросы, связанные с активацией пользователей по email.")
@CrossOrigin(origins = "http://localhost:4200")
public class ActivateControllerV1 {

    private final ActivateService service;

    public ActivateControllerV1(ActivateService service) {
        this.service = service;
    }

    @PostMapping 
    @Transactional
    @Operation(summary = "Обработка активации пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Ошибка верификации.", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "200", description = "Успешная верификация аккаунта.", content = {@Content(schema = @Schema())})
    })
    public ResponseEntity<?> activate(
            @Parameter(description = "DTO, содержащее в себе токен верификации.")
            @RequestBody ActivateDTO activate) {
        return service.activate(activate);
    }

}
