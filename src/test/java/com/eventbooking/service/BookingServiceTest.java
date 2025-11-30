package com.eventbooking.service;

import com.eventbooking.dto.booking.BookingCreateRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Event;
import com.eventbooking.entity.User;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.exception.UnauthorizedException;
import com.eventbooking.mapper.BookingMapper;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.service.impl.BookingServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceTest {

    @Mock
    BookingRepository bookingRepo;

    @Mock
    EventRepository eventRepo;

    @Mock
    UserRepository userRepo;

    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingServiceImpl service;

    User user;
    Event event;
    Booking booking;
    BookingCreateRequest request;
    BookingResponse response;

    final Long EVENT_ID = 1L;
    final int QUANTITY = 5;
    final BigDecimal PRICE = new BigDecimal("50.00");
    final BigDecimal TOTALPRICE = PRICE.multiply(BigDecimal.valueOf(QUANTITY));

    @BeforeEach
    public void initData() {
        user = User.builder()
                .email("testuser@gmail.com")
                .build();

        event = Event.builder()
                .id(EVENT_ID)
                .price(PRICE)
                .build();

        booking = Booking.builder()
                .event(event)
                .quantity(QUANTITY)
                .totalPrice(TOTALPRICE)
                .build();

        request = new BookingCreateRequest(EVENT_ID, QUANTITY);

        response = BookingResponse.builder()
                .bookingId(booking.getId())
                .quantity(QUANTITY)
                .totalPrice(TOTALPRICE)
                .event(EventResponse.builder()
                        .id(EVENT_ID)
                        .build())
                .build();
    }

    // ==================================================================================
    // createBooking tests
    // ==================================================================================
    @Test
    @DisplayName("create: return BookingResponse when all input is valid")
    public void create_returnsBookingResponse_whenAllVaid() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        doAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            BookingCreateRequest r = invocation.getArgument(1);
            b.setQuantity(r.quantity());
            return null;
        }).when(bookingMapper).toEntity(any(Booking.class), eq(request));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingResponse(booking)).thenReturn(response);

        BookingResponse actual = service.create(request);

        assertEquals(BookingResponse.builder()
                .bookingId(booking.getId())
                .quantity(QUANTITY)
                .totalPrice(TOTALPRICE)
                .event(EventResponse.builder()
                        .id(EVENT_ID)
                        .build())
                .build(), actual);

        verify(bookingMapper).toEntity(any(Booking.class), eq(request));
        verify(bookingRepo).save(any(Booking.class));
        verify(bookingMapper).toBookingResponse(eq(booking));
    }

    @Test
    @DisplayName("create: throw UnauthorizedException when User not login")
    public void create_throwsUnauthorize_whenEventNotFound() {
        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedException.class, () -> service.create(request));

        verify(userRepo, never()).findByEmail(any(String.class));
        verify(eventRepo, never()).findById(any(Long.class));
        verify(bookingMapper, never()).toEntity(any(Booking.class), eq(request));
        verify(bookingRepo, never()).save(any(Booking.class));
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    @DisplayName("create: throw EntityNotFoundException when Event not found")
    public void create_throwsNotFound_whenEventNotFound() {
        Long nonExistentEVENT_ID = 999L;
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(eventRepo.findById(nonExistentEVENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                service.create(new BookingCreateRequest(nonExistentEVENT_ID, QUANTITY)));

        verify(userRepo).findByEmail(any(String.class));
        verify(eventRepo).findById(eq(nonExistentEVENT_ID));
        verify(bookingMapper, never()).toEntity(any(Booking.class), eq(request));
        verify(bookingRepo, never()).save(any(Booking.class));
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }
}

