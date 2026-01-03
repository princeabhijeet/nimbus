package dev.prince.nimbus.exception;

import dev.prince.nimbus.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NimbusParentException.class})
    public ResponseEntity<ErrorDto> handleNimbusParentException(NimbusParentException exception, HttpServletRequest request) {
        ErrorDto error = ErrorDto.builder()
                .code(String.valueOf(exception.getErrorCode()))
                .message(exception.getErrorMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.valueOf(exception.getErrorCode())).body(error);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorDto> handleAllOtherExceptions(Exception exception, HttpServletRequest request) {
        ErrorDto error = ErrorDto.builder()
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
