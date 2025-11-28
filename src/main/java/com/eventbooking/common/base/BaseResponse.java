package com.eventbooking.common.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
  private boolean success;
  private String message;
  private T data;
  Object errors;
  private Instant timestamp = Instant.now();
}
