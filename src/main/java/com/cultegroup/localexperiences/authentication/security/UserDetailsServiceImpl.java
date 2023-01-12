package com.cultegroup.localexperiences.authentication.security;

import com.cultegroup.localexperiences.authentication.utils.ValidatorUtils;
import com.cultegroup.localexperiences.data.model.User;
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
        return new SecurityUser(user);
    }
}
