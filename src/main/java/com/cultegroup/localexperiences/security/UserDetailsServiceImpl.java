package com.cultegroup.localexperiences.security;

import com.cultegroup.localexperiences.model.User;
import com.cultegroup.localexperiences.utils.ValidatorUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ValidatorUtils validatorUtils;

    public UserDetailsServiceImpl(ValidatorUtils validatorUtils) {
        this.validatorUtils = validatorUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = validatorUtils.getUserByEmail(email);
        return new SecurityUser(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPassword()
        );
    }
}
