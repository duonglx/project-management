package com.shbvn.jms.service;

import com.shbvn.jms.dto.mapper.UserMapper;
import com.shbvn.jms.dto.request.CreateUserRequest;
import com.shbvn.jms.dto.request.UpdateUserRequest;
import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.model.User;
import com.shbvn.jms.model.WorkspaceMember;
import com.shbvn.jms.model.enums.WorkspaceRole;
import com.shbvn.jms.repository.UserRepository;
import com.shbvn.jms.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .systemRole(request.getSystemRole())
                .image("")
                .build();
        user = userRepository.save(user);

        // Assign to workspaces if provided
        if (request.getWorkspaceIds() != null) {
            for (String workspaceId : request.getWorkspaceIds()) {
                WorkspaceMember member = WorkspaceMember.builder()
                        .userId(user.getId())
                        .workspaceId(workspaceId)
                        .role(WorkspaceRole.MEMBER)
                        .message("")
                        .build();
                workspaceMemberRepository.save(member);
            }
        }

        return userMapper.toResponse(user);
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail().toLowerCase());
        }
        if (request.getUsername() != null) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getSystemRole() != null) {
            user.setSystemRole(request.getSystemRole());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(userId);
    }
}
