package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.CommentResponse;
import com.shbvn.jms.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "user", source = "comment.user")
    CommentResponse toResponse(Comment comment);
}
