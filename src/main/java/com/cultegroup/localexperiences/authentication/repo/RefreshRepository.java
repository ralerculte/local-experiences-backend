package com.cultegroup.localexperiences.authentication.repo;

import com.cultegroup.localexperiences.authentication.model.RefreshToken;
import com.cultegroup.localexperiences.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
}
