package com.iot.system.controller;

import com.iot.system.config.JwtAuthenticationFilter;
import com.iot.system.dto.UserDTO;
import com.iot.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "API for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by username, email or name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = JwtAuthenticationFilter.ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 401, \"message\": \"Unauthorized\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam("searchTerm") String searchTerm) {
        List<UserDTO> users = userService.searchUsers(searchTerm);
        return ResponseEntity.ok(users);
    }
}
