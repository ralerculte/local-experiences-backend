package com.cultegroup.findguide.authentication.services;

import com.cultegroup.findguide.authentication.DTO.ActivateDTO;
import com.cultegroup.findguide.authentication.DTO.EmailDTO;
import com.cultegroup.findguide.authentication.exceptions.InvalidActivationToken;
import com.cultegroup.findguide.mail.model.VerificationToken;
import com.cultegroup.findguide.mail.repo.VerificationRepository;
import com.cultegroup.findguide.data.model.enums.Status;
import com.cultegroup.findguide.data.model.User;
import com.cultegroup.findguide.mail.services.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
            VerificationToken token = verificationRepository.findByToken(dto.token())
                    .orElseThrow(() -> new InvalidActivationToken("Невалидный verification token"));

            User user = token.getUser();
            if (token.getDateExpiration().isBefore(LocalDateTime.now())) {
                verificationRepository.delete(token);
                sendActivationMessage(user);
                return new ResponseEntity<>("Истекло время действия ссылки", HttpStatus.GONE);
            }

            user.setStatus(Status.ACTIVE);
            verificationRepository.delete(token);

            return new ResponseEntity<>(new EmailDTO(user.getEmail()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка верификации: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public void sendActivationMessage(User user) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            mailService.sendVerificationMessage(user);
        }
    }

}
