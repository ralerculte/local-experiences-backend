package com.cultegroup.localexperiences.mail.services;

import com.cultegroup.localexperiences.mail.model.UpdateToken;
import com.cultegroup.localexperiences.mail.model.VerificationToken;
import com.cultegroup.localexperiences.mail.repo.UpdateRepository;
import com.cultegroup.localexperiences.mail.repo.VerificationRepository;
import com.cultegroup.localexperiences.data.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MailService {

    private final JavaMailSender sender;
    private final VerificationRepository verificationRepository;
    private final UpdateRepository passwordRepository;

    @Value("${spring.mail.username}")
    private String username;

    public MailService(JavaMailSender sender, VerificationRepository verificationRepository, UpdateRepository passwordRepository) {
        this.sender = sender;
        this.verificationRepository = verificationRepository;
        this.passwordRepository = passwordRepository;
    }

    public void sendPasswordUpdateMessage(User user) {
        String token = UUID.randomUUID().toString();
        String text = "Для обновления пароля перейдите по ссылке:\n"
                + "http://localhost:4200/update/password/" + token;
        String subject = "Изменение учётной записи.";

        LocalDateTime dateExpiration = LocalDateTime.now().plusHours(1L);
        passwordRepository.save(new UpdateToken(token, user, dateExpiration));

        sendMail(user.getEmail(), subject, text);
    }

    public void sendVerificationMessage(User user) {
        String token = UUID.randomUUID().toString();
        String text = "Для поддтверждения электронной почты перейдите по ссылке:\n"
                + "http://localhost:4200/activate/" + token;
        String subject = "Активируй впечатления!";

        LocalDateTime dateExpiration = LocalDateTime.now().plusDays(1L);
        verificationRepository.save(new VerificationToken(token, user, dateExpiration));

        sendMail(user.getEmail(), subject, text);
    }

    private void sendMail(String to, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        sender.send(mailMessage);
    }
}
