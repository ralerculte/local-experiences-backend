package com.cultegroup.findguide.data.services;

import com.amazonaws.services.s3.AmazonS3;
import com.cultegroup.findguide.authentication.security.SecurityUser;
import com.cultegroup.findguide.data.DTO.PasswordDTO;
import com.cultegroup.findguide.data.DTO.UpdatePasswordDTO;
import com.cultegroup.findguide.data.exceptions.InvalidUpdateToken;
import com.cultegroup.findguide.data.exceptions.SecurityException;
import com.cultegroup.findguide.data.exceptions.UserNotFoundException;
import com.cultegroup.findguide.data.exceptions.WrongFieldException;
import com.cultegroup.findguide.data.model.User;
import com.cultegroup.findguide.data.repo.UserRepository;
import com.cultegroup.findguide.mail.model.UpdateToken;
import com.cultegroup.findguide.mail.repo.UpdateRepository;
import com.cultegroup.findguide.mail.services.MailService;
import com.cultegroup.findguide.shared.exceptions.HttpStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private static final String TMP_DIR = "src/main/java/com/cultegroup/findguide/tmp";
    private static final String FOLDER = "profile-images/";

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
    private final AmazonS3 s3;

    @Value("${s3.bucket.name}")
    private String BUCKET;

    public UserService(UserRepository repository,
                       UpdateRepository updateRepository,
                       MailService mailService,
                       BCryptPasswordEncoder encoder, AmazonS3 s3Client) {
        this.repository = repository;
        this.updateRepository = updateRepository;
        this.mailService = mailService;
        this.encoder = encoder;
        this.s3 = s3Client;
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
            User user = checkAndGetUser(id);
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
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> updatePassword(Long id, UpdatePasswordDTO dto) {
        try {
            UpdateToken token = updateRepository.findByToken(dto.token()).orElseThrow(
                    () -> new InvalidUpdateToken("Невалидный токен обновления.", HttpStatus.BAD_REQUEST)
            );

            User user = checkAndGetUser(id);
            if (token.getDateExpiration().isBefore(LocalDateTime.now())) {
                mailService.sendPasswordUpdateMessage(user);
                updateRepository.delete(token);
                return new ResponseEntity<>("Истекло время действия ссылки", HttpStatus.GONE);
            }

            updateRepository.delete(token);
            user.setPassword(encoder.encode(dto.password()));

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
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
            User user = checkAndGetUser(id);
            if (!encoder.matches(dto.password(), user.getPassword())) {
                throw new SecurityException("Неверный пароль.", HttpStatus.BAD_REQUEST);
            }

            mailService.sendPasswordUpdateMessage(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> delete(Long id) {
        try {
            User user = checkAndGetUser(id);
            delete(user.getId());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    public ResponseEntity<?> uploadProfileImage(Long id, MultipartFile image) {
        try {
            User user = checkAndGetUser(id);

            s3.putObject(BUCKET, FOLDER + id, convert(image, id));
            user.setAvatarLink(id.toString());
            Files.delete(Path.of(TMP_DIR + id));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteProfileImage(Long id) {
        try {
            User user = checkAndGetUser(id);
            if (user.getAvatarLink() != null) {
                s3.deleteObject(BUCKET, FOLDER + id);
                user.setAvatarLink(null);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HttpStatusException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
    }

    private User checkAndGetUser(Long id) {
        User user = repository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Данного пользователя не существует.", HttpStatus.NOT_FOUND)
        );
        User current = ((SecurityUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .user();

        if (!current.getId().equals(user.getId())) {
            throw new SecurityException("Изменять можно только свою учётную запись", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private static File convert(MultipartFile image, long id) {
        File file = new File(TMP_DIR + id);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(image.getBytes());
        } catch (IOException e) {
            throw new HttpStatusException("Проблема с загрузкой файла.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return file;
    }
}
