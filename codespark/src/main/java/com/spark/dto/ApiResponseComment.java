package com.spark.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseComment<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ApiResponseComment<T> success(String message, T data) {
        return ApiResponseComment.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponseComment<T> error(String message) {
        return ApiResponseComment.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }

}