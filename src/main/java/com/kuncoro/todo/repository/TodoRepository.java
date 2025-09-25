package com.kuncoro.todo.repository;

import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    
    Page<Todo> findByStatus(Status status, Pageable pageable);
}