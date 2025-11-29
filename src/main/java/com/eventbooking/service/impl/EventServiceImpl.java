package com.eventbooking.service.impl;

import com.eventbooking.common.constant.ErrorCode;
import com.eventbooking.dto.event.EventDetailsResponse;
import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.dto.event.EventSearchCriteria;
import com.eventbooking.entity.Event;
import com.eventbooking.exception.BadRequestException;
import com.eventbooking.exception.EntityNotFoundException;
import com.eventbooking.mapper.EventMapper;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.service.EventService;
import com.eventbooking.service.GeocodingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {
  EventRepository repo;
  EventMapper mapper;

  GeocodingService geocodingService;

  @Override
  public EventDetailsResponse create(EventRequest request) {
    BigDecimal[] coords = geocodingService.getCoordinates(request.location()).orElseThrow(() ->
            new BadRequestException("Location not existed!"));

    Event event = mapper.toEntity(request);
    event.setLatitude(coords[0]);
    event.setLongitude(coords[1]);

    return mapper.toResponse(repo.save(event));
  }

  @Override
  public EventDetailsResponse update(Long id, EventRequest request) {
    Event event = repo.findById(id).orElseThrow(() ->
            new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND,"Event not found"));

    BigDecimal[] coords = geocodingService.getCoordinates(event.getLocation()).orElseThrow(() ->
            new BadRequestException("Location not existed!"));

    mapper.toEntity(event, request);
    event.setLatitude(coords[0]);
    event.setLongitude(coords[1]);

    return mapper.toResponse(repo.save(event));
  }

  @Override
  public void delete(Long id) {
    Event event = repo.findById(id).orElseThrow(() ->
            new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND,"Event not found"));

    repo.delete(event);
  }

  @Override
  public Page<EventResponse> search(EventSearchCriteria criteria, Pageable pageable) {
    return null;
  }

  @Override
  public EventDetailsResponse getById(Long id) {
    Event event = repo.findById(id).orElseThrow(() ->
            new EntityNotFoundException(ErrorCode.EVENT_NOT_FOUND,"Event not found"));

    return mapper.toResponse(event);
  }
}
