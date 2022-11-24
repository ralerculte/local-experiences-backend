package com.cultegroup.localexperience.repo;

import com.cultegroup.localexperience.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
