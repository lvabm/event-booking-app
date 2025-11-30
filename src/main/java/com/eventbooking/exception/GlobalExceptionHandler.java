package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.util.ApiResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Phương thức chung để xây dựng Body phản hồi lỗi cho các Custom Exception
    private Map<String, Object> buildErrorBody(BaseException ex, String path) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getHttpStatus().value());
        body.put("errorCode", ex.getErrorCode());
        body.put("message", ex.getMessage());
        body.put("path", path);
        return body;
    }

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

  protected ResponseEntity<Object> handleBindException(
      BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return buildValidationErrorResponse(ex.getBindingResult());
  }

//  // 2. Gộp TẤT CẢ các Custom Exception (Handlers 2, 3, 4, 5, 6, 7) vào một Handler duy nhất
//  // đều kế thừa từ BaseException hoặc có cấu trúc interface chung để lấy HttpStatus và ErrorCode.
//  @ExceptionHandler({
//    ConstraintViolationException.class,
//    EntityNotFoundException.class,
//    BadRequestException.class,
//    ConflictException.class,
//    ForbiddenException.class,
//    ResourceNotFoundException.class,
//    UnauthorizedException.class
//  })
//  public ResponseEntity<Object> handleCustomExceptions(
//      BaseException ex, HttpServletRequest request) {
//    // Đối với UnauthorizedException, trả về format đơn giản theo yêu cầu
//    if (ex instanceof UnauthorizedException) {
//      Map<String, Object> body = new LinkedHashMap<>();
//      body.put("success", false);
//      body.put("message", ex.getMessage());
//      return new ResponseEntity<>(body, ex.getHttpStatus());
//    }
//
//    Map<String, Object> body = buildErrorBody(ex, request.getRequestURI());
//    return new ResponseEntity<>(body, ex.getHttpStatus());
//
//
//  }

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

  @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<?>> handleAccessDeniedException(
        AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                ApiResponseBuilder.error(
                    "Forbidden – You do not have permission to perform this action", null));
    }

  private ResponseEntity<Object> buildValidationErrorResponse(BindingResult bindingResult) {
    List<Map<String, String>> errors =
        bindingResult.getFieldErrors().stream()
            .map(fieldError -> {
                Map<String, String> errorMap = new LinkedHashMap<>();
                errorMap.put("field", fieldError.getField());
                errorMap.put("message", fieldError.getDefaultMessage());
                return errorMap;
            })
            .collect(Collectors.toList());

    // include global errors if needed
    errors.addAll(
        bindingResult.getGlobalErrors().stream()
            .map(
                error ->{
                    Map<String, String> errorMap = new LinkedHashMap<>();
                    errorMap.put("field", error.getObjectName());
                    errorMap.put("message", error.getDefaultMessage());
                    return errorMap;
                })
            .collect(Collectors.toList()));

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("success", false);
    body.put("message", "Validation failed");
    body.put("errors", errors);
    return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
  }
}
