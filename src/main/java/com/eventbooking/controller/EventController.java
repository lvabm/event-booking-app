package com.eventbooking.controller;

import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.dto.event.EventListRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.service.EventService;
import com.eventbooking.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
public class EventController {

  private final EventService eventService;

  @GetMapping
  public ResponseEntity<BaseResponse<List<EventResponse>>> listEvents(
      @Valid @ModelAttribute EventListRequest request) {

    Page<EventResponse> events =
        eventService.search(request.toCriteria(), request.toPageable());

    BaseResponse<List<EventResponse>> response =
        ApiResponseBuilder.success("Events fetched successfully", events.getContent());
    return ResponseEntity.ok(response);
  }
}


