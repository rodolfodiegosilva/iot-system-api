package com.iot.system.repository;

import com.iot.system.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByUsernameIn(List<String> usernames);

    List<User> findByUsernameContainingOrEmailContainingOrNameContaining(String username, String email, String name);

}
