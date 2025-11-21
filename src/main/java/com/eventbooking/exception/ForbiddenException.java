package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.common.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
  public ForbiddenException(String message) {
    super(ErrorCode.FORBIDDEN, message, HttpStatus.FORBIDDEN);
  }

  public ForbiddenException(ErrorCode errorCode, String message) {
    super(errorCode, message, HttpStatus.FORBIDDEN);
  }
}
