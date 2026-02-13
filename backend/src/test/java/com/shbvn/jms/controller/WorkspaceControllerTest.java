package com.shbvn.jms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shbvn.jms.dto.request.CreateWorkspaceRequest;
import com.shbvn.jms.dto.request.UpdateWorkspaceRequest;
import com.shbvn.jms.dto.response.WorkspaceResponse;
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
class WorkspaceControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static String createdWorkspaceId;
    private static final String EXISTING_WORKSPACE_ID = "org_1";

    @Test
    @Order(1)
    void testGetWorkspacesByUserId() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/workspaces?userId=user_1&page=0&size=20",
                String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("Status: " + response.getStatusCode());
                System.err.println("Body: " + response.getBody());
                System.err.println("Headers: " + response.getHeaders());
            }
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody()).contains("content");
            assertThat(response.getBody()).contains("org_1");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @Order(2)
    void testCreateWorkspace() {
        CreateWorkspaceRequest request = new CreateWorkspaceRequest();
        request.setName("Test Workspace");
        request.setDescription("A test workspace");
        request.setImageUrl("https://example.com/image.png");
        request.setOwnerId("user_1");

        ResponseEntity<WorkspaceResponse> response = restTemplate.postForEntity(
            "/api/v1/workspaces",
            request,
            WorkspaceResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Test Workspace");
        assertThat(response.getBody().description()).isEqualTo("A test workspace");
        createdWorkspaceId = response.getBody().id();
    }

    @Test
    @Order(3)
    void testGetWorkspaceById() {
        ResponseEntity<WorkspaceResponse> response = restTemplate.getForEntity(
            "/api/v1/workspaces/" + EXISTING_WORKSPACE_ID,
            WorkspaceResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(EXISTING_WORKSPACE_ID);
        assertThat(response.getBody().name()).isEqualTo("Corp Workspace");
    }

    @Test
    @Order(4)
    void testUpdateWorkspace() {
        UpdateWorkspaceRequest request = new UpdateWorkspaceRequest();
        request.setName("Updated Corp Workspace");
        request.setDescription("Updated description");
        request.setImageUrl("https://example.com/updated.png");

        ResponseEntity<WorkspaceResponse> response = restTemplate.exchange(
            "/api/v1/workspaces/" + EXISTING_WORKSPACE_ID,
            org.springframework.http.HttpMethod.PUT,
            new org.springframework.http.HttpEntity<>(request),
            WorkspaceResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Updated Corp Workspace");
        assertThat(response.getBody().description()).isEqualTo("Updated description");
    }

    @Test
    @Order(5)
    void testDeleteWorkspace() {
        if (createdWorkspaceId == null) {
            // Skip if no workspace was created
            return;
        }
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/v1/workspaces/" + createdWorkspaceId,
            org.springframework.http.HttpMethod.DELETE,
            null,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
            "/api/v1/workspaces/" + createdWorkspaceId,
            String.class
        );
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
