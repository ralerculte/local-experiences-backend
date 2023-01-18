package com.cultegroup.findguide.data.rest;

import com.cultegroup.findguide.data.services.ExperienceService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Transactional
@RestController
@RequestMapping("/api/v1/experiences")
@CrossOrigin(origins = "http://localhost:4200")
public class ExperienceControllerV1 {

    private final ExperienceService service;

    public ExperienceControllerV1(ExperienceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> save() {
        return null;
    }
}
