package br.com.infromke.blog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateDTO(
        @NotBlank(message = "Comment cannot be empty")
        @Size(min = 1, max = 150, message = "Comment must be between 1 and 150 characters")
        String content
) {
}
