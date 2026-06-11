package br.com.infromke.blog.comment.dto;

import br.com.infromke.blog.post.dto.PostSummaryDto;
import br.com.infromke.blog.user.dto.UserSummaryDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDetailsDto(
        UUID id,
        String content,
        UserSummaryDto author,
        PostSummaryDto post,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
