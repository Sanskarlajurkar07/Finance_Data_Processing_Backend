package com.fintech.security;

import com.fintech.model.User;
import com.fintech.repository.UserRepository;
import com.fintech.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * JWT Authentication Filter for Spring Security.
 * 
 * Extends OncePerRequestFilter to ensure the filter is applied only once per request.
 * Extracts JWT token from Authorization header, validates it, and sets the SecurityContext.
 * 
 * Filter behavior:
 * - Extracts JWT from "Authorization: Bearer <token>" header
 * - Validates token signature and expiration using JwtUtil
 * - Extracts userId and username from token claims
 * - Loads user from UserRepository
 * - Creates UsernamePasswordAuthenticationToken and sets in SecurityContext
 * - Returns 401 Unauthorized for expired/invalid tokens
 * - Skips authentication for public endpoints (e.g., /auth/login, /actuator/health)
 * 
 * Requirements: 1.4
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    /**
     * Public endpoints that don't require JWT authentication.
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/actuator/health",
            "/swagger-ui",
            "/v3/api-docs"
    };
    
    /**
     * Constructor for dependency injection.
     * 
     * @param jwtUtil the JWT utility for token validation
     * @param userRepository the user repository for loading users
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }
    
    /**
     * Filters incoming requests to extract and validate JWT tokens.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // Check if this is a public endpoint
            if (isPublicEndpoint(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract JWT token from Authorization header
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                // Token is valid - extract user information and set SecurityContext
                authenticateUser(token);
            } else if (token != null) {
                // Token is invalid or expired - return 401
                sendUnauthorizedError(response, "Invalid or expired token");
                return;
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            // Handle any exceptions during token processing
            sendUnauthorizedError(response, "Authentication failed: " + e.getMessage());
        }
    }
    
    /**
     * Extracts JWT token from the Authorization header.
     * 
     * Expected format: "Authorization: Bearer <token>"
     * 
     * @param request the HTTP request
     * @return the JWT token, or null if not found or invalid format
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        
        return null;
    }
    
    /**
     * Authenticates the user by extracting claims from the JWT token
     * and setting the SecurityContext.
     * 
     * @param token the JWT token
     */
    private void authenticateUser(String token) {
        try {
            Long userId = jwtUtil.extractUserId(token);
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            
            // Load user from database
            if (userId != null) {
                Optional<User> userOptional = userRepository.findById(userId);
                
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    
                    // Verify user is active
                    if (!user.getIsActive()) {
                        return; // User is inactive, don't authenticate
                    }
                    
                    // Create authentication token with user's role
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    Collections.singletonList(authority)
                            );
                    
                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // If any error occurs during authentication, don't set the context
            // This will cause the request to be rejected by Spring Security
        }
    }
    
    /**
     * Checks if the request URI is a public endpoint that doesn't require authentication.
     * 
     * @param requestUri the request URI
     * @return true if the endpoint is public, false otherwise
     */
    private boolean isPublicEndpoint(String requestUri) {
        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
            if (requestUri.startsWith(publicEndpoint)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sends a 401 Unauthorized response with error message.
     * 
     * @param response the HTTP response
     * @param message the error message
     * @throws IOException if an I/O error occurs
     */
    private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
