package com.kuncoro.todo.dto;

import com.kuncoro.todo.domain.Status;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class TodoResponse {
    
    private UUID id;
    private String title;
    private String description;
    private Status status;
    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;
}