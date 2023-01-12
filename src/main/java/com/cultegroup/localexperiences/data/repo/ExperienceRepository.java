package com.cultegroup.localexperiences.data.repo;

import com.cultegroup.localexperiences.data.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
}
