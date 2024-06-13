package com.iot.system.service;

import com.iot.system.repository.UserRepository;
import com.iot.system.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrEmail = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            usernameOrEmail = userDetails.getUsername();
        }

        final String finalUsernameOrEmail = usernameOrEmail;
        return userRepository.findByUsername(finalUsernameOrEmail)
                .or(() -> userRepository.findByEmail(finalUsernameOrEmail))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
