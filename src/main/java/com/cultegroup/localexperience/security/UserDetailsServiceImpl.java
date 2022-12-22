package com.cultegroup.localexperience.security;

import com.cultegroup.localexperience.model.User;
import com.cultegroup.localexperience.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String EMAIL_REGEX = "^(.+)@(.+)$";
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = getUserByIdentifier(identifier);
        return new SecurityUser(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPassword()
        );
    }

    private User getUserByIdentifier(String identifier) {
        return identifier.matches(EMAIL_REGEX)
                ? userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с такой почтой не найден!"))
                : userRepository.findByPhoneNumber(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с таким номером телефона не найден!"));
    }
}
