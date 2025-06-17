package com.tinashe.teamService.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tinashe.teamService.client.UserClient;
import com.tinashe.teamService.dto.TeamDTO;
import com.tinashe.teamService.dto.TeamMemberDTO;
import com.tinashe.teamService.dto.UserResponse;
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
    public Team createTeam(TeamDTO teamDTO, Long currentUserId) {
        Team team = teams.save(Team.builder()
                .name(teamDTO.getName())
                .description(teamDTO.getDescription())
                .build());

        // Set creator as OWNER
        members.save(TeamMember.builder()
                .team(team)
                .userId(currentUserId)
                .role("OWNER")
                .build());

        return team;
    }

    @Override
    public TeamMember addMember(Long teamId, TeamMemberDTO memberDTO, Long currentUserId, String currentUserRole) {
        Team team = getTeamOrThrow(teamId);

        validateOwnerAccess(currentUserId, teamId, currentUserRole, "Only OWNER can add members");

        if (members.existsByTeamIdAndUserId(teamId, memberDTO.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already a team member");
        }

        TeamMember member = TeamMember.builder()
                .userId(memberDTO.getUserId())
                .role(memberDTO.getRole().toUpperCase())
                .team(team)
                .build();

        TeamMember saved = members.save(member);
        sendNotification(memberDTO.getUserId(), "You have been added to team: " + team.getName());
        return saved;
    }

    @Override
    public TeamMember removeMember(Long teamId, Long userId, Long currentUserId, String currentUserRole) {
        validateOwnerAccess(currentUserId, teamId, currentUserRole, "Only OWNER can remove members");

        TeamMember member = members.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        members.delete(member);
        sendNotification(userId, "You have been removed from team: " + member.getTeam().getName());
        return member;
    }

    @Override
    public void deleteTeam(Long teamId, Long currentUserId, String currentUserRole) {
        validateOwnerAccess(currentUserId, teamId, currentUserRole, "Only OWNER can delete the team");
        members.deleteAll(members.findByTeamId(teamId));
        teams.deleteById(teamId);
    }

    @Override
    public List<TeamMember> listMembers(Long teamId) {
        if (!teams.existsById(teamId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }
        return members.findByTeamId(teamId);
    }

    @Override
    public boolean exists(Long teamId) {
        return teams.existsById(teamId);
    }
    @Override
    public Page<TeamMember> listMembers(Long teamId, Pageable pageable) {
        if (!teams.existsById(teamId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }
        return members.findByTeamId(teamId, pageable);
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

    private void sendNotification(Long userId, String message) {
    try {
        String token = userClient.getTokenForUser(userId);

        UserResponse user = userClient.getUserById(userId, token); 
        String email = user.getEmail();

        emailService.sendEmail(email, "Team Notification", message);
        log.info("Email sent to {}: {}", email, message);
    } catch (Exception e) {
        log.error("Failed to send email to user {}: {}", userId, e.getMessage());
    }
}

}
