package br.com.infromke.blog.post.dto;

import br.com.infromke.blog.user.dto.UserSummaryDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostDetailsDTO(
        UUID id,
        String title,
        String summary,
        String content,
        String banner,
        String slug,
        UserSummaryDTO author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
