package com.eventbooking.util;

import com.eventbooking.common.base.BaseResponse;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
public final class ApiResponseBuilder {

  public static <T> BaseResponse<T> success(String message, T data) {
    return BaseResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(Instant.now())
        .build();
  }
}
