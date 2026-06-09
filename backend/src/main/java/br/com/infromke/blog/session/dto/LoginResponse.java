package br.com.infromke.blog.session.dto;

import br.com.infromke.blog.user.internal.User;

public record LoginResponse(String token, User user) {
}
