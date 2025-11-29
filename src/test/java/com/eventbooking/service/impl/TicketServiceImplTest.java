package com.eventbooking.service.impl;

import com.eventbooking.dto.booking.TicketResponse;
import com.eventbooking.dto.event.EventInfo;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Event;
import com.eventbooking.mapper.TicketMapper;
import com.eventbooking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

  // ======= Test fixtures & constants =======
  private static final Long USER_ID = 1L;
  private static final Long EMPTY_USER_ID = 5L;
  private static final Long BOOKING_ID = 1001L;
  private static final Long EVENT_ID = 101L;
  private static final LocalDateTime EVENT_DATE = LocalDateTime.of(2025, 5, 12, 10, 0);
  private static final String EVENT_TITLE = "Art Exhibition";
  private static final String EVENT_LOCATION = "Modern Art Gallery, New York";
  private static final int QUANTITY = 2;
  private static final String STATUS = "PAID";

  @Mock private BookingRepository bookingRepository;
  @Mock private TicketMapper ticketMapper;

  @InjectMocks private TicketServiceImpl ticketService;

  private Booking bookingFixture;
  private TicketResponse ticketResponseFixture;

  @BeforeEach
  void setUp() {
    bookingFixture = new Booking();
    bookingFixture.setId(BOOKING_ID);
    bookingFixture.setQuantity(QUANTITY);

    Event eventFixture = new Event();
    eventFixture.setId(EVENT_ID);
    eventFixture.setTitle(EVENT_TITLE);
    eventFixture.setDateTime(EVENT_DATE);
    eventFixture.setLocation(EVENT_LOCATION);

    bookingFixture.setEvent(eventFixture);

    ticketResponseFixture =
        TicketResponse.builder()
            .ticketId(BOOKING_ID)
            .quantity(QUANTITY)
            .status(STATUS)
            .event(
                EventInfo.builder()
                    .id(EVENT_ID)
                    .title(EVENT_TITLE)
                    .dateTime(EVENT_DATE)
                    .location(EVENT_LOCATION)
                    .build())
            .build();
  }

  @Test
  @DisplayName("getMyTickets: trả về TicketResponse khi user có dữ liệu trong bảng Booking")
  void getMyTickets_returnsList_whenTicketsExist() {
    when(bookingRepository.findByUserIdWithEvent(USER_ID)).thenReturn(List.of(bookingFixture));
    when(ticketMapper.toTicketResponse(bookingFixture)).thenReturn(ticketResponseFixture);

    List<TicketResponse> result = ticketService.getMyTickets(USER_ID);

    assertThat(result).containsExactly(ticketResponseFixture);

    verify(bookingRepository, times(1)).findByUserIdWithEvent(USER_ID);
    verify(ticketMapper, times(1)).toTicketResponse(bookingFixture);
    verifyNoMoreInteractions(bookingRepository, ticketMapper);
  }

  @Test
  void getMyTickets_returnsEmptyList_whenNoTickets() {
    when(bookingRepository.findByUserIdWithEvent(EMPTY_USER_ID))
        .thenReturn(Collections.emptyList());

    List<TicketResponse> result = ticketService.getMyTickets(EMPTY_USER_ID);

    assertThat(result).isEmpty();

    verify(bookingRepository, times(1)).findByUserIdWithEvent(EMPTY_USER_ID);
    verifyNoMoreInteractions(bookingRepository);
    verifyNoMoreInteractions(ticketMapper);
  }
}

