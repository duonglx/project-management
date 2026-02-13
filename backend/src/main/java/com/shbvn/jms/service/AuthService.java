package com.shbvn.jms.service;

import com.shbvn.jms.dto.mapper.UserMapper;
import com.shbvn.jms.dto.request.LoginRequest;
import com.shbvn.jms.dto.response.AuthMeResponse;
import com.shbvn.jms.dto.response.AuthResponse;
import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.model.RefreshToken;
import com.shbvn.jms.model.User;
import com.shbvn.jms.repository.RefreshTokenRepository;
import com.shbvn.jms.repository.UserRepository;
import com.shbvn.jms.security.CustomUserDetails;
import com.shbvn.jms.security.CustomUserDetailsService;
import com.shbvn.jms.security.JwtService;
import com.shbvn.jms.service.PermissionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final PermissionService permissionService;

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Store refresh token hash in DB
        storeRefreshToken(user, refreshToken);

        setAccessTokenCookie(response, accessToken);
        setRefreshTokenCookie(response, refreshToken);

        return AuthResponse.of(userMapper.toResponse(user));
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookieValue(request, "jwt_refresh");
        if (refreshToken != null) {
            String tokenHash = hashToken(refreshToken);
            refreshTokenRepository.deleteByTokenHash(tokenHash);
        }

        clearCookie(response, "jwt", "/api");
        clearCookie(response, "jwt_refresh", "/api/auth");
    }

    public AuthMeResponse getCurrentUser(String username, String workspaceId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        UserResponse userResponse = userMapper.toResponse(user);
        List<String> permissions = workspaceId != null
                ? permissionService.getUserPermissions(user.getId(), workspaceId)
                : Collections.emptyList();
        return new AuthMeResponse(userResponse, permissions);
    }

    @Transactional
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookieValue(request, "jwt_refresh");
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh token not found");
        }

        // Validate the refresh token
        String username;
        try {
            username = jwtService.extractUsername(refreshToken);
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Verify token exists in DB (not revoked)
        String tokenHash = hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Refresh token revoked"));

        // Load user and validate
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Rotate: delete old, generate new
        refreshTokenRepository.delete(storedToken);

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        storeRefreshToken(userDetails.getUser(), newRefreshToken);

        setAccessTokenCookie(response, newAccessToken);
        setRefreshTokenCookie(response, newRefreshToken);

        return AuthResponse.of(userMapper.toResponse(userDetails.getUser()));
    }

    private void storeRefreshToken(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(token);
    }

    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set true in production
        cookie.setPath("/api");
        cookie.setMaxAge(900); // 15 minutes
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt_refresh", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set true in production
        cookie.setPath("/api/auth");
        cookie.setMaxAge(604800); // 7 days
        response.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse response, String name, String path) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String extractCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
