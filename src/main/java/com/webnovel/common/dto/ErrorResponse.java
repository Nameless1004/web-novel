package com.webnovel.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)  // This will exclude null fields from serialization
public class ErrorResponse<T> {
    private int code;
    private String message;
    @Setter
    private T data;
    private LocalDateTime timestamp;

    @Builder
    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ResponseEntity<ErrorResponse> toEntity() {
        return ResponseEntity.status(this.code).body(this);
    }
}
