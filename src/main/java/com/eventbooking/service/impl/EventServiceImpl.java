package com.eventbooking.service.impl;

import com.eventbooking.common.constant.EventListType;
import com.eventbooking.dto.event.EventCreateRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.dto.event.EventSearchCriteria;
import com.eventbooking.dto.event.EventUpdateRequest;
import com.eventbooking.entity.Event;
import com.eventbooking.mapper.EventMapper;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.projection.NearbyEventProjection;
import com.eventbooking.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final EventMapper eventMapper;

  @Override
  public EventResponse create(EventCreateRequest request) {
    return null;
  }

  @Override
  public EventResponse update(Long id, EventUpdateRequest request) {
    return null;
  }

  @Override
  public void delete(Long id) {}

  @Override
  public Page<EventResponse> search(EventSearchCriteria criteria, Pageable pageable) {
    String keyword = normalizeKeyword(criteria.getSearch());
    EventListType type = criteria.getType();

    Page<Event> events;
    if (type == EventListType.POPULAR) {
      events = eventRepository.findPopularEvents(keyword, pageable);
    } else if (type == EventListType.UPCOMING) {
      events = eventRepository.findUpcomingEvents(LocalDateTime.now(), keyword, pageable);
    } else if (type == EventListType.NEARBY) {
      if (!criteria.hasUserCoordinates()) {
        throw new IllegalArgumentException("User coordinates are required for nearby search");
      }
      return findNearbyEvents(criteria, keyword, pageable);
    } else {
      events = eventRepository.findAllWithSearch(keyword, pageable);
    }

    return events.map(eventMapper::toResponse);
  }

  @Override
  public EventResponse getById(Long id) {
    return null;
  }

  private Page<EventResponse> findNearbyEvents(
      EventSearchCriteria criteria, String keyword, Pageable pageable) {
    double radius =
        criteria.getRadiusKm() != null ? criteria.getRadiusKm() : EventSearchCriteria.DEFAULT_RADIUS_KM;

    Page<NearbyEventProjection> resultPage =
        eventRepository.findNearbyEvents(
            keyword,
            criteria.getUserLatitude(),
            criteria.getUserLongitude(),
            radius,
            pageable);

    return resultPage.map(eventMapper::toResponse);
  }

  private String normalizeKeyword(String keyword) {
    return StringUtils.hasText(keyword) ? keyword.trim() : null;
  }
}
