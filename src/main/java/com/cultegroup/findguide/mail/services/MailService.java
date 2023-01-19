package com.cultegroup.findguide.mail.services;

import com.cultegroup.findguide.data.model.User;
import com.cultegroup.findguide.mail.model.UpdateToken;
import com.cultegroup.findguide.mail.repo.UpdateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MailService {

    private final JavaMailSender sender;
    private final UpdateRepository passwordRepository;

    @Value("${spring.mail.username}")
    private String username;

    public MailService(JavaMailSender sender, UpdateRepository passwordRepository) {
        this.sender = sender;
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
        String code = user.getActivationCode();
        String text = "Для поддтверждения электронной почты перейдите по ссылке:\n"
                + "http://localhost:4200/activate/" + code;
        String subject = "Активируй впечатления!";

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
