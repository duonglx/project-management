package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.model.User;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T15:19:59+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Homebrew)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        String id = null;
        String name = null;
        String email = null;
        String username = null;
        String systemRole = null;
        String image = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        username = user.getUsername();
        if ( user.getSystemRole() != null ) {
            systemRole = user.getSystemRole().name();
        }
        image = user.getImage();
        createdAt = user.getCreatedAt();
        updatedAt = user.getUpdatedAt();

        UserResponse userResponse = new UserResponse( id, name, email, username, systemRole, image, createdAt, updatedAt );

        return userResponse;
    }
}
