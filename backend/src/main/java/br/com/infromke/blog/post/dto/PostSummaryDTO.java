package br.com.infromke.blog.post.dto;

import java.util.UUID;

public record PostSummaryDTO(UUID id, String title, String slug){
}
