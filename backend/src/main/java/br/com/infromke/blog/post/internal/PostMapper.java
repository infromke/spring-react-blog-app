package br.com.infromke.blog.post.internal;

import br.com.infromke.blog.post.dto.PostDetailsDTO;
import br.com.infromke.blog.user.dto.UserSummaryDTO;

public class PostMapper {

    public static PostDetailsDTO toDetailsDTO(Post post) {
        if (post == null) return null;

        var author = post.getAuthor();

        UserSummaryDTO authorDTO = new UserSummaryDTO(
                author.getId(),
                author.getName(),
                author.getSlug()
        );

        return new PostDetailsDTO(
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
