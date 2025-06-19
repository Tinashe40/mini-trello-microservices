package com.tinashe.taskservice.repository;

import com.tinashe.taskservice.model.Task;
import com.tinashe.taskservice.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);
}
