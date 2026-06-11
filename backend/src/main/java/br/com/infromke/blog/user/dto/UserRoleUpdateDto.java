package br.com.infromke.blog.user.dto;

import br.com.infromke.blog.user.internal.UserRole;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateDto(
        @NotNull(message = "The target role must be specified")
        UserRole role
) { }
