package com.eventbooking.service.impl;

import com.eventbooking.common.constant.BookingStatus;
import com.eventbooking.common.constant.ErrorCode;
import com.eventbooking.dto.payment.PaymentRequest;
import com.eventbooking.dto.payment.PaymentResponse;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Payment;
import com.eventbooking.exception.BadRequestException;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.mapper.PaymentMapper;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.PaymentRepository;
import com.eventbooking.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Service;

@Service
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {
  PaymentRepository paymentRepo;
  BookingRepository bookingRepo;

  PaymentMapper paymentMapper;

  @Override
  public PaymentResponse processPayment(PaymentRequest request) {
    Booking booking = bookingRepo.findById(request.bookingId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOOKING_NOT_FOUND, "Booking not found"));

    if(booking.getStatus() == BookingStatus.PAID)
      throw new BadRequestException("Booking already paid");

    booking.setStatus(BookingStatus.PAID);

    Payment payment = paymentRepo.findByBooking(booking)
            .orElseGet(() -> paymentRepo.save(Payment.builder()
                    .amount(booking.getTotalPrice())
                    .booking(booking)
                    .build()));

    return paymentMapper.toResponse(paymentRepo.save(payment));
  }
}
