package com.eventbooking.mapper;

import com.eventbooking.dto.booking.TicketResponse;
import com.eventbooking.dto.event.EventInfo;
import com.eventbooking.entity.Booking;
import com.eventbooking.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

  @Mapping(target = "ticketId", source = "id")
  @Mapping(target = "event", source = "event")
  @Mapping(target = "status", expression = "java(booking.getStatus().name())")
  TicketResponse toTicketResponse(Booking booking);

  EventInfo toEventInfo(Event event);
}

