package br.com.infromke.blog.post.dto;

import java.util.UUID;

public record PostSummaryDto(UUID id, String title, String slug){
}
