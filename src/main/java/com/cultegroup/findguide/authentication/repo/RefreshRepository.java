package com.cultegroup.findguide.authentication.repo;

import com.cultegroup.findguide.authentication.model.RefreshToken;
import com.cultegroup.findguide.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
}
