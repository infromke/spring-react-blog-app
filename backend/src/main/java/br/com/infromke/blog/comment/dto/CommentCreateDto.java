package br.com.infromke.blog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateDto(
        @NotBlank(message = "Comment cannot be empty")
        @Size(min = 1, max = 360, message = "Comment must be between 1 and 360 characters")
        String content
) {
}
