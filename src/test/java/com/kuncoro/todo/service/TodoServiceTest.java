package com.kuncoro.todo.service;

import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.domain.Todo;
import com.kuncoro.todo.exception.GlobalException;
import com.kuncoro.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo testTodo;
    private UUID testId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testId = UUID.randomUUID();
        testTodo = new Todo();
        testTodo.setId(testId);
        testTodo.setTitle("Test Todo");
        testTodo.setDescription("Test Description");
        testTodo.setStatus(Status.OPEN);
    }

    @Test
    void create_ShouldReturnSavedTodo() {
        // Given
        Todo newTodo = new Todo();
        newTodo.setTitle("New Todo");
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When
        Todo result = todoService.create(newTodo);

        // Then
        assertNotNull(result);
        assertEquals(Status.OPEN, result.getStatus());
        verify(todoRepository).save(newTodo);
    }

    @Test
    void findAll_WithStatus_ShouldReturnFilteredResults() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Todo> expectedPage = new PageImpl<>(List.of(testTodo));
        when(todoRepository.findByStatus(Status.OPEN, pageRequest)).thenReturn(expectedPage);

        // When
        Page<Todo> result = todoService.findAll(Status.OPEN, pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testTodo, result.getContent().get(0));
        verify(todoRepository).findByStatus(Status.OPEN, pageRequest);
    }

    @Test
    void findAll_WithoutStatus_ShouldReturnAllResults() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Todo> expectedPage = new PageImpl<>(List.of(testTodo));
        when(todoRepository.findAll(pageRequest)).thenReturn(expectedPage);

        // When
        Page<Todo> result = todoService.findAll(null, pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(todoRepository).findAll(pageRequest);
    }

    @Test
    void findById_WhenExists_ShouldReturnTodo() {
        // Given
        when(todoRepository.findById(testId)).thenReturn(Optional.of(testTodo));

        // When
        Todo result = todoService.findById(testId);

        // Then
        assertNotNull(result);
        assertEquals(testTodo, result);
    }

    @Test
    void findById_WhenNotExists_ShouldThrow() {
        // Given
        when(todoRepository.findById(testId)).thenReturn(Optional.empty());

        // Then
        assertThrows(GlobalException.class, () -> todoService.findById(testId));
    }

    @Test
    void updateStatus_WhenExists_ShouldReturnUpdatedTodo() {
        // Given
        when(todoRepository.findById(testId)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When
        Optional<Todo> result = todoService.updateStatus(testId, Status.DONE);

        // Then
        assertTrue(result.isPresent());
        assertEquals(Status.DONE, testTodo.getStatus());
        verify(todoRepository).save(testTodo);
    }

    @Test
    void deleteById_WhenExists_ShouldReturnTrue() {
        // Given
        when(todoRepository.existsById(testId)).thenReturn(true);

        // When
        boolean result = todoService.deleteById(testId);

        // Then
        assertTrue(result);
        verify(todoRepository).deleteById(testId);
    }

    @Test
    void deleteById_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(todoRepository.existsById(testId)).thenReturn(false);

        // When
        boolean result = todoService.deleteById(testId);

        // Then
        assertFalse(result);
        verify(todoRepository, never()).deleteById(any());
    }
}