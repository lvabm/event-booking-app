package com.eventbooking.controller;

import com.eventbooking.dto.booking.BookingCreateRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.service.BookingService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {
    final String API = "/api/bookings";

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BookingService bookingService;

    BookingCreateRequest request;
    BookingResponse response;

    @BeforeEach
    void initData() {
        Long EVENT_ID = 1L;

        request = new BookingCreateRequest(EVENT_ID, 10);

        response = BookingResponse.builder()
                .bookingId(1L)
                .quantity(10)
                .event(EventResponse.builder()
                        .id(EVENT_ID)
                        .build())
                .build();
    }

    // ==================================================================================
    // createBooking tests
    // ==================================================================================
    @Test
    @DisplayName("POST /api/bookings — returns 201 CREATED when valid data")
    void createBooking_returns201_whenValidData() throws Exception {
        when(bookingService.create(request)).thenReturn(response);

        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": 1, \"quantity\": 10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking created successfully"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(bookingService).create(request);
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    @DisplayName("POST /api/bookings — returns 422 Unprocessable Entity when invalid data")
    void createBooking_returns422_whenInvalidData() throws Exception {
        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventId\": null, \"quantity\": 0}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Validation Error"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());

        verify(bookingService, never()).create(Mockito.any());
    }
}
