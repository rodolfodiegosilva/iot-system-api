package com.iot.system.auth;

import com.iot.system.config.JwtService;
import com.iot.system.repository.UserRepository;
import com.iot.system.user.Role;
import com.iot.system.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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
}
