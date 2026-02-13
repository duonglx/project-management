package com.shbvn.jms.controller;

import com.shbvn.jms.dto.mapper.CommentMapper;
import com.shbvn.jms.dto.request.CreateCommentRequest;
import com.shbvn.jms.dto.response.CommentResponse;
import com.shbvn.jms.model.Comment;
import com.shbvn.jms.security.CustomUserDetails;
import com.shbvn.jms.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @PathVariable String taskId,
            Pageable pageable) {
        Page<Comment> comments = commentService.getCommentsByTaskId(taskId, pageable);
        return ResponseEntity.ok(comments.map(commentMapper::toResponse));
    }

    @PostMapping
    @PreAuthorize("@perm.checkProject(#workspaceId, 'comment:create', #projectId)")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @PathVariable String taskId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(userDetails.getId())
                .taskId(taskId)
                .build();

        Comment created = commentService.createComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toResponse(created));
    }
}
