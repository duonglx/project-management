package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.CommentResponse;
import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.model.Comment;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-13T16:54:26+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Homebrew)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public CommentResponse toResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        UserResponse user = null;
        String id = null;
        String content = null;
        String userId = null;
        String taskId = null;
        LocalDateTime createdAt = null;

        user = userMapper.toResponse( comment.getUser() );
        id = comment.getId();
        content = comment.getContent();
        userId = comment.getUserId();
        taskId = comment.getTaskId();
        createdAt = comment.getCreatedAt();

        CommentResponse commentResponse = new CommentResponse( id, content, userId, taskId, createdAt, user );

        return commentResponse;
    }
}
