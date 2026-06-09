package br.com.infromke.blog.infra.exceptions;

public record ValidationErrorResponse(String path, String message) {
}
