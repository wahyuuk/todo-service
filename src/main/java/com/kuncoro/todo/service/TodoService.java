package com.kuncoro.todo.service;

import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.domain.Todo;
import com.kuncoro.todo.dto.CreateTodoRequest;
import com.kuncoro.todo.dto.TodoResponse;
import com.kuncoro.todo.exception.ErrorCodes;
import com.kuncoro.todo.exception.GlobalException;
import com.kuncoro.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponse create(CreateTodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setDueDate(request.getDueDate());
        // Status defaulted to OPEN in entity; no need to set here
        Todo saved = todoRepository.save(todo);
        return mapToResponse(saved);
    }

    public Page<TodoResponse> findAllResponse(Status status, Pageable pageable) {
        Page<Todo> page;
        if (status != null) {
            page = todoRepository.findByStatus(status, pageable);
        } else {
            page = todoRepository.findAll(pageable);
        }
        return page.map(this::mapToResponse);
    }

    public TodoResponse findByIdResponse(UUID id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodes.TODO_NOT_FOUND));
        return mapToResponse(todo);
    }

    @Transactional
    public TodoResponse updateStatusResponse(UUID id, Status status) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodes.TODO_NOT_FOUND));
        todo.setStatus(status);
        Todo saved = todoRepository.save(todo);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteByIdOrThrow(UUID id) {
        if (!todoRepository.existsById(id)) {
            throw new GlobalException(ErrorCodes.TODO_NOT_FOUND);
        }
        todoRepository.deleteById(id);
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