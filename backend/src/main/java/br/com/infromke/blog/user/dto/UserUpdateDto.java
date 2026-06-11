package br.com.infromke.blog.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Size(min = 2, max = 54, message = "Name must be between 2 and 54 characters")
        String name,

        @Email(message = "Invalid email format")
        String email,

        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        String confirmPassword
) {
}
