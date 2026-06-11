package br.com.infromke.blog.session.dto;

import br.com.infromke.blog.user.dto.UserDto;

public record LoginResponse(String token, UserDto user) {
}
