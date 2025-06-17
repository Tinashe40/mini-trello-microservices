package com.tinashe.teamService.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tinashe.teamService.client.UserClient;
import com.tinashe.teamService.dto.TeamDTO;
import com.tinashe.teamService.dto.TeamMemberDTO;
import com.tinashe.teamService.dto.UserDTO;
import com.tinashe.teamService.model.Team;
import com.tinashe.teamService.model.TeamMember;
import com.tinashe.teamService.repository.TeamMemberRepository;
import com.tinashe.teamService.repository.TeamRepository;
import com.tinashe.teamService.service.EmailService;
import com.tinashe.teamService.service.TeamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teams;
    private final TeamMemberRepository members;
    private final UserClient userClient;
    private final EmailService emailService;

    @Override
    public Team createTeam(TeamDTO dto, Long currentUserId) {
        Team team = teams.save(Team.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .build());

        members.save(TeamMember.builder()
            .team(team)
            .userId(currentUserId)
            .role("OWNER")
            .build());

        return team;
    }

    @Override
    public TeamMember addMember(Long teamId, TeamMemberDTO dto, Long currentUserId, String currentUserRole) {
        Team team = getTeamOrThrow(teamId);
        validateOwnerAccess(currentUserId, teamId, currentUserRole, "Only OWNER can add members");

        if (members.existsByTeamIdAndUserId(teamId, dto.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a member");
        }

        TeamMember newMember = TeamMember.builder()
            .userId(dto.getUserId())
            .role(dto.getRole().toUpperCase())
            .team(team)
            .build();

        TeamMember saved = members.save(newMember);
        notifyUser(dto.getUserId(), "You have been added to team: " + team.getName());
        return saved;
    }

    @Override
    public TeamMember removeMember(Long teamId, Long userId, Long currentUserId, String currentUserRole) {
        validateOwnerAccess(currentUserId, teamId, currentUserRole, "Only OWNER can remove members");

        TeamMember member = members.findByTeamIdAndUserId(teamId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        members.delete(member);
        notifyUser(userId, "You have been removed from team: " + member.getTeam().getName());
        return member;
    }

    @Override
    public void deleteTeam(Long teamId, Long currentUserId, String currentUserRole) {
        validateOwnerAccess(currentUserId, teamId, currentUserRole, "Only OWNER can delete the team");

        members.deleteAll(members.findByTeamId(teamId));
        teams.deleteById(teamId);
    }

    @Override
    public Page<TeamMember> listMembers(Long teamId, Pageable pageable) {
        if (!teams.existsById(teamId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }
        return members.findByTeamId(teamId, pageable);
    }

    @Override
    public boolean exists(Long teamId) {
        return teams.existsById(teamId);
    }

    private Team getTeamOrThrow(Long teamId) {
        return teams.findById(teamId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
    }

    private void validateOwnerAccess(Long userId, Long teamId, String role, String message) {
        if (!"OWNER".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }

        boolean isOwner = members.findByTeamIdAndUserId(teamId, userId)
            .map(m -> "OWNER".equalsIgnoreCase(m.getRole()))
            .orElse(false);

        if (!isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You must be the OWNER of this team");
        }
    }

    private void notifyUser(Long userId, String message) {
        try {
            // Simulate token or replace with header logic if needed
            String dummyToken = "Bearer INTERNAL_SERVICE_CALL";
            UserDTO user = userClient.find(userId, dummyToken);

            if (user != null && user.getEmail() != null) {
                emailService.sendEmail(user.getEmail(), "Team Notification", message);
                log.info("Notification email sent to {}", user.getEmail());
            } else {
                log.warn("User not found or email is null for ID {}", userId);
            }

        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }
}
