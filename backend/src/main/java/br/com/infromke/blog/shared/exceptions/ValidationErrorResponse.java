package br.com.infromke.blog.shared.exceptions;

public record ValidationErrorResponse(String path, String message) {
}
