package br.com.infromke.blog.user.dto;

import br.com.infromke.blog.user.internal.UserRole;

import java.util.UUID;

public record UserDto(UUID id,
                      String name,
                      String email,
                      String avatar,
                      String slug,
                      UserRole role) {
}
