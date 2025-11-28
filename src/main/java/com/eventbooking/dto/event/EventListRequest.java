package com.eventbooking.dto.event;

import com.eventbooking.common.constant.EventListType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventListRequest {

  @Pattern(regexp = "popular|upcoming|nearby", message = "Invalid type value")
  private String type;

  private String search;

  @Min(value = 1, message = "Page must be a positive integer")
  private Integer page = 1;

  @Min(value = 1, message = "Size must be a positive integer")
  private Integer size = 10;

  private Double lat;
  private Double lng;

  public EventSearchCriteria toCriteria() {
    return EventSearchCriteria.builder()
        .type(resolveType())
        .search(search)
        .userLatitude(lat)
        .userLongitude(lng)
        .build();
  }

  public Pageable toPageable() {
    int pageIndex = page == null ? 0 : Math.max(page - 1, 0);
    int pageSize = size == null ? 10 : size;
    return PageRequest.of(pageIndex, pageSize);
  }

  public EventListType resolveType() {
    return EventListType.fromValue(type).orElse(null);
  }

  public boolean isNearbyType() {
    return resolveType() == EventListType.NEARBY;
  }
}


