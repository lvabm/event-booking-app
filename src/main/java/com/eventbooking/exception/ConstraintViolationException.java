package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.common.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class ConstraintViolationException extends BaseException {
  public ConstraintViolationException(ErrorCode errorCode, String message) {
    super(errorCode, message, HttpStatus.BAD_REQUEST);
  }

  public ConstraintViolationException(String message) {
    super(ErrorCode.BAD_REQUEST, message, HttpStatus.BAD_REQUEST);
  }
}
