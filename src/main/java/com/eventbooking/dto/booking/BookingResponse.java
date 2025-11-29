package com.eventbooking.dto.booking;

import com.eventbooking.dto.event.EventResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    Long bookingId;
    EventResponse event;
    Integer quantity;
    BigDecimal totalPrice;
}