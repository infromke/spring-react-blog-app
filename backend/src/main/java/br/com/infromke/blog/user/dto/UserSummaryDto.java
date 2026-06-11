package br.com.infromke.blog.user.dto;

import java.util.UUID;

public record UserSummaryDto(UUID id, String name, String slug) {
}
