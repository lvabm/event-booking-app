package com.eventbooking.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface NearbyEventProjection {
  Long getId();

  String getTitle();

  LocalDateTime getDateTime();

  String getLocation();

  BigDecimal getPrice();

  String getDescription();

  String getImageUrl();

  BigDecimal getLatitude();

  BigDecimal getLongitude();

  Double getDistanceKm();
}


