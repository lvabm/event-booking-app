package com.eventbooking.service.impl;

import com.eventbooking.dto.event.EventCreateRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.dto.event.EventSearchCriteria;
import com.eventbooking.dto.event.EventUpdateRequest;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
  private final EventRepository eventRepository;

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
    return null;
  }

  @Override
  public EventResponse getById(Long id) {
    return null;
  }
}
