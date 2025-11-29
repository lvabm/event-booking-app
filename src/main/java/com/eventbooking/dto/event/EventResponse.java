package com.eventbooking.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventResponse {
    Long id;
    String title;
    LocalDateTime dateTime;
    String location;
}
