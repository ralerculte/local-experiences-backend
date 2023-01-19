package com.cultegroup.findguide.authentication.services;

import com.cultegroup.findguide.authentication.DTO.ActivateDTO;
import com.cultegroup.findguide.authentication.DTO.EmailDTO;
import com.cultegroup.findguide.authentication.exceptions.InvalidActivationToken;
import com.cultegroup.findguide.authentication.repo.RedisRepo;
import com.cultegroup.findguide.data.model.User;
import com.cultegroup.findguide.data.repo.UserRepository;
import com.cultegroup.findguide.mail.services.MailService;
import com.cultegroup.findguide.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class ActivateService {
    private static final Duration LINK_TIMEOUT = Duration.ofDays(1L);

    private final UserRepository userRepo;
    private final RedisRepo redisRepo;
    private final MailService mailService;

    public ActivateService(UserRepository userRepo, RedisRepo redisRepo, MailService mailService) {
        this.userRepo = userRepo;
        this.redisRepo = redisRepo;
        this.mailService = mailService;
    }

    public ResponseEntity<?> activate(ActivateDTO dto) {
        try {
            User user = userRepo.findByActivationCode(dto.code())
                    .orElseThrow(() -> new InvalidActivationToken(
                            "Невалидный код верификации.",
                            HttpStatus.NOT_FOUND
                    ));

            if (redisRepo.get(dto.code()) == null) {
                user.setActivationCode(UUID.randomUUID().toString());
                sendActivationMessage(user);
                return new ResponseEntity<>("Истекло время действия ссылки", HttpStatus.GONE);
            }

            redisRepo.delete(user.getActivationCode());
            user.setActivationCode(null);
            return new ResponseEntity<>(new EmailDTO(user.getEmail()), HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public void sendActivationMessage(User user) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            mailService.sendVerificationMessage(user);
            redisRepo.set(user.getActivationCode(), user.getEmail(), LINK_TIMEOUT);
        }
    }

}
