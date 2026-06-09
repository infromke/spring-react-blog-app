package br.com.infromke.blog.comment.dto;

import br.com.infromke.blog.post.dto.PostSummaryDTO;
import br.com.infromke.blog.user.dto.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDetailsDTO(
        UUID id,
        String content,
        UserSummaryDTO author,
        PostSummaryDTO post,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
