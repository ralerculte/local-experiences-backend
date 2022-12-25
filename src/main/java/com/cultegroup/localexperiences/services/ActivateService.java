package com.cultegroup.localexperiences.services;

import com.cultegroup.localexperiences.DTO.ActivateDTO;
import com.cultegroup.localexperiences.exceptions.InvalidActivationToken;
import com.cultegroup.localexperiences.model.Status;
import com.cultegroup.localexperiences.model.User;
import com.cultegroup.localexperiences.model.VerificationToken;
import com.cultegroup.localexperiences.repo.UserRepository;
import com.cultegroup.localexperiences.repo.VerificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ActivateService {

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final MailService mailService;

    public ActivateService(UserRepository userRepository, VerificationRepository verificationRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.mailService = mailService;
    }

    public ResponseEntity<?> activate(ActivateDTO dto) {
        try {
            VerificationToken token = verificationRepository.findByToken(dto.getToken())
                    .orElseThrow(() -> new InvalidActivationToken("Невалидный verification token"));

            User user = userRepository.findById(token.getUser().getId())
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователя не существует"));
            user.setStatus(Status.ACTIVE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка верификации: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    public void sendActivationMessage(User user) {
        // TODO ADD ACTIVATION BY PHONE NUMBER
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            mailService.sendMail(user);
        }
    }

}
