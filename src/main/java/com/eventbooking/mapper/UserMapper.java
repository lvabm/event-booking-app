package com.eventbooking.mapper;

import com.eventbooking.dto.auth.LoginRequest;
import com.eventbooking.dto.auth.RegisterRequest;
import com.eventbooking.dto.auth.RegisterResponse;
import com.eventbooking.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reminder", ignore = true)
    User toEntity(RegisterRequest registerRequest);

    @Mapping(source = "id", target = "userId")
    RegisterResponse toDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "reminder", ignore = true)
    User toEntity(LoginRequest loginRequest);
}