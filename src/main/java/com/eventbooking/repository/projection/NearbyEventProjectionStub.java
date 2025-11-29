package com.eventbooking.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class NearbyEventProjectionStub implements NearbyEventProjection {
  private final Long id;
  private final String title;
  private final LocalDateTime dateTime;
  private final String location;
  private final BigDecimal price;
  private final String description;
  private final String imageUrl;
  private final BigDecimal latitude;
  private final BigDecimal longitude;
  private final Double distanceKm;

  public NearbyEventProjectionStub(
      Long id,
      String title,
      LocalDateTime dateTime,
      String location,
      BigDecimal price,
      String description,
      String imageUrl,
      BigDecimal latitude,
      BigDecimal longitude,
      Double distanceKm) {
    this.id = id;
    this.title = title;
    this.dateTime = dateTime;
    this.location = location;
    this.price = price;
    this.description = description;
    this.imageUrl = imageUrl;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distanceKm = distanceKm;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public LocalDateTime getDateTime() {
    return dateTime;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public BigDecimal getPrice() {
    return price;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getImageUrl() {
    return imageUrl;
  }

  @Override
  public BigDecimal getLatitude() {
    return latitude;
  }

  @Override
  public BigDecimal getLongitude() {
    return longitude;
  }

  @Override
  public Double getDistanceKm() {
    return distanceKm;
  }
}


