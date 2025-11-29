package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.util.ApiResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(value = BaseException.class)
  public ResponseEntity<?> handlingRuntimeException(BaseException ex) {
    return ResponseEntity.status(ex.getHttpStatus())
            .body(ApiResponseBuilder.error(ex.getMessage(), null));
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<?> handlingValidationException(MethodArgumentNotValidException ex) {
    List<Map<String, String>> errors = new ArrayList<>();

    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      Map<String, String> error = new HashMap<>();
      error.put("field", fieldError.getField());
      error.put("message", fieldError.getDefaultMessage());
      errors.add(error);
    }

    return ResponseEntity.status(422)
            .body(ApiResponseBuilder.error("Validation Error", errors));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseEntity.status(403)
            .body(ApiResponseBuilder.error("Forbidden – You do not have permission to perform this action", null));
  }
}
