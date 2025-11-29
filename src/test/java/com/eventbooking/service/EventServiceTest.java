package com.eventbooking.service;

import com.eventbooking.dto.event.EventDetailsResponse;
import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.entity.Event;
import com.eventbooking.exception.BadRequestException;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.mapper.EventMapper;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.service.impl.EventServiceImpl;
import com.eventbooking.service.impl.GeocodingServiceImpl;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceTest {
    @Mock  EventRepository eventRepo;
    @Mock  EventMapper eventMapper;
    @Mock
    GeocodingServiceImpl geocodingService;

    @InjectMocks
    EventServiceImpl eventService;

     Event event;
     EventRequest request;
     EventDetailsResponse response;

     final Long EVENT_ID = 1L;
     final String TITLE = "Concert";
     final String DATETIME = "2025-12-01T10:20:30";
     final String LOCATION = "New York";
     final BigDecimal LATITUDE = new BigDecimal("40.7128");
     final BigDecimal LONGITUDE = new BigDecimal("-74.0060");
     final BigDecimal PRICE = new BigDecimal("50.00");
     final String DESCRIPTION = "Music concert event";
     final String IMAGEURL = "http://example.com/image.jpg";

    @BeforeEach
    public void initData() {

        event = Event.builder()
                .id(EVENT_ID)
                .location(LOCATION)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .dateTime(LocalDateTime.parse(DATETIME))
                .price(PRICE)
                .title(TITLE)
                .description(DESCRIPTION)
                .imageUrl(IMAGEURL)
                .build();

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
    @DisplayName("create: trả về EventResponse khi location tồn tại")
    public void create_returnEventDetailsResponse_whenLocationExisted() {
        when(geocodingService.getCoordinates(request.location())).thenReturn(Optional.of(new BigDecimal[]{
                LATITUDE,
                LONGITUDE
        }));
        when(eventMapper.toEntity(request)).thenReturn(event);
        when(eventRepo.save(event)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(response);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        EventDetailsResponse actual = eventService.create(request);

        assertNotNull(actual);
        assertEquals(EventDetailsResponse.builder()
                .id(EVENT_ID)
                .location(LOCATION)
                .dateTime(LocalDateTime.parse(DATETIME))
                .price(PRICE)
                .title(TITLE)
                .description(DESCRIPTION)
                .imageUrl(IMAGEURL)
                .build(), actual);

        verify(eventRepo).save(eventCaptor.capture());
        assertEquals(LATITUDE, eventCaptor.getValue().getLatitude());
        assertEquals(LONGITUDE, eventCaptor.getValue().getLongitude());

        verify(geocodingService).getCoordinates(eq(request.location()));
        verify(eventMapper).toEntity(eq(request));
        verify(eventMapper).toResponse(eq(event));
    }

    @Test
    @DisplayName("create: ném lỗi BadRequest khi location không tồn tại")
    public void create_throwBadRequest_whenLocationNotExists() {
        request = new EventRequest(
                TITLE,
                DATETIME,
                "UNKNOWN LOCATION",
                PRICE,
                DESCRIPTION,
                IMAGEURL
        );

        when(geocodingService.getCoordinates(request.location())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> eventService.create(request));

        verify(geocodingService).getCoordinates(eq(request.location()));
        verify(eventMapper, never()).toEntity(any(EventRequest.class));
        verify(eventRepo, never()).save(any(Event.class));
        verify(eventMapper, never()).toResponse(any(Event.class));
    }

    // ==================================================================================
    // updateEvent tests
    // ==================================================================================
    @Test
    @DisplayName("update: trả về EventResponse khi location và Event đều tồn tại")
    public void update_returnEventDetailsResponse_whenAllVaid() {
        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(geocodingService.getCoordinates(anyString())).thenReturn(Optional.of(new BigDecimal[]{
                LATITUDE,
                LONGITUDE
        }));
        doNothing().when(eventMapper).toEntity(event, request);
        when(eventRepo.save(event)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(response);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        EventDetailsResponse actual = eventService.update(EVENT_ID, request);

        assertNotNull(actual);
        assertEquals(EventDetailsResponse.builder()
                .id(EVENT_ID)
                .location(LOCATION)
                .dateTime(LocalDateTime.parse(DATETIME))
                .price(PRICE)
                .title(TITLE)
                .description(DESCRIPTION)
                .imageUrl(IMAGEURL)
                .build(), actual);

        verify(eventRepo).save(eventCaptor.capture());
        assertEquals(LATITUDE, eventCaptor.getValue().getLatitude());
        assertEquals(LONGITUDE, eventCaptor.getValue().getLongitude());

        verify(eventRepo).findById(eq(EVENT_ID));
        verify(geocodingService).getCoordinates(eq(request.location()));
        verify(eventMapper).toEntity(eq(event), eq(request));
        verify(eventMapper).toResponse(eq(event));
    }

    @Test
    @DisplayName("update: ném lỗi NotFound khi Event không tồn tại")
    public void update_throwNotFound_whenEventNotExists() {
        Long invalidEVENT_ID = 999L;
        when(eventRepo.findById(invalidEVENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.update(invalidEVENT_ID, request));

        verify(eventRepo).findById(eq(invalidEVENT_ID));
        verify(geocodingService, never()).getCoordinates(any(String.class));
        verify(eventMapper, never()).toEntity(any(Event.class), any(EventRequest.class));
        verify(eventRepo, never()).save(any(Event.class));
        verify(eventMapper, never()).toResponse(any(Event.class));
    }

    @Test
    @DisplayName("update: ném lỗi BadRequest khi location không tồn tại")
    public void update_throwBadRequest_whenLocationNotExists() {
        request = new EventRequest(
                TITLE,
                DATETIME,
                "UNKNOWN LOCATION",
                PRICE,
                DESCRIPTION,
                IMAGEURL
        );

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(geocodingService.getCoordinates(request.location())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> eventService.update(EVENT_ID, request));

        verify(eventRepo).findById(eq(EVENT_ID));
        verify(geocodingService).getCoordinates(eq(request.location()));
        verify(eventMapper, never()).toEntity(any(Event.class), any(EventRequest.class));
        verify(eventRepo, never()).save(any(Event.class));
        verify(eventMapper, never()).toResponse(any(Event.class));
    }

    // ==================================================================================
    // deleteEvent tests
    // ==================================================================================
    @Test
    @DisplayName("delete: xoá thành công Event khi Event tồn tại")
    public void delete_success_whenEventExists() {
        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));

        eventService.delete(EVENT_ID);

        verify(eventRepo).findById(eq(EVENT_ID));
        verify(eventRepo).delete(eq(event));
    }

    @Test
    @DisplayName("delete: ném lỗi NotFound khi Event không tồn tại")
    public void delete_throwNotFound_whenEventNotExists() {
        Long invalidEVENT_ID = 99L;
        when(eventRepo.findById(invalidEVENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.delete(invalidEVENT_ID));

        verify(eventRepo).findById(eq(invalidEVENT_ID));
        verify(eventRepo, never()).delete(any(Event.class));
    }

    // ==================================================================================
    // getEventById tests
    // ==================================================================================
    @Test
    @DisplayName("getById: trả về EventDetailsResponse khi Event tồn tại")
    public void getById_returnEventDetailsResponse_whenEventExists() {
        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventDetailsResponse actual = eventService.getById(EVENT_ID);

        assertEquals(EventDetailsResponse.builder()
                .id(EVENT_ID)
                .location(LOCATION)
                .dateTime(LocalDateTime.parse(DATETIME))
                .price(PRICE)
                .title(TITLE)
                .description(DESCRIPTION)
                .imageUrl(IMAGEURL)
                .build(), actual);

        verify(eventRepo).findById(eq(EVENT_ID));
        verify(eventMapper).toResponse(eq(event));
    }

    @Test
    @DisplayName("getById: ném lỗi NotFound khi Event không tồn tại")
    public void getById_throwNotFound_whenEventNotExists() {
        Long invalidEVENT_ID = 99L;
        when(eventRepo.findById(invalidEVENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getById(invalidEVENT_ID));

        verify(eventRepo).findById(eq(invalidEVENT_ID));
        verify(eventMapper, never()).toResponse(any(Event.class));
    }
}
