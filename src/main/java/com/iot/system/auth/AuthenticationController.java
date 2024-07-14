package com.iot.system.auth;

import com.iot.system.dto.UserDTO;
import com.iot.system.exception.SuccessResponse;
import com.iot.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 400, \"message\": \"Invalid input\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"status\": 401, \"message\": \"Invalid credentials\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
            ))
    })
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out")
    })
        public ResponseEntity<SuccessResponse> logout (HttpServletRequest request, HttpServletResponse response){
            SuccessResponse successResponse = authenticationService.logout(request, response);
            return ResponseEntity.ok(successResponse);
        }

        @GetMapping("/user")
        @Operation(summary = "Get current user")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
                @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                        schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(value = "{ \"status\": 404, \"message\": \"User not found\", \"timestamp\": \"2024-07-11T18:04:42.4620788\" }")
                ))
        })
        public ResponseEntity<UserDTO> getUser () {
            return ResponseEntity.ok(userService.getUser());
        }
    }
