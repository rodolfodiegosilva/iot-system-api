package com.iot.system.service;

import com.iot.system.dto.UserDTO;
import com.iot.system.exception.ResourceNotFoundException;
import com.iot.system.repository.UserRepository;
import com.iot.system.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> searchUsers(String searchTerm) {
        User currentUser = getCurrentUser();

        return userRepository.findByUsernameContainingOrEmailContainingOrNameContaining(searchTerm, searchTerm, searchTerm)
                .stream()
                .filter(user -> !user.getUsername().equals(currentUser.getUsername()))
                .map(this::userToUserDTO)
                .collect(Collectors.toList());
    }

    public User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrEmail = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            usernameOrEmail = userDetails.getUsername();
        }

        final String finalUsernameOrEmail = usernameOrEmail;
        return userRepository.findByUsername(finalUsernameOrEmail)
                .or(() -> userRepository.findByEmail(finalUsernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Optional<User> findByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    public UserDTO getUser() {
        final User user = getCurrentUser();
        return userToUserDTO(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + username + "' not found"));
    }

    public List<User> findUsersByUsernameList(List<String> usernames) {
        List<User> users = new ArrayList<>();
        for (String username : usernames) {
            users.add(findUserByUsername(username));
        }
        return users;
    }

    private UserDTO userToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }
}
