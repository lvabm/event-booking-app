package com.eventbooking.service;

import com.eventbooking.dto.payment.PaymentRequest;
import com.eventbooking.dto.payment.PaymentResponse;

public interface PaymentService {
  /** Xử lý thanh toán cho booking, kiểm tra bookingId và tình trạng thanh toán */
  PaymentResponse processPayment(PaymentRequest request);
}
