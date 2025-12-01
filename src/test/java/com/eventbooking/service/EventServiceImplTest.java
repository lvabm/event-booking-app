package com.eventbooking.service;

import com.eventbooking.common.constant.EventListType;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.dto.event.EventSearchCriteria;
import com.eventbooking.entity.Event;
import com.eventbooking.mapper.EventMapper;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.projection.NearbyEventProjection;
import com.eventbooking.repository.projection.NearbyEventProjectionStub;
import com.eventbooking.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

  private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 10);
  private static final String TRIMMED_KEYWORD = "art";
  private static final String RAW_KEYWORD = "  art  ";
  private static final double USER_LAT = 10.0;
  private static final double USER_LNG = 106.0;
  private static final double RADIUS = 25.0;

  @Mock private EventRepository eventRepository;
  @Mock private EventMapper eventMapper;

  @InjectMocks private EventServiceImpl eventService;

  private Event event;
  private EventResponse eventResponse;

  @BeforeEach
  void setUp() {
    event = new Event();
    event.setId(1L);
    event.setTitle("Art Exhibition");
    event.setLocation("Modern Art Gallery");
    event.setDateTime(LocalDateTime.of(2025, 5, 12, 10, 0));

    eventResponse =
        EventResponse.builder()
            .id(event.getId())
            .title(event.getTitle())
            .location(event.getLocation())
            .dateTime(event.getDateTime())
            .build();
  }

  @Test
  @DisplayName("search: trả về popular events khi type = POPULAR")
  void search_returnsPopularEvents_whenTypePopular() {
    EventSearchCriteria criteria =
        EventSearchCriteria.builder().type(EventListType.POPULAR).search(RAW_KEYWORD).build();
    Page<Event> repositoryResult = new PageImpl<>(List.of(event), DEFAULT_PAGEABLE, 1);

    when(eventRepository.findPopularEvents(TRIMMED_KEYWORD, DEFAULT_PAGEABLE))
        .thenReturn(repositoryResult);
    when(eventMapper.toResponse(event)).thenReturn(eventResponse);

    Page<EventResponse> result = eventService.search(criteria, DEFAULT_PAGEABLE);

    assertThat(result.getContent()).containsExactly(eventResponse);

    verify(eventRepository).findPopularEvents(TRIMMED_KEYWORD, DEFAULT_PAGEABLE);
    verify(eventMapper).toResponse(event);
    verifyNoMoreInteractions(eventRepository, eventMapper);
  }

  @Test
  @DisplayName("search: trả về upcoming events khi type = UPCOMING")
  void search_returnsUpcomingEvents_whenTypeUpcoming() {
    EventSearchCriteria criteria =
        EventSearchCriteria.builder().type(EventListType.UPCOMING).search(null).build();
    Page<Event> repositoryResult = new PageImpl<>(List.of(event), DEFAULT_PAGEABLE, 1);

    when(eventRepository.findUpcomingEvents(any(LocalDateTime.class), any(), any()))
        .thenReturn(repositoryResult);
    when(eventMapper.toResponse(event)).thenReturn(eventResponse);

    Page<EventResponse> result = eventService.search(criteria, DEFAULT_PAGEABLE);

    assertThat(result.getContent()).containsExactly(eventResponse);

    verify(eventRepository).findUpcomingEvents(any(LocalDateTime.class), any(), any());
    verify(eventMapper).toResponse(event);
  }

  @Test
  @DisplayName("search: trả về all events khi không truyền type")
  void search_returnsAllEvents_whenTypeNull() {
    EventSearchCriteria criteria = EventSearchCriteria.builder().search(TRIMMED_KEYWORD).build();
    Page<Event> repositoryResult = new PageImpl<>(List.of(event), DEFAULT_PAGEABLE, 1);

    when(eventRepository.findAllWithSearch(TRIMMED_KEYWORD, DEFAULT_PAGEABLE))
        .thenReturn(repositoryResult);
    when(eventMapper.toResponse(event)).thenReturn(eventResponse);

    Page<EventResponse> result = eventService.search(criteria, DEFAULT_PAGEABLE);

    assertThat(result.getContent()).containsExactly(eventResponse);

    verify(eventRepository).findAllWithSearch(TRIMMED_KEYWORD, DEFAULT_PAGEABLE);
    verify(eventMapper).toResponse(event);
    verifyNoMoreInteractions(eventRepository, eventMapper);
  }
  @Test
  @DisplayName("search: trả về danh sách nearby events khi cung cấp đầy đủ tọa độ")
  void search_returnsNearbyEvents_whenTypeNearby() {
    EventSearchCriteria criteria =
        EventSearchCriteria.builder()
            .type(EventListType.NEARBY)
            .userLatitude(USER_LAT)
            .userLongitude(USER_LNG)
            .radiusKm(RADIUS)
            .build();

    NearbyEventProjection projection =
        new NearbyEventProjectionStub(
            99L,
            "Music Festival",
            LocalDateTime.of(2025, 6, 1, 18, 0),
            "City Park",
            BigDecimal.valueOf(50),
            "Description",
            "https://cdn.example.com/events/music.jpg",
            BigDecimal.valueOf(USER_LAT),
            BigDecimal.valueOf(USER_LNG),
            5.0);

    Page<NearbyEventProjection> repositoryResult =
        new PageImpl<>(List.of(projection), DEFAULT_PAGEABLE, 1);

    when(eventRepository.findNearbyEvents(
            null, USER_LAT, USER_LNG, RADIUS, DEFAULT_PAGEABLE))
        .thenReturn(repositoryResult);
    when(eventMapper.toResponse(projection)).thenReturn(eventResponse);

    Page<EventResponse> result = eventService.search(criteria, DEFAULT_PAGEABLE);

    assertThat(result.getContent()).containsExactly(eventResponse);

    verify(eventRepository)
        .findNearbyEvents(null, USER_LAT, USER_LNG, RADIUS, DEFAULT_PAGEABLE);
    verify(eventMapper).toResponse(projection);
    verifyNoMoreInteractions(eventRepository, eventMapper);
  }

}
