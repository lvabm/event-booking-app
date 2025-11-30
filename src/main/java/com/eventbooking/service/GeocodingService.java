package com.eventbooking.service;

import java.math.BigDecimal;
import java.util.Optional;

public interface GeocodingService {
    Optional<BigDecimal[]> getCoordinates(String location);
}
