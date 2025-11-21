package com.eventbooking.entity;

import com.eventbooking.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "events")
public class Event extends BaseEntity {

  @Column(nullable = false)
  private String title;

  @Column(name = "date_time", nullable = false)
  private LocalDateTime dateTime;

  @Column(nullable = false)
  private String location;

  @Column(nullable = false, precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(nullable = false, precision = 11, scale = 8)
  private BigDecimal longitude;

  @Column(precision = 10, scale = 2)
  private BigDecimal price = BigDecimal.ZERO;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Quan hệ
  @OneToMany(mappedBy = "event")
  private List<Booking> bookings;
}
