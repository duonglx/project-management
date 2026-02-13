package com.shbvn.jms.controller;

import com.shbvn.jms.dto.request.CreateTaskRequest;
import com.shbvn.jms.dto.request.UpdateTaskRequest;
import com.shbvn.jms.dto.response.TaskResponse;
import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.TaskStatus;
import com.shbvn.jms.model.enums.TaskType;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static String createdTaskId;
    private static final String PROJECT_ID = "4d0f6ef3-e798-4d65-a864-00d9f8085c51";

    @Test
    @Order(1)
    void testGetTasksByProjectId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/v1/tasks?projectId=" + PROJECT_ID + "&page=0&size=20",
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("content");
        assertThat(response.getBody()).contains("Design Dashboard UI");
    }

    @Test
    @Order(2)
    void testCreateTask() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setProjectId(PROJECT_ID);
        request.setTitle("Integration Test Task");
        request.setDescription("A task created during integration testing");
        request.setStatus(TaskStatus.TODO);
        request.setType(TaskType.TASK);
        request.setPriority(Priority.MEDIUM);
        request.setAssigneeId("user_1");
        request.setDueDate(java.time.LocalDateTime.now().plusDays(7));

        ResponseEntity<TaskResponse> response = restTemplate.postForEntity(
            "/api/v1/tasks",
            request,
            TaskResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Integration Test Task");
        assertThat(response.getBody().status()).isEqualTo("TODO");
        assertThat(response.getBody().priority()).isEqualTo("MEDIUM");
        createdTaskId = response.getBody().id();
    }

    @Test
    @Order(3)
    void testGetTaskById() {
        String taskId = "24ca6d74-7d32-41db-a257-906a90bca8f4";
        ResponseEntity<TaskResponse> response = restTemplate.getForEntity(
            "/api/v1/tasks/" + taskId,
            TaskResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(taskId);
        assertThat(response.getBody().title()).isEqualTo("Design Dashboard UI");
        assertThat(response.getBody().status()).isEqualTo("IN_PROGRESS");
    }

    @Test
    @Order(4)
    void testUpdateTaskStatus() {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Task Title");
        request.setDescription("Updated description");
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setType(TaskType.FEATURE);
        request.setPriority(Priority.HIGH);
        request.setAssigneeId("user_2");

        ResponseEntity<TaskResponse> response = restTemplate.exchange(
            "/api/v1/tasks/" + createdTaskId,
            org.springframework.http.HttpMethod.PUT,
            new org.springframework.http.HttpEntity<>(request),
            TaskResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo("IN_PROGRESS");
        assertThat(response.getBody().title()).isEqualTo("Updated Task Title");
    }

    @Test
    @Order(5)
    void testDeleteTask() {
        if (createdTaskId == null) {
            // Skip if no task was created
            return;
        }
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/v1/tasks/" + createdTaskId,
            org.springframework.http.HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
            "/api/v1/tasks/" + createdTaskId,
            String.class
        );
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
