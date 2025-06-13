package com.tinashe.projectService.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tinashe.projectService.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {}
