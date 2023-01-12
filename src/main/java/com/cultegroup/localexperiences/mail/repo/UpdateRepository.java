package com.cultegroup.localexperiences.mail.repo;

import com.cultegroup.localexperiences.mail.model.UpdateToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpdateRepository extends JpaRepository<UpdateToken, Long> {
    Optional<UpdateToken> findByToken(String token);
}
