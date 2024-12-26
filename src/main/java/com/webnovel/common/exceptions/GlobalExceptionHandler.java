package com.webnovel.common.exceptions;

import com.webnovel.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleServiceException(final ServiceException e) {
        ErrorResponse err = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return err.toEntity();
    }

    @ExceptionHandler({ AuthException.class, AccessDeniedException.class})
    public ResponseEntity<?> handleServiceException(final RuntimeException e) {
        ErrorResponse err = ErrorResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .build();

        return err.toEntity();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleServiceException(final MethodArgumentNotValidException e, HttpServletRequest request) {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for(var fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]\n");
        }

        ErrorResponse<Map<String, Object>> err = new ErrorResponse<>(HttpStatus.BAD_REQUEST.value(), builder.toString());
        Map<String, Object> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            Map<String, Object> fieldErrorDetails = new HashMap<>();
            fieldErrorDetails.put("defaultMessage", fieldError.getDefaultMessage());
            fieldErrorDetails.put("isError", true);
            errorMap.put(fieldError.getField(), fieldErrorDetails);
        });

        err.setData(errorMap);
        return err.toEntity();
    }
}
