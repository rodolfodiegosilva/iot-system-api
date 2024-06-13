
        package com.iot.system.auth;

import com.iot.system.config.JwtService;
import com.iot.system.model.BlacklistedToken;
import com.iot.system.repository.BlacklistedTokenRepository;
import com.iot.system.repository.UserRepository;
import com.iot.system.user.Role;
import com.iot.system.user.User;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .or(() -> userRepository.findByUsername(request.getUsername()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or username or password");
        }
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("--------------------------------------------------------------");
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            BlacklistedToken blacklistedToken = new BlacklistedToken();
            blacklistedToken.setToken(token);
            blacklistedTokenRepository.save(blacklistedToken);
        }
        SecurityContextHolder.clearContext();
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }
}