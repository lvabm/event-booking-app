package com.eventbooking.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
  private Long id;
  private String title;
  private LocalDateTime dateTime;
  private String location;
  private BigDecimal price;
  private String description;
  private String imageUrl;
}
