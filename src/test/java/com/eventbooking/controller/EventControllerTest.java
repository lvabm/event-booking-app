package com.eventbooking.controller;

import com.eventbooking.config.SpringSecurityConfig;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.security.JwtAuthenticationFilter;
import com.eventbooking.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
@Import(SpringSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true)
class EventControllerTest {

    private static final String API = "/api/events";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setupSecurityFilterPassThrough() throws Exception {
        Mockito.doAnswer(
                        invocation -> {
                            var request =
                                    invocation.getArgument(0, jakarta.servlet.http.HttpServletRequest.class);
                            var response =
                                    invocation.getArgument(1, jakarta.servlet.http.HttpServletResponse.class);
                            var chain = invocation.getArgument(2, jakarta.servlet.FilterChain.class);
                            chain.doFilter(request, response);
                            return null;
                        })
                .when(jwtAuthenticationFilter)
                .doFilter(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("GET /api/events — returns 200 with valid params")
    void listEvents_returns200_whenRequestIsValid() throws Exception {
        EventResponse event =
                EventResponse.builder()
                        .id(101L)
                        .title("Art Exhibition")
                        .dateTime(LocalDateTime.of(2025, 5, 12, 10, 0))
                        .location("Modern Art Gallery, New York")
            .price(BigDecimal.valueOf(25.0))
            .description("Lorem ipsum dolor sit amet...")
            .imageUrl("https://cdn.example.com/events/art_exhibition.jpg")
                        .build();

        when(eventService.search(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(event), PageRequest.of(0, 10), 1));

        mockMvc
                .perform(
                        get(API)
                                .param("type", "popular")
                                .param("page", "1")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Events fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(101))
                .andExpect(jsonPath("$.data[0].title").value("Art Exhibition"))
                .andExpect(jsonPath("$.data[0].location").value("Modern Art Gallery, New York"))
                .andExpect(jsonPath("$.data[0].price").value(25.0));

        verify(eventService).search(any(), any(Pageable.class));
        verifyNoMoreInteractions(eventService);
    }

    @Test
    @DisplayName("GET /api/events — returns 422 when type is invalid")
    void listEvents_returns422_whenTypeInvalid() throws Exception {
        mockMvc
                .perform(get(API).param("type", "invalid").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("type"))
                .andExpect(jsonPath("$.errors[0].message").value("Invalid type value"));

        verify(eventService, never()).search(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/events — returns 422 when size is invalid")
    void listEvents_returns422_whenSizeInvalid() throws Exception {
        mockMvc
                .perform(get(API).param("size", "-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("size"))
                .andExpect(jsonPath("$.errors[0].message").value("Size must be a positive integer"));

        verify(eventService, never()).search(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/events — returns 422 when page is invalid")
    void listEvents_returns422_whenPageInvalid() throws Exception {
        mockMvc
                .perform(get(API).param("page", "-1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0].field").value("page"))
                .andExpect(jsonPath("$.errors[0].message").value("Page must be a positive integer"));

        verify(eventService, never()).search(any(), any(Pageable.class));
    }
}


