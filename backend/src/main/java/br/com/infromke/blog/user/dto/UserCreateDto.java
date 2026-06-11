package br.com.infromke.blog.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2, max = 54, message = "Name must be between 2 or 54 characters")
        String name,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword) {
}
