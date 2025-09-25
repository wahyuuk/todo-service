package com.kuncoro.todo.service;

import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.domain.Todo;
import com.kuncoro.todo.dto.CreateTodoRequest;
import com.kuncoro.todo.dto.TodoResponse;
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
    void create_ShouldReturnSavedTodoResponse() {
        // Given
        CreateTodoRequest req = new CreateTodoRequest();
        req.setTitle("New Todo");
        req.setDescription("desc");
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // When
        TodoResponse result = todoService.create(req);

        // Then
        assertNotNull(result);
        assertEquals(Status.OPEN, result.getStatus());
        assertEquals(testTodo.getTitle(), result.getTitle());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void findAll_WithStatus_ShouldReturnFilteredResultsAsResponses() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Todo> expectedPage = new PageImpl<>(List.of(testTodo));
        when(todoRepository.findByStatus(Status.OPEN, pageRequest)).thenReturn(expectedPage);

        // When
        Page<TodoResponse> result = todoService.findAllResponse(Status.OPEN, pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testTodo.getId(), result.getContent().get(0).getId());
        verify(todoRepository).findByStatus(Status.OPEN, pageRequest);
    }

    @Test
    void findAll_WithoutStatus_ShouldReturnAllResultsAsResponses() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Todo> expectedPage = new PageImpl<>(List.of(testTodo));
        when(todoRepository.findAll(pageRequest)).thenReturn(expectedPage);

        // When
        Page<TodoResponse> result = todoService.findAllResponse(null, pageRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(todoRepository).findAll(pageRequest);
    }

    @Test
    void findById_WhenExists_ShouldReturnTodoResponse() {
        // Given
        when(todoRepository.findById(testId)).thenReturn(Optional.of(testTodo));

        // When
        TodoResponse result = todoService.findByIdResponse(testId);

        // Then
        assertNotNull(result);
        assertEquals(testTodo.getId(), result.getId());
    }

    @Test
    void findById_WhenNotExists_ShouldThrow() {
        // Given
        when(todoRepository.findById(testId)).thenReturn(Optional.empty());

        // Then
        assertThrows(GlobalException.class, () -> todoService.findByIdResponse(testId));
    }

    @Test
    void updateStatus_WhenExists_ShouldReturnUpdatedTodoResponse() {
        // Given
        when(todoRepository.findById(testId)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TodoResponse result = todoService.updateStatusResponse(testId, Status.DONE);

        // Then
        assertNotNull(result);
        assertEquals(Status.DONE, result.getStatus());
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void deleteById_WhenExists_ShouldDelete() {
        // Given
        when(todoRepository.existsById(testId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> todoService.deleteByIdOrThrow(testId));

        // Then
        verify(todoRepository).deleteById(testId);
    }

    @Test
    void deleteById_WhenNotExists_ShouldThrow() {
        // Given
        when(todoRepository.existsById(testId)).thenReturn(false);

        // Then
        assertThrows(GlobalException.class, () -> todoService.deleteByIdOrThrow(testId));
        verify(todoRepository, never()).deleteById(any());
    }
}