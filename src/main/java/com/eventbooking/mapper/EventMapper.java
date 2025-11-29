package com.eventbooking.mapper;

import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.entity.Event;
import com.eventbooking.repository.projection.NearbyEventProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

  EventResponse toResponse(Event event);

  EventResponse toResponse(NearbyEventProjection projection);
}


