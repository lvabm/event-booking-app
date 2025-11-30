package com.eventbooking.mapper;

import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.dto.event.EventRequest;
import com.eventbooking.dto.event.EventDetailsResponse;
import com.eventbooking.entity.Event;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.projection.NearbyEventProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "dateTime", source = "dateTime", qualifiedByName = "stringToLocalDate")
    Event toEntity(EventRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "dateTime", source = "dateTime", qualifiedByName = "stringToLocalDate")
    void toEntity(@MappingTarget Event event, EventRequest request);

    EventDetailsResponse toDetailsResponse(Event event);
    EventResponse toResponse(Event event);
    EventResponse toResponse(NearbyEventProjection projection);

    @Named("stringToLocalDate")
    default LocalDateTime stringToLocalDate(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) return null;
        return LocalDateTime.parse(dateTime);
    }
}
