package br.com.infromke.blog.user.dto;

import java.util.UUID;

public record UserSummaryDTO(UUID id, String name, String slug) {
}
