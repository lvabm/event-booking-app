package com.eventbooking.dto.payment;

import com.eventbooking.common.constant.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    Long paymentId;
    Long bookingId;
    BigDecimal amount;
    PaymentStatus status;
}

