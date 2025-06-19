package com.tinashe.taskservice.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tinashe.taskservice.dto.TaskCreateDTO;
import com.tinashe.taskservice.dto.TaskDTO;
import com.tinashe.taskservice.dto.TaskUpdateDTO;
import com.tinashe.taskservice.model.TaskStatus;
import com.tinashe.taskservice.service.TaskService;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Task Management", description = "APIs for managing tasks in the Mini Trello application")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody TaskCreateDTO taskDTO,
            @RequestHeader("X-USERNAME") String username) {
        return ResponseEntity.ok(taskService.createTask(taskDTO, username));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @RequestBody TaskUpdateDTO dto,
            @RequestHeader("X-USERNAME") String username) {
        return ResponseEntity.ok(taskService.updateTask(id, dto, username));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestHeader("X-USERNAME") String username) {
        taskService.deleteTask(id, username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get tasks by project")
    public ResponseEntity<Page<TaskDTO>> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, Optional.ofNullable(status), page, size));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<TaskDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestHeader("X-USERNAME") String username) {
        return ResponseEntity.ok(taskService.updateStatus(id, status, username));
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign a user to a task")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long id,
            @RequestParam String assignee,
            @RequestHeader("X-USERNAME") String username) {
        try {
            return ResponseEntity.ok(taskService.assignUser(id, assignee, username));
        } catch (FeignException.NotFound ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/{id}/assign-team")
    @Operation(summary = "Assign a team to a task")
    public ResponseEntity<TaskDTO> assignTeam(
            @PathVariable Long id,
            @RequestParam Long teamId,
            @RequestHeader("X-USERNAME") String username) {
        return ResponseEntity.ok(taskService.assignTeam(id, teamId, username));
    }

    @PatchMapping("/{id}/unassign")
    @Operation(summary = "Unassign user and team from a task")
    public ResponseEntity<TaskDTO> unassignTask(
            @PathVariable Long id,
            @RequestHeader("X-USERNAME") String username) {
        return ResponseEntity.ok(taskService.unassign(id, username));
    }
}
