package com.eventbooking.exception;

import com.eventbooking.common.base.BaseException;
import com.eventbooking.common.constant.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
  public ResourceNotFoundException(String message) {
    super(ErrorCode.RESOURCE_NOT_FOUND, message, HttpStatus.NOT_FOUND);
  }

  public ResourceNotFoundException(ErrorCode errorCode, String message) {
    super(errorCode, message, HttpStatus.NOT_FOUND);
  }
}
