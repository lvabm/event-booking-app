package com.eventbooking.service.impl;

import com.eventbooking.common.constant.BookingStatus;
import com.eventbooking.common.constant.ErrorCode;
import com.eventbooking.dto.booking.BookingCreateRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Event;
import com.eventbooking.entity.Payment;
import com.eventbooking.entity.User;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.mapper.BookingMapper;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.service.BookingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
  BookingRepository bookingRepo;
  EventRepository eventRepo;

  BookingMapper mapper;

  UserServiceImpl userService;

  @Override
  public BookingResponse create(BookingCreateRequest request) {
    User user = userService.getCurrentUserEntity();

    Event event = eventRepo.findById(request.eventId())
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

    BigDecimal totalPrice = event.getPrice().multiply(new BigDecimal(request.quantity()));

    Booking booking = new Booking(user, event, new Payment(totalPrice));

    mapper.toEntity(booking, request);
    booking.setTotalPrice(totalPrice);
    booking.getPayment().setAmount(totalPrice);
    booking.setStatus(BookingStatus.PENDING);

    return mapper.toBookingResponse(bookingRepo.save(booking));
  }
}
