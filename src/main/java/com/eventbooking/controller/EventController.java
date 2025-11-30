package com.eventbooking.controller;

import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.dto.event.EventDetailsResponse;
import com.eventbooking.dto.event.EventListRequest;
import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.service.EventService;
import com.eventbooking.util.ApiResponseBuilder;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/events")
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventController {
  EventService service;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping()
  public ResponseEntity<BaseResponse<EventDetailsResponse>> create(@Valid @RequestBody EventRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponseBuilder.success("Event created successfully", service.create(request)));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<BaseResponse<EventDetailsResponse>> update(
          @PathVariable Long id,
          @Valid @RequestBody EventRequest request) {
    return ResponseEntity.ok(
            ApiResponseBuilder.success("Event updated successfully", service.update(id, request)));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<BaseResponse<EventDetailsResponse>> eventDetail(@PathVariable Long id) {
    return ResponseEntity.ok(
            ApiResponseBuilder.success("Event detail fetched successfully", service.getById(id)));
  }

  @GetMapping
  public ResponseEntity<BaseResponse<List<EventResponse>>> listEvents(
          @Valid @ModelAttribute EventListRequest request) {

    Page<EventResponse> events =
            service.search(request.toCriteria(), request.toPageable());

    BaseResponse<List<EventResponse>> response =
            ApiResponseBuilder.success("Events fetched successfully", events.getContent());
    return ResponseEntity.ok(response);
  }
}
