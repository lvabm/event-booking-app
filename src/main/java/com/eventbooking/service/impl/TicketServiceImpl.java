package com.eventbooking.service.impl;

import com.eventbooking.dto.booking.TicketResponse;
import com.eventbooking.entity.Booking;
import com.eventbooking.mapper.TicketMapper;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final BookingRepository bookingRepository;
  private final TicketMapper ticketMapper;

  @Override
  public List<TicketResponse> getMyTickets(Long userId) {
    List<Booking> bookings = bookingRepository.findByUserIdWithEvent(userId);

    return bookings.stream()
        .map(ticketMapper::toTicketResponse)
        .collect(Collectors.toList());
  }
}

