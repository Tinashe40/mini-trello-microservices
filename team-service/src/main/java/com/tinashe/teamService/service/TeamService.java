package com.tinashe.teamService.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tinashe.teamService.dto.TeamDTO;
import com.tinashe.teamService.dto.TeamMemberDTO;
import com.tinashe.teamService.model.Team;
import com.tinashe.teamService.model.TeamMember;

public interface TeamService {
    Team createTeam(TeamDTO teamDTO, Long currentUserId);
    TeamMember addMember(Long teamId, TeamMemberDTO memberDTO, Long currentUserId, String currentUserRole);
    TeamMember removeMember(Long teamId, Long userId, Long currentUserId, String currentUserRole);
    void deleteTeam(Long teamId, Long currentUserId, String currentUserRole);
    boolean exists(Long teamId);
    Page<TeamMember> listMembers(Long teamId, Pageable pageable);

}
