package com.cultegroup.localexperiences.rest;

import com.cultegroup.localexperiences.DTO.ActivateDTO;
import com.cultegroup.localexperiences.services.ActivateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/activate")
public class ActivateControllerV1 {

    private final ActivateService service;

    public ActivateControllerV1(ActivateService service) {
        this.service = service;
    }

    @Transactional
    @PostMapping()
    public ResponseEntity<?> activate(@RequestBody ActivateDTO activate) {
        return service.activate(activate);
    }

}
