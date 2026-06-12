package br.com.infromke.blog.user.internal;

import br.com.infromke.blog.user.dto.UserDto;
import br.com.infromke.blog.user.dto.UserSummaryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // mapeia id, nome, e-mail, avatar, slug e role
    UserDto toDto(User user);

    // mapeia id, nome e slug
    UserSummaryDto toSummaryDto(User user);
}
