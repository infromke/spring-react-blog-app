package br.com.spring_react.blog.infra.exceptions;

public record ValidationErrorResponse(String path, String message) {
}
