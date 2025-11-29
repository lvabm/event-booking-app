package com.eventbooking.dto.event;

import com.eventbooking.common.constant.EventListType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchCriteria {
  public static final double DEFAULT_RADIUS_KM = 50D;

  private EventListType type;
  private String search;
  private Double userLatitude;
  private Double userLongitude;
  @Builder.Default private Double radiusKm = DEFAULT_RADIUS_KM;

  public boolean hasUserCoordinates() {
    return userLatitude != null && userLongitude != null;
  }
}
