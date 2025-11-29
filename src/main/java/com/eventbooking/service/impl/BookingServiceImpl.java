package com.eventbooking.service.impl;

import com.eventbooking.common.constant.BookingStatus;
import com.eventbooking.common.constant.ErrorCode;
import com.eventbooking.dto.booking.BookingCreateRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.dto.booking.TicketResponse;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Event;
import com.eventbooking.entity.Payment;
import com.eventbooking.entity.User;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.exception.UnauthorizedException;
import com.eventbooking.mapper.BookingMapper;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.BookingService;
import com.eventbooking.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
  BookingRepository bookingRepo;
  EventRepository eventRepo;
  UserRepository userRepo;

  BookingMapper mapper;


  @Override
  public BookingResponse create(BookingCreateRequest request) {
    var authen = SecurityContextHolder.getContext().getAuthentication();

    if (authen == null || authen.getPrincipal() == null) {
      throw new UnauthorizedException("Unauthorized - please login and try again");
    }

    Object principal = authen.getPrincipal();

    User user = userRepo.findByEmail(principal.toString())
            .orElseThrow(() -> new UnauthorizedException("Unauthorized - please login and try again"));

    Event event = eventRepo.findById(request.eventId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND, "Event not found"));

    BigDecimal totalPrice = event.getPrice().multiply(new BigDecimal(request.quantity()));

    Booking booking = new Booking(user, event, new Payment());

    mapper.toEntity(booking, request);
    booking.setTotalPrice(totalPrice);
    booking.setStatus(BookingStatus.PENDING);

    return mapper.toBookingResponse(bookingRepo.save(booking));
  }

  @Override
  public List<TicketResponse> getMyTickets() {
    return List.of();
  }
}
