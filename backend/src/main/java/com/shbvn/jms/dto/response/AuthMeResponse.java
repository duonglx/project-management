package com.shbvn.jms.dto.response;

import java.util.List;

public record AuthMeResponse(
    UserResponse user,
    List<String> permissions
) {}
