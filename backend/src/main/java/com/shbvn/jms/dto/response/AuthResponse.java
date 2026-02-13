package com.shbvn.jms.dto.response;

public record AuthResponse(
    UserResponse user
) {
    public static AuthResponse of(UserResponse user) {
        return new AuthResponse(user);
    }
}
