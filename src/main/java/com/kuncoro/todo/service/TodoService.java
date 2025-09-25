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

import java.util.Optional;
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
        if (todo.getStatus() == null) {
            todo.setStatus(Status.OPEN);
        }
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

    // Existing entity-oriented APIs (kept for backward compatibility/tests)
    @Transactional
    public Todo create(Todo todo) {
        if (todo.getStatus() == null) {
            todo.setStatus(Status.OPEN);
        }
        return todoRepository.save(todo);
    }

    public Page<Todo> findAll(Status status, Pageable pageable) {
        if (status != null) {
            return todoRepository.findByStatus(status, pageable);
        }
        return todoRepository.findAll(pageable);
    }

    public Todo findById(UUID id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCodes.TODO_NOT_FOUND));
    }

    @Transactional
    public Optional<Todo> updateStatus(UUID id, Status status) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setStatus(status);
                    return todoRepository.save(todo);
                });
    }

    @Transactional
    public boolean deleteById(UUID id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
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