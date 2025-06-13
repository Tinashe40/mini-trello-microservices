package com.tinashe.projectService.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tinashe.projectService.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerUsername(String ownerUsername);
    Page<Project> findByNameContainingIgnoreCaseOrOwnerUsernameContainingIgnoreCase(
    String name, String ownerUsername, PageRequest pageRequest);

    @Query("SELECT p FROM Project p JOIN p.teams t WHERE t.id = :teamId")
    Page<Project> findByTeamId(Long teamId, PageRequest pageRequest);


}

