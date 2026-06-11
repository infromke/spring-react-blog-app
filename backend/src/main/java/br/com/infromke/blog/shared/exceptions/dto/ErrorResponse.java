package br.com.infromke.blog.shared.exceptions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

// para não exibir os campos nulos as vezes atribuídos ao validationErros e stackTrace
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        List<ValidationErrorResponse> errors, // para os erros de validação
        String stack
) {
    public static ResponseEntity<ErrorResponse> build(
            String type,
            HttpStatus status,
            String detail,
            String instance,
            List<ValidationErrorResponse> validationErrors,
            String stack
    ) {
        ErrorResponse problem = new ErrorResponse(
                type,
                status.getReasonPhrase(),
                status.value(),
                detail,
                instance,
                validationErrors,
                stack
        );

        // define Content-Type específico da RFC 7807
        return ResponseEntity
                .status(status)
                .header("Content-Type", "application/problem+json")
                .body(problem);
    }
}
