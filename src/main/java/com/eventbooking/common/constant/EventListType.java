package com.eventbooking.common.constant;

import java.util.Arrays;
import java.util.Optional;

public enum EventListType {
  POPULAR("popular"),
  UPCOMING("upcoming"),
  NEARBY("nearby");

  private final String value;

  EventListType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static Optional<EventListType> fromValue(String raw) {
    if (raw == null || raw.isBlank()) {
      return Optional.empty();
    }
    String needle = raw.trim().toLowerCase();
    return Arrays.stream(values())
        .filter(type -> type.value.equals(needle))
        .findFirst();
  }
}


