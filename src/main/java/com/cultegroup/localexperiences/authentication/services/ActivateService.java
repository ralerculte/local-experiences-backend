package com.cultegroup.localexperiences.authentication.services;

import com.cultegroup.localexperiences.authentication.DTO.ActivateDTO;
import com.cultegroup.localexperiences.authentication.exceptions.InvalidActivationToken;
import com.cultegroup.localexperiences.authentication.model.VerificationToken;
import com.cultegroup.localexperiences.authentication.repo.VerificationRepository;
import com.cultegroup.localexperiences.shared.model.Status;
import com.cultegroup.localexperiences.shared.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ActivateService {
    private final VerificationRepository verificationRepository;
    private final MailService mailService;

    public ActivateService(VerificationRepository verificationRepository, MailService mailService) {
        this.verificationRepository = verificationRepository;
        this.mailService = mailService;
    }

    public ResponseEntity<?> activate(ActivateDTO dto) {
        try {
            VerificationToken token = verificationRepository.findByToken(dto.getToken())
                    .orElseThrow(() -> new InvalidActivationToken("Невалидный verification token"));

            User user = token.getUser();
            if (token.getDateExpiration().isBefore(LocalDateTime.now())) {
                verificationRepository.delete(token);
                sendActivationMessage(user);
                return new ResponseEntity<>("Истекло время действия ссылки", HttpStatus.GONE);
            }

            user.setStatus(Status.ACTIVE);
            verificationRepository.delete(token);
            Map<String, String> response = new HashMap<>() {{
                put("email", user.getEmail());
            }};
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка верификации: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public void sendActivationMessage(User user) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            mailService.sendMail(user);
        }
    }

}
