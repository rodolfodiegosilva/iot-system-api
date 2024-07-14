package com.iot.system.auth;

import com.iot.system.config.JwtService;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.model.BlacklistedToken;
import com.iot.system.repository.BlacklistedTokenRepository;
import com.iot.system.repository.UserRepository;
import com.iot.system.user.Role;
import com.iot.system.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public AuthenticationResponse register(@NonNull final RegisterRequest request) {
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("Email and username cannot be null or empty");
        }

        final Optional<User> userOptional = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getUsername()));
        if (userOptional.isPresent()) {
            logger.warn("User already exists with email: {} or username: {}", request.getEmail(), request.getUsername());
            throw new IllegalArgumentException("User with the given email or username already exists");
        }

        final User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        final String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse login(@NonNull final AuthenticationRequest request) {
        if (!StringUtils.hasText(request.getEmail()) && !StringUtils.hasText(request.getUsername())) {
            throw new IllegalArgumentException("Email or username must be provided");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        final User user = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getUsername()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Invalid password for user: {}", user.getUsername());
            throw new IllegalArgumentException("Invalid email or username or password");
        }
        final String token = jwtService.generateToken(user);
        logger.info("User logged in with username: {}", user.getUsername());
        return new AuthenticationResponse(token);
    }

    public SuccessResponse logout(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String token = authHeader.substring(7);
            final BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(token);
            blacklistedTokenRepository.save(blacklistedToken);
            logger.info("User logged out and token blacklisted");
        }
        SecurityContextHolder.clearContext();
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());
        return new SuccessResponse(200, "User was successfully logged out.");
    }
}
