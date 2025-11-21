package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.common.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BaseException {
  public EntityNotFoundException(ErrorCode errorCode, String message) {
    super(errorCode, message, HttpStatus.NOT_FOUND);
  }
}
