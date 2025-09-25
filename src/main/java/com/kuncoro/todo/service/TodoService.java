package com.kuncoro.todo.service;

import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.domain.Todo;
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

    public Optional<Todo> findById(UUID id) {
        return todoRepository.findById(id);
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
}