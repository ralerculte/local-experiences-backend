package com.cultegroup.localexperiences.authentication.services;

import com.cultegroup.localexperiences.authentication.model.VerificationToken;
import com.cultegroup.localexperiences.authentication.repo.VerificationRepository;
import com.cultegroup.localexperiences.shared.model.User;
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

    @Value("${spring.mail.username}")
    private String username;

    public MailService(JavaMailSender sender, VerificationRepository verificationRepository) {
        this.sender = sender;
        this.verificationRepository = verificationRepository;
    }

    public void sendMail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        String token = UUID.randomUUID().toString();
        String text = "Для вступления в культ перейди по ссылке:\n"
                + "http://localhost:4200/activate/" + token;
        String subject = "Активируй впечатления!";

        LocalDateTime dateExpiration = LocalDateTime.now().plusDays(1L);
        verificationRepository.save(new VerificationToken(token, user, dateExpiration));

        mailMessage.setFrom(username);
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        sender.send(mailMessage);
    }
}
