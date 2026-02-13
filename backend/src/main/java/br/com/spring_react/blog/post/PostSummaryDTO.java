package br.com.spring_react.blog.post;

import java.util.UUID;

public record PostSummaryDTO(UUID id, String title, String slug){
}
