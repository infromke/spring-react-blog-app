package br.com.infromke.blog.shared.exceptions.dto;

public record ValidationErrorResponse(String path, String message) {
}
