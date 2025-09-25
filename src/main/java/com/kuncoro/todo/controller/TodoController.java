package com.kuncoro.todo.controller;

import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.domain.Todo;
import com.kuncoro.todo.dto.CreateTodoRequest;
import com.kuncoro.todo.dto.TodoResponse;
import com.kuncoro.todo.dto.UpdateStatusRequest;
import com.kuncoro.todo.exception.TodoNotFoundException;
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
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());

        Todo savedTodo = todoService.create(todo);
        TodoResponse response = mapToResponse(savedTodo);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TodoResponse>> findAll(
            @RequestParam(value = "status", required = false) Status status,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<Todo> todos = todoService.findAll(status, pageable);
        Page<TodoResponse> response = todos.map(this::mapToResponse);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> findById(@PathVariable UUID id) {
        Todo todo = todoService.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        
        TodoResponse response = mapToResponse(todo);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TodoResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {
        
        Todo todo = todoService.updateStatus(id, request.getStatus())
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));
        
        TodoResponse response = mapToResponse(todo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!todoService.deleteById(id)) {
            throw new TodoNotFoundException("Todo not found with id: " + id);
        }
        
        return ResponseEntity.noContent().build();
    }

    private TodoResponse mapToResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setStatus(todo.getStatus());
        response.setDueDate(todo.getDueDate());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}