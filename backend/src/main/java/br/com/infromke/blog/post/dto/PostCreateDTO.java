package br.com.infromke.blog.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateDTO(
        @NotBlank(message = "Title cannot be empty")
        @Size(min = 5, max = 150, message = "Title must be between 5 and 150 characters")
        String title,

        @Size(max = 200, message = "Summary cannot exceed 200 characters")
        String summary,

        @NotBlank(message = "Content cannot be empty")
        @Size(min = 100, max = 20000, message = "Content must be between 100 and 20.000 characters")
        String content
) {
}
