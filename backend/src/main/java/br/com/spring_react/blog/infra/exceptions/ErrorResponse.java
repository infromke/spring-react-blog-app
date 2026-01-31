package br.com.spring_react.blog.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String code,
        String stack
) {
    public static ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                              String code, Exception ex) {
        String stack = null;

        if (ex != null) {
            stack = Arrays.toString(ex.getStackTrace());
            ex.printStackTrace();
        }

        ErrorResponse error = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                code,
                stack
        );

        return ResponseEntity.status(status).body(error);
    }
}
