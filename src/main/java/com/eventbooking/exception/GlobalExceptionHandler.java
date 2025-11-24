package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // Phương thức chung để xây dựng Body phản hồi lỗi cho các Custom Exception
  private Map<String, Object> buildErrorBody(BaseException ex, String path) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", ex.getHttpStatus().value());
    body.put("errorCode", ex.getErrorCode());
    body.put("message", ex.getMessage());
    body.put("path", path);
    return body;
  }

  // 1. Bắt lỗi validation (DTO & Entity)
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    String path = request.getDescription(false);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());

    List<String> errors =
        ex.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());

    body.put("errors", errors);
    body.put("path", path.replace("uri=", "")); // Tối ưu hóa path hiển thị
    return new ResponseEntity<>(body, status);
  }

  // 2. Gộp TẤT CẢ các Custom Exception (Handlers 2, 3, 4, 5, 6, 7) vào một Handler duy nhất
  // đều kế thừa từ BaseException hoặc có cấu trúc interface chung để lấy HttpStatus và ErrorCode.
  @ExceptionHandler({
    ConstraintViolationException.class,
    EntityNotFoundException.class,
    BadRequestException.class,
    ConflictException.class,
    ForbiddenException.class,
    ResourceNotFoundException.class,
    UnauthorizedException.class
  })
  public ResponseEntity<Object> handleCustomExceptions(
      BaseException ex, HttpServletRequest request) {
    // Đối với UnauthorizedException, trả về format đơn giản theo yêu cầu
    if (ex instanceof UnauthorizedException) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("success", false);
      body.put("message", ex.getMessage());
      return new ResponseEntity<>(body, ex.getHttpStatus());
    }
    
    Map<String, Object> body = buildErrorBody(ex, request.getRequestURI());
    return new ResponseEntity<>(body, ex.getHttpStatus());
  }

  // 3. Bắt tất cả lỗi còn lại - GIỮ NGUYÊN
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAll(Exception ex, HttpServletRequest request) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("errorCode", "INTERNAL_SERVER_ERROR"); // Cung cấp mã lỗi chung
    body.put("message", "An unexpected error occurred. Please try again later."); // Thông báo chung
    body.put("path", request.getRequestURI());
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
