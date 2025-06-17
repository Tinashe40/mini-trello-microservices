package com.tinashe.teamService.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tinashe.teamService.dto.TeamDTO;
import com.tinashe.teamService.dto.TeamMemberDTO;
import com.tinashe.teamService.model.Team;
import com.tinashe.teamService.model.TeamMember;
import com.tinashe.teamService.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor

public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a new team")
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamDTO dto,
                                           @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(dto, userId));
    }

    @PostMapping("/{teamId}/members")
    @Operation(summary = "Add a member to a team")
    public ResponseEntity<TeamMember> addMember(@PathVariable Long teamId,
                                                @Valid @RequestBody TeamMemberDTO memberDTO,
                                                @RequestHeader("X-User-Id") Long currentUserId,
                                                @RequestHeader("X-User-Role") String currentUserRole) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.addMember(teamId, memberDTO, currentUserId, currentUserRole));
    }

    @GetMapping("/{teamId}/exists")
    @Operation(summary = "Check if a team exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.exists(teamId));
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @DeleteMapping("/{teamId}/members/{userId}")
    @Operation(summary = "Remove a member from a team")
    public ResponseEntity<TeamMember> removeMember(@PathVariable Long teamId,
                                                   @PathVariable Long userId,
                                                   @RequestHeader("X-User-Id") Long currentUserId,
                                                   @RequestHeader("X-User-Role") String currentUserRole) {
        return ResponseEntity.ok(teamService.removeMember(teamId, userId, currentUserId, currentUserRole));
    }

    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    @DeleteMapping("/{teamId}")
    @Operation(summary = "Delete a team")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId,
                                           @RequestHeader("X-User-Id") Long currentUserId,
                                           @RequestHeader("X-User-Role") String currentUserRole) {
        teamService.deleteTeam(teamId, currentUserId, currentUserRole);
        return ResponseEntity.noContent().build();
    }
        @GetMapping("/{teamId}/members")
        @Operation(summary = "List all members of a team with pagination")
        public ResponseEntity<Page<TeamMember>> listMembers(
                            @PathVariable Long teamId,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(teamService.listMembers(teamId, pageable));
        }

}
