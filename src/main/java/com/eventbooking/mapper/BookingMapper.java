package com.eventbooking.mapper;

import com.eventbooking.dto.booking.BookingCreateRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void toEntity (@MappingTarget Booking booking, BookingCreateRequest request);

    @Mapping(target = "bookingId", source = "id")
    BookingResponse toBookingResponse(Booking booking);

}
