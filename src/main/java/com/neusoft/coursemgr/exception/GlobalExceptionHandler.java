package com.neusoft.coursemgr.exception;

import com.neusoft.coursemgr.common.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBizException(BizException ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        StringJoiner joiner = new StringJoiner("; ");
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            joiner.add(err.getField() + ": " + err.getDefaultMessage());
        }
        return ApiResponse.fail(joiner.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraintViolation(ConstraintViolationException ex) {
        StringJoiner joiner = new StringJoiner("; ");
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            joiner.add(v.getPropertyPath() + ": " + v.getMessage());
        }
        return ApiResponse.fail(joiner.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ApiResponse.fail("请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) {
        log.error("Unhandled exception", ex);
        return ApiResponse.fail("服务器内部错误");
    }
}
