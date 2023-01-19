package com.cultegroup.findguide.data.repo;

import com.cultegroup.findguide.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByActivationCode(String code);
    boolean existsByEmail(String email);
}
