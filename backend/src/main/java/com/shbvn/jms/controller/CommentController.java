package com.shbvn.jms.controller;

import com.shbvn.jms.dto.mapper.CommentMapper;
import com.shbvn.jms.dto.request.CreateCommentRequest;
import com.shbvn.jms.dto.response.CommentResponse;
import com.shbvn.jms.model.Comment;
import com.shbvn.jms.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable String taskId,
            Pageable pageable) {
        Page<Comment> comments = commentService.getCommentsByTaskId(taskId, pageable);
        Page<CommentResponse> response = comments.map(commentMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable String taskId,
            @Valid @RequestBody CreateCommentRequest request) {
        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(request.getUserId())
                .taskId(taskId)
                .build();

        Comment created = commentService.createComment(comment);
        CommentResponse response = commentMapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
