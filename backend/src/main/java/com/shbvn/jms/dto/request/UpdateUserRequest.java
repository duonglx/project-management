package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.SystemRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private SystemRole systemRole;
}
