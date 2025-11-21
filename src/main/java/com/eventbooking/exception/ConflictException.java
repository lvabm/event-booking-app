package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.common.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
  public ConflictException(String message) {
    super(ErrorCode.CONFLICT, message, HttpStatus.CONFLICT);
  }

  public ConflictException(ErrorCode errorCode, String message) {
    super(errorCode, message, HttpStatus.CONFLICT);
  }
}
