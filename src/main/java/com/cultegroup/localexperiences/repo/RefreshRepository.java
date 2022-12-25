package com.cultegroup.localexperiences.repo;

import com.cultegroup.localexperiences.model.RefreshToken;
import com.cultegroup.localexperiences.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
}
