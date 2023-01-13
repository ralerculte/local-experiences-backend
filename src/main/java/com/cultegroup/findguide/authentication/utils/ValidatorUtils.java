package com.cultegroup.findguide.authentication.utils;

import com.cultegroup.findguide.data.repo.UserRepository;
import com.cultegroup.findguide.data.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ValidatorUtils {

    private final UserRepository userRepository;

    public ValidatorUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь с такой почтой не найден!")
        );
    }

}
