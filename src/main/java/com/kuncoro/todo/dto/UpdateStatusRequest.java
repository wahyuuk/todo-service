package com.kuncoro.todo.dto;

import com.kuncoro.todo.domain.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    
    @NotNull(message = "Status is required")
    private Status status;
}