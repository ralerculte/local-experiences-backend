package com.cultegroup.localexperiences.data.services;

import com.cultegroup.localexperiences.authentication.DTO.EmailDTO;
import com.cultegroup.localexperiences.authentication.security.SecurityUser;
import com.cultegroup.localexperiences.data.DTO.PasswordDTO;
import com.cultegroup.localexperiences.data.DTO.UpdatePasswordDTO;
import com.cultegroup.localexperiences.data.exceptions.InvalidUpdateToken;
import com.cultegroup.localexperiences.data.exceptions.SecurityException;
import com.cultegroup.localexperiences.data.exceptions.UserNotFoundException;
import com.cultegroup.localexperiences.data.exceptions.WrongFieldException;
import com.cultegroup.localexperiences.data.model.User;
import com.cultegroup.localexperiences.data.repo.UserRepository;
import com.cultegroup.localexperiences.mail.model.UpdateToken;
import com.cultegroup.localexperiences.mail.repo.UpdateRepository;
import com.cultegroup.localexperiences.mail.services.MailService;
import com.cultegroup.localexperiences.shared.exceptions.HttpStatusException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final List<String> DISABLED_FIELDS = new ArrayList<>(List.of(
            "id",
            "password",
            "avatarLink",
            "status",
            "experiences",
            "events"
    ));

    private final UserRepository repository;
    private final UpdateRepository updateRepository;
    private final MailService mailService;
    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository repository,
                       UpdateRepository updateRepository,
                       MailService mailService,
                       BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.updateRepository = updateRepository;
        this.mailService = mailService;
        this.encoder = encoder;
    }

    public ResponseEntity<?> getUser(Long id) {
        try {
            User user = repository.findById(id).orElseThrow(
                    () -> new UserNotFoundException("Данного пользователя не существует.", HttpStatus.NOT_FOUND)
            );
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> patchUser(Long id, Map<String, String> fields) {
        try {
            User user = repository.findById(id).orElseThrow(
                    () -> new UserNotFoundException("Данного пользователя не существует.", HttpStatus.NOT_FOUND)
            );
            checkCurrentUser(user);

            for (String fieldName : fields.keySet()) {
                Field field = ReflectionUtils.findField(User.class, fieldName);
                if (field == null || DISABLED_FIELDS.contains(fieldName)) {
                    throw new WrongFieldException(
                            "Поле {" + fieldName + "} недоступно для обновления.",
                            HttpStatus.BAD_REQUEST
                    );
                }
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, fields.get(fieldName));
            }
            return ResponseEntity.ok(HttpEntity.EMPTY);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> updatePassword(Long id, UpdatePasswordDTO dto) {
        try {
            UpdateToken token = updateRepository.findByToken(dto.token()).orElseThrow(
                    () -> new InvalidUpdateToken("Невалидный токен обновления.", HttpStatus.BAD_REQUEST)
            );

            User user = repository.findById(id).orElseThrow(
                    () -> new UserNotFoundException("Данного пользователя не существует.", HttpStatus.NOT_FOUND)
            );
            checkCurrentUser(user);

            if (token.getDateExpiration().isBefore(LocalDateTime.now())) {
                mailService.sendPasswordUpdateMessage(user);
                updateRepository.delete(token);
                return new ResponseEntity<>("Истекло время действия ссылки", HttpStatus.GONE);
            }

            updateRepository.delete(token);
            user.setPassword(encoder.encode(dto.password()));

            return new ResponseEntity<>(new EmailDTO(user.getEmail()), HttpStatus.ACCEPTED);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> getEvents(Long id) {
        try {
            User user = repository.findById(id).orElseThrow(
                    () -> new UserNotFoundException("Данного пользователя не существует.", HttpStatus.NOT_FOUND)
            );

            return new ResponseEntity<>(user.getEvents(), HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> getExperiences(Long id) {
        try {
            User user = repository.findById(id).orElseThrow(
                    () -> new UserNotFoundException("Данного пользователя не существует.", HttpStatus.NOT_FOUND)
            );

            return new ResponseEntity<>(user.getExperiences(), HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> sendUpdateMessage(Long id, PasswordDTO dto) {
        try {
            User user = repository.findById(id).orElseThrow(
                    () -> new UserNotFoundException("Данного пользователя не существует.", HttpStatus.NOT_FOUND)
            );
            checkCurrentUser(user);

            if (!encoder.matches(dto.password(), user.getPassword())) {
                throw new SecurityException("Неверный пароль.", HttpStatus.BAD_REQUEST);
            }

            mailService.sendPasswordUpdateMessage(user);
            return ResponseEntity.ok(HttpEntity.EMPTY);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    private static void checkCurrentUser(User user) {
        User current = ((SecurityUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .user();
        if (!current.getId().equals(user.getId())) {
            throw new SecurityException("Изменять можно только свою учётную запись", HttpStatus.FORBIDDEN);
        }
    }
}
