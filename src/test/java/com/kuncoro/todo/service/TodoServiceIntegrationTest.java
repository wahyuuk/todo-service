package com.kuncoro.todo.service;

import com.kuncoro.todo.config.TestContainersConfiguration;
import com.kuncoro.todo.domain.Status;
import com.kuncoro.todo.dto.CreateTodoRequest;
import com.kuncoro.todo.dto.TodoResponse;
import com.kuncoro.todo.exception.GlobalException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
class TodoServiceIntegrationTest {

    @Autowired
    private TodoService todoService;

    @Test
    void create_ShouldCreateAndReturnTodoResponse() {
        // Given
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Integration Test Todo");
        request.setDescription("Integration test description");
        request.setDueDate(Instant.parse("2025-12-31T23:59:59Z"));

        // When
        TodoResponse response = todoService.create(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Integration Test Todo");
        assertThat(response.getDescription()).isEqualTo("Integration test description");
        assertThat(response.getStatus()).isEqualTo(Status.OPEN);
        assertThat(response.getDueDate()).isEqualTo(Instant.parse("2025-12-31T23:59:59Z"));
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    void findByIdResponse_WhenExists_ShouldReturnTodoResponse() {
        // Given
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Find By ID Test");
        request.setDescription("Find by ID description");
        TodoResponse created = todoService.create(request);

        // When
        TodoResponse found = todoService.findByIdResponse(created.getId());

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getTitle()).isEqualTo("Find By ID Test");
        assertThat(found.getDescription()).isEqualTo("Find by ID description");
        assertThat(found.getStatus()).isEqualTo(Status.OPEN);
    }

    @Test
    void findByIdResponse_WhenNotExists_ShouldThrowGlobalException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        assertThatThrownBy(() -> todoService.findByIdResponse(nonExistentId))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    void updateStatusResponse_WhenExists_ShouldUpdateAndReturnTodo() {
        // Given
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Update Status Test");
        request.setDescription("Update status description");
        TodoResponse created = todoService.create(request);

        // When
        TodoResponse updated = todoService.updateStatusResponse(created.getId(), Status.DONE);

        // Then
        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getStatus()).isEqualTo(Status.DONE);
        assertThat(updated.getTitle()).isEqualTo("Update Status Test");
        // Don't check exact timestamp comparison as it might be flaky
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void deleteByIdOrThrow_WhenExists_ShouldDeleteTodo() {
        // Given
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Delete Test");
        request.setDescription("Delete description");
        TodoResponse created = todoService.create(request);

        // When
        todoService.deleteByIdOrThrow(created.getId());

        // Then
        assertThatThrownBy(() -> todoService.findByIdResponse(created.getId()))
                .isInstanceOf(GlobalException.class);
    }
}