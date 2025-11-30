package com.eventbooking.controller;

import com.eventbooking.common.constant.ErrorCode;
import com.eventbooking.config.SpringSecurityConfig;
import com.eventbooking.dto.event.EventDetailsResponse;
import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.security.JwtAuthenticationFilter;
import com.eventbooking.service.EventService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
@AutoConfigureMockMvc()
@Import(SpringSecurityConfig.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventControllerTest {
    final static String API = "/api/events";

    final Long EVENT_ID = 1L;
    final String validRequestBuilder = """
                                    {
                                        "title": "Concert",
                                        "dateTime": "2025-12-01T10:20:30",
                                        "location": "New York",
                                        "price": 55.55,
                                        "description": "Music concert event",
                                        "imageUrl": "http://example.com/image.jpg"
                                    }
                                """;
    final String invalidRequestBuilder = """
                                    {
                                    "title": ""
                                    }
                                """;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EventService eventService;

    EventRequest request;
    EventDetailsResponse response;

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

    @BeforeEach
    void initData() {
        String TITLE = "Concert";
        String DATETIME = "2025-12-01T10:20:30";
        String LOCATION = "New York";
        BigDecimal PRICE = new BigDecimal("55.55");
        String DESCRIPTION = "Music concert event";
        String IMAGEURL = "http://example.com/image.jpg";


        request = new EventRequest(
                TITLE,
                DATETIME,
                LOCATION,
                PRICE,
                DESCRIPTION,
                IMAGEURL
        );

        response = EventDetailsResponse.builder()
                .id(EVENT_ID)
                .location(LOCATION)
                .dateTime(LocalDateTime.parse(DATETIME))
                .price(PRICE)
                .title(TITLE)
                .description(DESCRIPTION)
                .imageUrl(IMAGEURL)
                .build();
    }

    // ==================================================================================
    // createEvent tests
    // ==================================================================================
    @Test
    @WithMockUser(username = "testuser@example.com", roles = "ADMIN")
    @DisplayName("POST /api/events — returns 201 OK when valid data")
    void create_returns201_whenValidData() throws Exception {
        when(eventService.create(request)).thenReturn(response);

        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBuilder))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event created successfully"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(eventService).create(request);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    @DisplayName("POST /api/events — returns 422 Unprocessable Entity when invalid data")
    void create_returns422_whenInvalidData() throws Exception {
        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBuilder))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Validation Error"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());

        verify(eventService, never()).create(Mockito.any());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    @DisplayName("POST /api/events/... — returns 403 Forbidden when user is not admin")
    void create_returns403_whenUserIsNotAdmin() throws Exception {
        when(eventService.create(request)).thenReturn(response);

        mockMvc.perform(post(API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBuilder))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Forbidden – You do not have permission to perform this action"));
    }

    // ==================================================================================
    // updateEvent tests
    // ==================================================================================
    @Test
    @WithMockUser(username = "testuser@example.com", roles = "ADMIN")
    @DisplayName("PUT /api/events/{id} — returns 200 OK when valid data")
    void update_returns200_whenValidData() throws Exception {
        when(eventService.update(EVENT_ID, request)).thenReturn(response);

        mockMvc.perform(put(API + "/" + EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBuilder))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event updated successfully"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(eventService).update(EVENT_ID, request);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    @DisplayName("PUT /api/events/{id} — returns 422 Unprocessable Entity when invalid data")
    void update_returns422_whenInvalidData() throws Exception {
        mockMvc.perform(put(API + "/" + EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBuilder))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Validation Error"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());

        verify(eventService, never()).update(anyLong(), any());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    @DisplayName("PUT /api/events/{id}  — returns 403 Forbidden when user is not admin")
    void update_returns403_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(put(API + "/" + EVENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBuilder))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Forbidden – You do not have permission to perform this action"));

        verify(eventService, never()).update(anyLong(), any());
    }

    // ==================================================================================
    // deleteEvent tests
    // ==================================================================================
    @Test
    @WithMockUser(username = "testuser@example.com", roles = "ADMIN")
    @DisplayName("DELETE /api/events/{id} — returns 202 No content when event found")
    void delete_returns202_whenEventFound() throws Exception {
        doNothing().when(eventService).delete(EVENT_ID);

        mockMvc.perform(delete(API + "/" + EVENT_ID))
                .andExpect(status().isNoContent());

        verify(eventService).delete(EVENT_ID);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "ADMIN")
    @DisplayName("DELETE /api/events/{id} — returns 404 Not Found when event not found")
    void delete_returns404_whenEventNotFound() throws Exception {
        doThrow(new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND, "Event not found"))
                .when(eventService).delete(EVENT_ID);

        mockMvc.perform(delete(API + "/" + EVENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Event not found"));

        verify(eventService).delete(anyLong());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    @DisplayName("DELETE /api/events/{id}  — returns 403 Forbidden when user is not admin")
    void delete_returns403_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(delete(API + "/" + EVENT_ID))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("Forbidden – You do not have permission to perform this action"));

        verify(eventService, never()).delete(anyLong());
    }

    // ==================================================================================
    // getDetailsEvent tests
    // ==================================================================================
    @Test
    @DisplayName("GET /api/events/{id} — returns 200 OK when event found")
    void getDetails_returns200_whenEventFound() throws Exception {
        when(eventService.getById(EVENT_ID)).thenReturn(response);

        mockMvc.perform(get(API + "/" + EVENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Event detail fetched successfully"))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(eventService).getById(EVENT_ID);
        verifyNoMoreInteractions(eventService);
    }

    @Test
    @DisplayName("GET /api/events/{id} — returns 404 Not Found when event not found")
    void getDetails_returns404_whenEventNotFound() throws Exception {
        doThrow(new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND, "Event not found"))
                .when(eventService).getById(EVENT_ID);

        mockMvc.perform(get(API + "/" + EVENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Event not found"));

        verify(eventService).getById(anyLong());
    }

    // ==================================================================================
    // listEvent tests
    // ==================================================================================
    @Test
    @DisplayName("GET /api/events — returns 200 with valid params")
    void listEvents_returns200_whenRequestIsValid() throws Exception {
        EventResponse event =
                EventResponse.builder()
                        .id(101L)
                        .title("Art Exhibition")
                        .dateTime(LocalDateTime.of(2025, 5, 12, 10, 0))
                        .location("Modern Art Gallery, New York")
//                        .price(BigDecimal.valueOf(25.0))
//                        .description("Lorem ipsum dolor sit amet...")
//                        .imageUrl("https://cdn.example.com/events/art_exhibition.jpg")
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
