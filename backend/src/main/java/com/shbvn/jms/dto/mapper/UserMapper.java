package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
