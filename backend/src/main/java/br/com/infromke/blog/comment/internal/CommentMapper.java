package br.com.infromke.blog.comment.internal;

import br.com.infromke.blog.comment.dto.CommentDetailsDto;
import br.com.infromke.blog.post.internal.PostMapper;
import br.com.infromke.blog.user.internal.UserMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, PostMapper.class})
public interface CommentMapper {
    CommentDetailsDto toDetailsDto(Comment comment);
}
