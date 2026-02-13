# Security Standards & Authentication Patterns

## Overview

This document outlines security standards for the full-stack project management platform, including JWT authentication, RBAC authorization, and secure API patterns.

**Status:** Spring Boot backend with implemented JWT + RBAC
**Standards:** OAuth 2.0-compatible JWT, Spring Security, OWASP Top 10 compliance

## Backend Security

### Authentication (JWT)

#### Token Generation

**JwtService.java:**
```java
public String generateAccessToken(CustomUserDetails userDetails) {
    return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("userId", userDetails.getId())
            .claim("type", "access")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessExpiration))
            .signWith(getSigningKey())
            .compact();
}

public String generateRefreshToken(CustomUserDetails userDetails) {
    return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("userId", userDetails.getId())
            .claim("type", "refresh")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
            .signWith(getSigningKey())
            .compact();
}
```

**Configuration (application.yml):**
```yaml
app:
  jwt:
    secret: ${JWT_SECRET}  # Minimum 64 bytes, base64-encoded
    access-expiration: 900000  # 15 minutes (milliseconds)
    refresh-expiration: 604800000  # 7 days
```

**Best Practices:**
- Access token: 15 minutes (short-lived)
- Refresh token: 7 days (long-lived, stored in database)
- HS512 algorithm (HMAC SHA-512)
- Secret minimum 64 bytes (256 bits)
- Generate new refresh token on each refresh (token rotation)
- Store refresh tokens in database for revocation
- httpOnly, Secure, SameSite=Strict cookies

#### Token Validation

**JwtService.java:**
```java
public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

public boolean isTokenValid(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
}

private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    return claimsResolver.apply(claims);
}
```

**Validation Steps:**
1. Signature verification (HMAC-SHA512)
2. Expiration check
3. Username validation
4. Type claim check (access vs refresh)

#### JWT in Requests

**JwtAuthenticationFilter.java:**
```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                               HttpServletResponse response,
                               FilterChain filterChain) {
    String token = extractToken(request);
    if (token != null && jwtService.isTokenValid(token, userDetails)) {
        SecurityContextHolder.getContext()
            .setAuthentication(authToken);
    }
    filterChain.doFilter(request, response);
}

private String extractToken(HttpServletRequest request) {
    // Try Authorization header first
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
        return header.substring(7);
    }
    // Fall back to httpOnly cookie
    for (Cookie cookie : request.getCookies()) {
        if ("accessToken".equals(cookie.getName())) {
            return cookie.getValue();
        }
    }
    return null;
}
```

**Cookie Settings:**
```java
response.addCookie(createCookie("accessToken", token, 15 * 60));  // 15 min

private HttpOnly Cookie createCookie(String name, String value, int maxAge) {
    return new ResponseCookie()
            .name(name)
            .value(value)
            .path("/")
            .maxAge(maxAge)
            .httpOnly(true)  // Prevent JavaScript access (XSS protection)
            .secure(true)    // HTTPS only (production)
            .sameSite("Strict")  // CSRF protection
            .build();
}
```

### Authorization (RBAC)

#### Spring Security Method-Level Security

**SecurityConfig.java:**
```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // @PreAuthorize enabled
}
```

**Controller Example:**
```java
@DeleteMapping("/workspaces/{workspaceId}/projects/{projectId}")
@PreAuthorize("@perm.hasProjectPermission(#userId, #workspaceId, #projectId, 'project:delete')")
public ResponseEntity<Void> deleteProject(
        @PathVariable String workspaceId,
        @PathVariable String projectId,
        @AuthenticationPrincipal CustomUserDetails user) {
    String userId = user.getId();
    // Permission checked before method execution
    projectService.deleteProject(projectId);
    return ResponseEntity.noContent().build();
}
```

**PermissionEvaluator Integration:**
```java
@Component
public class PermissionEvaluator extends org.springframework.security.access.PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String workspaceId = extractWorkspaceId(targetDomainObject);
        return permissionService.hasPermission(userDetails.getId(), workspaceId, permission.toString());
    }
}
```

#### Permission Evaluation Levels

**Workspace-Level:**
```java
// Check workspace permission (ADMIN, MEMBER)
public boolean hasPermission(String userId, String workspaceId, String permissionName) {
    WorkspaceMember member = workspaceMemberRepository
        .findByUserIdAndWorkspaceId(userId, workspaceId);

    if (member.isEmpty()) return false;
    if (member.get().getRole() == WorkspaceRole.OWNER) return true;

    Set<String> permissions = getPermissionsForRole(role, workspaceId);
    return permissions.contains(permissionName);
}
```

**Project-Level:**
```java
// Check project permission (LEAD, MEMBER) + workspace permission
public boolean hasProjectPermission(String userId, String workspaceId,
                                   String projectId, String permissionName) {
    // Workspace OWNER bypasses all
    if (isWorkspaceOwner(userId, workspaceId)) return true;

    // Check project role
    ProjectMember projMember = projectMemberRepository
        .findByUserIdAndProjectId(userId, projectId);
    if (projMember.isPresent()) {
        Set<String> permissions = getPermissionsForRole(projMember.get().getRole(), workspaceId);
        if (permissions.contains(permissionName)) return true;
    }

    // Check workspace ADMIN role (has implicit project access)
    Set<String> wsPermissions = getPermissionsForRole("ADMIN", workspaceId);
    return wsPermissions.contains(permissionName);
}
```

**Task-Level:**
```java
// Task-level checks are application logic (not Spring Security)
// Example: User can only edit own task or project member can edit
public boolean canEditTask(String userId, String taskId) {
    Task task = taskRepository.findById(taskId).orElse(null);
    if (task == null) return false;

    // Creator can edit
    if (task.getCreatedBy().equals(userId)) return true;

    // Assignee can edit
    if (task.getAssigneeId().equals(userId)) return true;

    // Project member can edit
    return isProjectMember(userId, task.getProjectId());
}
```

#### Cache for Permissions

**Redis Cache Configuration:**
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheManager cacheManager = RedisCacheManager.create(factory);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))  // 5-minute TTL
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return cacheManager;
    }
}
```

**PermissionService Caching:**
```java
@Cacheable(value = "rolePermissions", key = "#role + ':' + #workspaceId")
public Set<String> getPermissionsForRole(String role, String workspaceId) {
    // Check workspace-specific overrides
    List<RolePermission> overrides = rolePermissionRepository
        .findByRoleAndWorkspaceId(role, workspaceId);

    if (!overrides.isEmpty()) {
        return overrides.stream()
            .map(rp -> rp.getPermission().getName())
            .collect(Collectors.toSet());
    }

    // Fall back to system defaults
    List<RolePermission> defaults = rolePermissionRepository
        .findByRoleAndWorkspaceIdIsNull(role);
    return defaults.stream()
        .map(rp -> rp.getPermission().getName())
        .collect(Collectors.toSet());
}

@Transactional
@CacheEvict(value = "rolePermissions", allEntries = true)
public void updateRolePermissions(String workspaceId, String role, List<String> permissionNames) {
    rolePermissionRepository.deleteByRoleAndWorkspaceId(role, workspaceId);
    // Create new permissions...
}
```

### Password Security

**BCrypt Hashing (12 rounds):**
```java
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // 12 rounds
    }
}
```

**User Creation:**
```java
public void registerUser(CreateUserRequest request) {
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    User user = User.builder()
        .username(request.getUsername())
        .password(hashedPassword)
        .build();
    userRepository.save(user);
}
```

**Authentication:**
```java
public AuthResponse login(LoginRequest request, HttpServletResponse response) {
    User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new AuthenticationException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new AuthenticationException("Invalid password");
    }

    // Generate tokens...
}
```

## Frontend Security

### Secure API Client

**auth-api.js:**
```javascript
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export async function loginApi(username, password) {
  const response = await fetch(`${API_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
    credentials: 'include',  // Auto-include httpOnly cookies
  });
  return response.json();
}

export async function fetchMeApi(workspaceId) {
  const url = new URL(`${API_URL}/auth/me`);
  if (workspaceId) url.searchParams.append('workspaceId', workspaceId);

  const response = await fetch(url.toString(), {
    method: 'GET',
    credentials: 'include',  // Auto-include httpOnly cookies
  });
  return response.json();
}

export async function logoutApi() {
  const response = await fetch(`${API_URL}/auth/logout`, {
    method: 'POST',
    credentials: 'include',
  });
  return response.json();
}
```

**Key Practices:**
- `credentials: 'include'` auto-includes httpOnly cookies in all requests
- No manual token storage needed (browser handles cookies)
- Automatic CSRF protection via SameSite=Strict
- CORS ensures only trusted origins can access

### Protected Routes

**ProtectedRoute.jsx:**
```jsx
export default function ProtectedRoute() {
  const dispatch = useDispatch();
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const status = useSelector(selectAuthStatus);

  useEffect(() => {
    if (!isAuthenticated && status !== 'loading') {
      dispatch(fetchCurrentUser());  // Verify session
    }
  }, [isAuthenticated, status, dispatch]);

  if (status === 'loading') {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}
```

**App.jsx Route Setup:**
```jsx
<Routes>
  <Route path="/login" element={<LoginPage />} />
  <Route element={<ProtectedRoute />}>
    <Route path="/" element={<Layout />}>
      <Route index element={<Dashboard />} />
      <Route path="projects" element={<Projects />} />
      <Route path="workspace/:workspaceId/*" element={<WorkspaceLayout />} />
    </Route>
  </Route>
</Routes>
```

### Permission-Based Rendering

**usePermission Hook:**
```javascript
export function usePermission() {
  const permissions = useSelector(selectPermissions);

  return useMemo(() => ({
    permissions,
    has: (perm) => permissions.includes(perm),
    hasAny: (perms) => perms.some((p) => permissions.includes(p)),
    hasAll: (perms) => perms.every((p) => permissions.includes(p)),
  }), [permissions]);
}
```

**Usage in Components:**
```jsx
function ProjectActions() {
  const { has } = usePermission();

  return (
    <div>
      {has('project:edit') && <EditButton />}
      {has('project:delete') && <DeleteButton />}
      {hasAny(['workspace:admin', 'project:lead']) && <SettingsButton />}
    </div>
  );
}
```

**PermissionGate Component (future):**
```jsx
export function PermissionGate({ permission, fallback, children }) {
  const { has } = usePermission();

  if (has(permission)) {
    return children;
  }

  return fallback || null;
}

// Usage:
<PermissionGate permission="workspace:delete">
  <DeleteWorkspaceButton />
</PermissionGate>
```

### Input Validation

**Form Validation:**
```jsx
function LoginPage() {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};

    if (!formData.username) {
      newErrors.username = 'Username is required';
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.username)) {
      newErrors.username = 'Invalid email format';
    }
    if (!formData.password || formData.password.length < 8) {
      newErrors.password = 'Password must be at least 8 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      // Submit to API
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input value={formData.username} onChange={(e) => setFormData({...formData, username: e.target.value})} />
      {errors.username && <span className="error">{errors.username}</span>}
      {/* Password field... */}
    </form>
  );
}
```

**XSS Prevention:**
- React auto-escapes all text content
- Never use `dangerouslySetInnerHTML` with user input
- Sanitize HTML if needed (use DOMPurify library)

## API Security

### CORS Configuration

**SecurityConfig.java:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "http://localhost:5173",  // Development
        "https://app.example.com"  // Production
    ));
    config.setAllowedMethods(List.of("*"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);  // Allow httpOnly cookies

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

### Input Validation

**DTO with Validation Annotations:**
```java
@Data
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Email(message = "Invalid email format")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

**Controller Validation:**
```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(
    @Valid @RequestBody LoginRequest request,  // @Valid triggers validation
    HttpServletResponse response) {
    return ResponseEntity.ok(authService.login(request, response));
}
```

**Global Exception Handler:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        return ResponseEntity.badRequest()
            .body(new ErrorResponse("Validation failed", errors));
    }
}
```

### Rate Limiting

**Future Configuration (not yet implemented):**
```java
@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter authRateLimiter() {
        return RateLimiter.create(5.0);  // 5 requests per second
    }
}

@PostMapping("/auth/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    if (!authRateLimiter.tryAcquire()) {
        throw new TooManyRequestsException("Rate limit exceeded");
    }
    // Login logic...
}
```

## Data Protection

### Workspace Isolation

**Query Example:**
```java
@Query("SELECT p FROM Project p WHERE p.workspace.id = :workspaceId AND p.id = :projectId")
Optional<Project> findByIdAndWorkspaceId(
    @Param("workspaceId") String workspaceId,
    @Param("projectId") String projectId);
```

**Service Layer:**
```java
public Project getProject(String workspaceId, String projectId) {
    Project project = projectRepository
        .findByIdAndWorkspaceId(workspaceId, projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

    // Permission already checked by @PreAuthorize
    return project;
}
```

**Principle:** Always filter by workspace before returning data to user.

### Sensitive Data Handling

**Exclude from API Responses:**
```java
@Entity
public class User {
    private String password;  // Excluded from serialization

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    // Other fields serialized normally
}
```

**Audit Logging:**
```java
@PreAuthorize("@perm.hasPermission(#userId, #workspaceId, 'admin:delete_project')")
public void deleteProject(String workspaceId, String projectId) {
    auditService.log(AuditEvent.builder()
        .action("PROJECT_DELETED")
        .workspaceId(workspaceId)
        .resourceId(projectId)
        .timestamp(LocalDateTime.now())
        .build());

    projectRepository.deleteById(projectId);
}
```

## Security Checklist

### Before Deployment

- [ ] JWT secret is strong (minimum 64 bytes)
- [ ] HTTPS enabled in production
- [ ] CORS only allows trusted origins
- [ ] Password hashing using BCrypt (12+ rounds)
- [ ] All endpoints require authentication (except /login, /health)
- [ ] All database queries include workspace filtering
- [ ] Sensitive data not logged or exposed in responses
- [ ] Rate limiting configured for auth endpoints
- [ ] Input validation on all API endpoints
- [ ] OWASP Top 10 vulnerabilities reviewed
- [ ] Dependencies audited for vulnerabilities
- [ ] Environment variables not committed to git

### Production Monitoring

- [ ] Monitor auth failure rates
- [ ] Log all permission denials
- [ ] Alert on unusual access patterns
- [ ] Regular security audits
- [ ] Dependency updates (security patches)
- [ ] Token rotation and refresh monitoring
