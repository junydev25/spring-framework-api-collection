package com.junydev.spring.api.common;

import com.junydev.spring.api.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleNotFound(ResourceNotFoundException e) {
        return ApiResponse.builder()
                .status("fail")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleIllegalArg(MethodArgumentTypeMismatchException e) {
        return ApiResponse.builder()
                .status("fail")
                .message("잘못된 파라미터를 요청했습니다.")
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleAll(Exception e) {
        return ApiResponse.builder()
                        .status("fail")
                        .message("알 수 없는 오류가 발생했습니다.")
                        .build();
    }
}
