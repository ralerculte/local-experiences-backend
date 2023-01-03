package com.cultegroup.localexperiences.utils;

import com.cultegroup.localexperiences.model.User;
import com.cultegroup.localexperiences.repo.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ValidatorUtils {

    private final UserRepository userRepository;

    public ValidatorUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с такой почтой не найден!"));
    }

}
