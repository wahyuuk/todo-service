package com.kuncoro.todo.controller;

import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.dto.CreateTodoRequest;
import com.kuncoro.todo.dto.TodoResponse;
import com.kuncoro.todo.dto.UpdateStatusRequest;
import com.kuncoro.todo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody CreateTodoRequest request) {
        TodoResponse response = todoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TodoResponse>> findAll(
            @RequestParam(value = "status", required = false) Status status,
            @PageableDefault() Pageable pageable) {
        
        Page<TodoResponse> response = todoService.findAllResponse(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> findById(@PathVariable UUID id) {
        TodoResponse response = todoService.findByIdResponse(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TodoResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {
        
        TodoResponse response = todoService.updateStatusResponse(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        todoService.deleteByIdOrThrow(id);
        return ResponseEntity.noContent().build();
    }
}