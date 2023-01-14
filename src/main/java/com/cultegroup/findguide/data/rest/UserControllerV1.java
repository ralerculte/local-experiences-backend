package com.cultegroup.findguide.data.rest;

import com.cultegroup.findguide.data.DTO.PasswordDTO;
import com.cultegroup.findguide.data.DTO.UpdatePasswordDTO;
import com.cultegroup.findguide.data.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Map;

@Transactional
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserControllerV1 {

    private final UserService service;

    public UserControllerV1(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @GetMapping("{id}/events")
    public ResponseEntity<?> getEvents(@PathVariable Long id) {
        return service.getEvents(id);
    }

    @GetMapping("{id}/experiences")
    public ResponseEntity<?> getExperiences(@PathVariable Long id) {
        return service.getExperiences(id);
    }

    @PostMapping("/{id}/file")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable Long id,
            @RequestPart MultipartFile profileImage
    ) {
        return service.uploadProfileImage(id, profileImage);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<?> requestUpdatePassword(
            @PathVariable Long id,
            @RequestBody PasswordDTO dto
    ) {
        return service.sendUpdateMessage(id, dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> fields
    ) {
        return service.patchUser(id, fields);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long id,
            @RequestBody UpdatePasswordDTO dto
    ) {
        return service.updatePassword(id, dto);
    }

    @DeleteMapping("/{id}/file")
    public ResponseEntity<?> deleteProfileImage(@PathVariable Long id) {
        return service.deleteProfileImage(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return service.delete(id);
    }
}
