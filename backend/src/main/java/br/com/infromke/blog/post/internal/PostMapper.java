package br.com.infromke.blog.post.internal;

import br.com.infromke.blog.post.dto.PostDetailsDto;
import br.com.infromke.blog.post.dto.PostSummaryDto;
import br.com.infromke.blog.user.internal.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface PostMapper {
    // mapeia id, título, sumário, conteúdo, banner, slug, autor(a) e timestamps
    PostDetailsDto toDetailsDto(Post post);

    // mapeia id, título e slug
    PostSummaryDto toSummaryDto(Post post);
}
