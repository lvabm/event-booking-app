package com.eventbooking.service.impl;

import com.eventbooking.common.constant.ErrorCode;
import com.eventbooking.dto.event.EventDetailsResponse;
import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.common.constant.EventListType;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.dto.event.EventSearchCriteria;
import com.eventbooking.entity.Event;
import com.eventbooking.mapper.EventMapper;
import com.eventbooking.exception.BadRequestException;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.projection.NearbyEventProjection;
import com.eventbooking.service.EventService;
import com.eventbooking.service.GeocodingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
  EventRepository eventRepo;
  EventMapper eventMapper;

  GeocodingService geocodingService;

  @Override
  public EventDetailsResponse create(EventRequest request) {
    BigDecimal[] coords = geocodingService.getCoordinates(request.location()).orElseThrow(() ->
            new BadRequestException("Location not existed!"));

    Event event = eventMapper.toEntity(request);
    event.setLatitude(coords[0]);
    event.setLongitude(coords[1]);

    return eventMapper.toDetailsResponse(eventRepo.save(event));
  }

  @Override
  public EventDetailsResponse update(Long id, EventRequest request) {
    Event event = eventRepo.findById(id).orElseThrow(() ->
            new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND,"Event not found"));

    BigDecimal[] coords = geocodingService.getCoordinates(event.getLocation()).orElseThrow(() ->
            new BadRequestException("Location not existed!"));

    eventMapper.toEntity(event, request);
    event.setLatitude(coords[0]);
    event.setLongitude(coords[1]);

    return eventMapper.toDetailsResponse(eventRepo.save(event));
  }

  @Override
  public void delete(Long id) {
    Event event = eventRepo.findById(id).orElseThrow(() ->
            new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND,"Event not found"));

    eventRepo.delete(event);
  }

  @Override
  public Page<EventResponse> search(EventSearchCriteria criteria, Pageable pageable) {
    String keyword = normalizeKeyword(criteria.getSearch());
    EventListType type = criteria.getType();

    Page<Event> events;
    if (type == EventListType.POPULAR) {
      events = eventRepo.findPopularEvents(keyword, pageable);
    } else if (type == EventListType.UPCOMING) {
      events = eventRepo.findUpcomingEvents(LocalDateTime.now(), keyword, pageable);
    } else if (type == EventListType.NEARBY) {
      if (!criteria.hasUserCoordinates()) {
        throw new IllegalArgumentException("User coordinates are required for nearby search");
      }
      return findNearbyEvents(criteria, keyword, pageable);
    } else {
      events = eventRepo.findAllWithSearch(keyword, pageable);
    }

    return events.map(eventMapper::toResponse);
  }

  @Override
  public EventDetailsResponse getById(Long id) {
    Event event = eventRepo.findById(id).orElseThrow(() ->
            new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND,"Event not found"));

    return eventMapper.toDetailsResponse(event);
  }

  private Page<EventResponse> findNearbyEvents(
      EventSearchCriteria criteria, String keyword, Pageable pageable) {
    double radius =
        criteria.getRadiusKm() != null ? criteria.getRadiusKm() : EventSearchCriteria.DEFAULT_RADIUS_KM;

    Page<NearbyEventProjection> resultPage =
        eventRepo.findNearbyEvents(
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
