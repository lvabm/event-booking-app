package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.util.ApiResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // Record/Class nội bộ 1: Cấu trúc cho lỗi Validation (field: message)
  private record FieldErrorDetail(String field, String message) {}

  // Record/Class nội bộ 2: Cấu trúc lỗi đơn giản cho Custom/BaseException (errorCode: message)
  private record SimpleErrorDetail(String errorCode, String message) {}

  // 1. Xử lý lỗi Validation (MethodArgumentNotValidException)
  // Trả về HTTP 422 (UNPROCESSABLE_ENTITY) với danh sách lỗi chi tiết
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<?>> handlingValidationException(
      MethodArgumentNotValidException ex) {
    // 1. Ánh xạ lỗi thành cấu trúc chi tiết List<FieldErrorDetail>
    List<FieldErrorDetail> validationErrors =
        ex.getBindingResult().getAllErrors().stream()
            .map(
                error -> {
                  String fieldName =
                      error instanceof FieldError fieldError
                          ? fieldError.getField()
                          : error.getObjectName();
                  String errorMessage = error.getDefaultMessage();
                  return new FieldErrorDetail(fieldName, errorMessage);
                })
            .collect(Collectors.toList());

    // 2. Trả về phản hồi lỗi Validation (422) qua ApiResponseBuilder
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiResponseBuilder.error("Validation failed", validationErrors));
  }

  // 2. Xử lý TẤT CẢ Custom Exception (BaseException và các lớp kế thừa như ConflictException)
  // Trả về Status Code được định nghĩa trong Exception (400, 401, 403, 409, 404, etc.)
  @ExceptionHandler(value = BaseException.class)
  public ResponseEntity<BaseResponse<?>> handlingBaseException(BaseException ex) {

    // 1. Tạo đối tượng lỗi đơn giản từ BaseException để điền vào trường 'errors'
    SimpleErrorDetail errorDetail =
        new SimpleErrorDetail(ex.getErrorCode().toString(), ex.getMessage());

    // 2. Trả về phản hồi lỗi nghiệp vụ qua ApiResponseBuilder
    return ResponseEntity.status(ex.getHttpStatus())
        .body(ApiResponseBuilder.error(ex.getMessage(), errorDetail));
  }

  // 3. Bắt tất cả lỗi còn lại (Internal Server Error)
  // Trả về HTTP 500
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<?>> handleAll(Exception ex, HttpServletRequest request) {
    // Log lỗi chi tiết ở đây: logger.error("Internal Server Error: ", ex);

    SimpleErrorDetail errorDetail =
        new SimpleErrorDetail(
            "INTERNAL_SERVER_ERROR",
            ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ApiResponseBuilder.error(
                "An unexpected error occurred. Please try again later.", errorDetail));
  }
}
