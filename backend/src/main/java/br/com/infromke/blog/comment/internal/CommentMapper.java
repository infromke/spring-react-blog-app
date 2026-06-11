package br.com.infromke.blog.comment.internal;

import br.com.infromke.blog.comment.dto.CommentDetailsDto;
import br.com.infromke.blog.post.dto.PostSummaryDto;
import br.com.infromke.blog.user.dto.UserSummaryDto;

public final class CommentMapper {

    private CommentMapper() {
        throw new UnsupportedOperationException("CommentMapper is an utility class and cannot be instantiated");
    }

    public static CommentDetailsDto toDetailsDto(Comment comment) {
        if (comment == null) return null;

        var author = comment.getAuthor();
        var post = comment.getPost();

        UserSummaryDto authorDTO = new UserSummaryDto(
                author.getId(),
                author.getName(),
                author.getSlug()
        );

        PostSummaryDto postDTO = new PostSummaryDto(
                post.getId(),
                post.getTitle(),
                post.getSlug()
        );

        return new CommentDetailsDto(
                comment.getId(),
                comment.getContent(),
                authorDTO,
                postDTO,
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
