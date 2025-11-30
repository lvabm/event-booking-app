package com.eventbooking.service;

import com.eventbooking.common.constant.BookingStatus;
import com.eventbooking.common.constant.PaymentStatus;
import com.eventbooking.dto.payment.PaymentRequest;
import com.eventbooking.dto.payment.PaymentResponse;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Payment;
import com.eventbooking.exception.BadRequestException;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.mapper.PaymentMapper;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.PaymentRepository;
import com.eventbooking.service.impl.PaymentServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepo;

    @Mock
    BookingRepository bookingRepo;

    @Mock
    PaymentMapper paymentMapper;

    @InjectMocks
    PaymentServiceImpl service;

    Payment payment;
    Booking booking;
    PaymentRequest request;
    PaymentResponse response;

    final Long BOOKING_ID = 1L;
    final BigDecimal TOTAL_PRICE = new BigDecimal("250.00");

    @BeforeEach
    public void initData() {
        booking = Booking.builder()
                .id(BOOKING_ID)
                .status(BookingStatus.PENDING)
                .totalPrice(TOTAL_PRICE)
                .build();

        payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.PAID)
                .build();

        request = new PaymentRequest(BOOKING_ID, "0000111122223333", "123", "12/25");

        response = PaymentResponse.builder()
                .bookingId(BOOKING_ID)
                .amount(TOTAL_PRICE)
                .status(PaymentStatus.PAID)
                .build();
    }

    @Test
    @DisplayName("processPayment: trả về PaymentResponse khi Booking tồn tại và chưa được thanh toán")
    public void processPayment_returnsBookingDto_whenBookingFound() {
        when(bookingRepo.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(paymentRepo.findByBooking(booking)).thenReturn(Optional.of(payment));
        when(paymentRepo.save(payment)).thenReturn(payment);
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        PaymentResponse actual = service.processPayment(request);

        assertEquals(PaymentResponse.builder()
                .bookingId(BOOKING_ID)
                .amount(TOTAL_PRICE)
                .status(PaymentStatus.PAID)
                .build(), actual);

        verify(paymentRepo).save(paymentCaptor.capture());
        assertEquals(BookingStatus.PAID, paymentCaptor.getValue().getBooking().getStatus());

        verify(bookingRepo).findById(eq(BOOKING_ID));
        verify(paymentRepo).findByBooking(eq(booking));
        verify(paymentMapper).toResponse(eq(payment));
    }

    @Test
    @DisplayName("processPayment: ném EntityNotFoundException khi Booking không tồn tại")
    public void processPayment_throwsNotFound_whenBookingNotFound() {
        Long nonExistentEventId = 999L;

        when(bookingRepo.findById(nonExistentEventId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.processPayment(new PaymentRequest(
                nonExistentEventId, "0000111122223333", "123", "12/25")));

        verify(bookingRepo).findById(eq(nonExistentEventId));
        verify(paymentRepo, never()).findByBooking(any(Booking.class));
        verify(paymentRepo, never()).save(any(Payment.class));
        verify(paymentMapper, never()).toResponse(any(Payment.class));
    }

    @Test
    @DisplayName("processPayment: ném BadRequestException khi Booking đã được thanh toán")
    public void processPayment_throwsBadRequest_whenBookingAlreadyPaid() {
        booking.setStatus(BookingStatus.PAID);

        when(bookingRepo.findById(BOOKING_ID)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> service.processPayment(request));

        verify(bookingRepo).findById(eq(BOOKING_ID));
        verify(paymentRepo, never()).findByBooking(any(Booking.class));
        verify(paymentRepo, never()).save(any(Payment.class));
        verify(paymentMapper, never()).toResponse(any(Payment.class));
    }
}

