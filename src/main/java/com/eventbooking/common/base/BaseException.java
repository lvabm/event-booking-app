package com.eventbooking.common.base;

import com.eventbooking.common.constant.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
  private final ErrorCode errorCode;
  private final HttpStatus httpStatus;
  @Setter private String path;

  public BaseException(ErrorCode errorCode, String message, HttpStatus httpStatus) {
    super(message);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
  }
}
