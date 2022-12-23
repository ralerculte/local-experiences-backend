package com.cultegroup.localexperiences.utils;

import com.cultegroup.localexperiences.model.User;
import com.cultegroup.localexperiences.repo.UserRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ValidatorUtils {

    private static final String EMAIL_REGEX = "^(.+)@(.+)$";

    private final UserRepository userRepository;

    public ValidatorUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByIdentifier(String identifier) {
        return identifier.matches(EMAIL_REGEX)
                ? userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с такой почтой не найден!"))
                : userRepository.findByPhoneNumber(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким номером телефона не найден!"));
    }

    public boolean isEmail(String identifier) {
        return identifier.matches(EMAIL_REGEX);
    }

}
