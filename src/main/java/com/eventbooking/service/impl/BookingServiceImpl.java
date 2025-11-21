package com.eventbooking.service.impl;

import com.eventbooking.dto.booking.BookingCreateRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.dto.booking.TicketResponse;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
  private final BookingRepository bookingRepository;

  @Override
  public BookingResponse create(BookingCreateRequest request) {
    return null;
  }

  @Override
  public List<TicketResponse> getMyTickets() {
    return List.of();
  }
}
