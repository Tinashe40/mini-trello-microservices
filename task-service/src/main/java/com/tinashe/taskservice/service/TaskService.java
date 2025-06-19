package com.tinashe.taskservice.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.tinashe.taskservice.dto.TaskCreateDTO;
import com.tinashe.taskservice.dto.TaskDTO;
import com.tinashe.taskservice.dto.TaskUpdateDTO;
import com.tinashe.taskservice.dto.TeamDTO;
import com.tinashe.taskservice.dto.UserDTO;
import com.tinashe.taskservice.feign.TeamClient;
import com.tinashe.taskservice.feign.UserClient;
import com.tinashe.taskservice.mapper.TaskMapper;
import com.tinashe.taskservice.model.Task;
import com.tinashe.taskservice.model.TaskStatus;
import com.tinashe.taskservice.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepo;
    private final UserClient userClient;
    private final TeamClient teamClient;

    public TaskDTO createTask(TaskCreateDTO dto, String username) {
        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .projectId(dto.getProjectId())
                .status(TaskStatus.TODO)
                .build();

        log.info("User '{}' is creating task '{}' for project {}", username, dto.getTitle(), dto.getProjectId());
        return TaskMapper.toDTO(taskRepo.save(task));
    }

    public TaskDTO getTaskById(Long id) {
        return TaskMapper.toDTO(findByIdOrThrow(id));
    }

    public TaskDTO updateTask(Long id, TaskUpdateDTO dto, String username) {
        Task task = findByIdOrThrow(id);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());

        log.info("User '{}' updated task {} to status {}", username, id, dto.getStatus());
        return TaskMapper.toDTO(taskRepo.save(task));
    }

    public void deleteTask(Long id, String username) {
        Task task = findByIdOrThrow(id);
        taskRepo.delete(task);
        log.info("User '{}' deleted task {}", username, id);
    }

    public TaskDTO assignTeam(Long taskId, Long teamId, String requestedBy) {
        TeamDTO team = teamClient.getTeamById(teamId);
        if (team == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found");
        }

        Task task = findByIdOrThrow(taskId);
        task.setAssignedToTeamId(teamId);

        log.info("User '{}' assigned team '{}' to task {}", requestedBy, team.getName(), taskId);
        return TaskMapper.toDTO(taskRepo.save(task));
    }

    public TaskDTO assignUser(Long taskId, String username, String requestedBy) {
        UserDTO user = userClient.getUserByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Task task = findByIdOrThrow(taskId);
        task.setAssignedToUsername(username);

        log.info("User '{}' assigned user '{}' to task {}", requestedBy, username, taskId);
        return TaskMapper.toDTO(taskRepo.save(task));
    }

    public Page<TaskDTO> getAllTasks(int page, int size) {
        return taskRepo.findAll(PageRequest.of(page, size)).map(TaskMapper::toDTO);
    }

    public Page<TaskDTO> getTasksByProject(Long projectId, Optional<TaskStatus> status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (status.isPresent()) {
            return taskRepo.findByProjectIdAndStatus(projectId, status.get(), pageRequest)
                    .map(TaskMapper::toDTO);
        }

        return taskRepo.findByProjectId(projectId, pageRequest)
                .map(TaskMapper::toDTO);
    }

    public TaskDTO updateStatus(Long id, String statusStr, String username) {
        Task task = findByIdOrThrow(id);
        try {
            TaskStatus newStatus = TaskStatus.valueOf(statusStr.toUpperCase());
            task.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + statusStr);
        }

        log.info("User '{}' updated task {} status to {}", username, id, statusStr.toUpperCase());
        return TaskMapper.toDTO(taskRepo.save(task));
    }

    public TaskDTO unassign(Long taskId, String requestedBy) {
        Task task = findByIdOrThrow(taskId);
        task.setAssignedToUsername(null);
        task.setAssignedToTeamId(null);

        log.info("User '{}' unassigned user and team from task {}", requestedBy, taskId);
        return TaskMapper.toDTO(taskRepo.save(task));
    }

    private Task findByIdOrThrow(Long id) {
        return taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }
}
