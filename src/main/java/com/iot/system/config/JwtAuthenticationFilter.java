package com.iot.system.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iot.system.exception.ExpiredJwtException;
import com.iot.system.exception.InvalidJwtException;
import com.iot.system.repository.BlacklistedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    private static final List<String> EXCLUDED_PATHS = Arrays.asList("/auth/login", "/auth/register");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestPath = request.getServletPath();
        if (EXCLUDED_PATHS.contains(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String userIdentifier;
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        if (jwtToken.isEmpty()) {
            logger.error("JWT Token is empty");
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "JWT Token is empty");
            return;
        }
        logger.info("JWT Token: " + jwtToken);

        if (blacklistedTokenRepository.findByToken(jwtToken).isPresent()) {
            logger.error("JWT Token is blacklisted");
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "JWT Token is blacklisted");
            return;
        }

        try {
            userIdentifier = jwtService.extractUsername(jwtToken);
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired: " + e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return;
        } catch (InvalidJwtException e) {
            logger.error("Invalid token: " + e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Error extracting username from token", e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Error extracting username from token");
            return;
        }

        logger.info("User Identifier: " + userIdentifier);

        if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userIdentifier);
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("User authenticated and context set");
            } else {
                logger.warn("JWT Token is invalid");
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "JWT Token is invalid");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(status, message, LocalDateTime.now());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Registro do módulo JSR310
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    public static class ErrorResponse {
        private int status;
        private String message;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;

        public ErrorResponse(int status, String message, LocalDateTime timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}
