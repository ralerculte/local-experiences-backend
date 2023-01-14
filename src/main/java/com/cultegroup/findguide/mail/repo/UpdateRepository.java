package com.cultegroup.findguide.mail.repo;

import com.cultegroup.findguide.mail.model.UpdateToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpdateRepository extends JpaRepository<UpdateToken, Long> {
    Optional<UpdateToken> findByToken(String token);
}
