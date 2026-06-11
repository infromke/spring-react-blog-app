package br.com.infromke.blog.post.internal;

import br.com.infromke.blog.post.dto.PostDetailsDto;
import br.com.infromke.blog.user.dto.UserSummaryDto;

public final class PostMapper {

    private PostMapper() {
        throw new UnsupportedOperationException("PostMapper is an utility class and cannot be instantiated");
    }

    public static PostDetailsDto toDetailsDto(Post post) {
        if (post == null) return null;

        var author = post.getAuthor();

        UserSummaryDto authorDTO = new UserSummaryDto(
                author.getId(),
                author.getName(),
                author.getSlug()
        );

        return new PostDetailsDto(
                post.getId(),
                post.getTitle(),
                post.getSummary(),
                post.getContent(),
                post.getBanner(),
                post.getSlug(),
                authorDTO,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
