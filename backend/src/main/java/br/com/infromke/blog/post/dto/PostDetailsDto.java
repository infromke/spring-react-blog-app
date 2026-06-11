package br.com.infromke.blog.post.dto;

import br.com.infromke.blog.user.dto.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostDetailsDto(
        UUID id,
        String title,
        String summary,
        String content,
        String banner,
        String slug,
        UserSummaryDto author,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
