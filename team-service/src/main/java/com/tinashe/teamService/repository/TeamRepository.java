package com.tinashe.teamService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tinashe.teamService.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {}
