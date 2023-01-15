package com.cultegroup.findguide.data.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@Transactional
@RestController
@RequestMapping("/api/v1/experiences")
@CrossOrigin(origins = "http://localhost:4200")
public class ExperienceControllerV1 {

}
