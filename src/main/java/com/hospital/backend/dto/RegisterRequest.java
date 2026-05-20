package com.hospital.backend.dto;

import com.hospital.backend.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * DTO for POST /api/auth/register.
 *
 * FIX: Replaces the original @RequestParam approach.
 * Sending credentials via a JSON body keeps them out of
 * server access-logs and browser address bars.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private User.Role role;
}