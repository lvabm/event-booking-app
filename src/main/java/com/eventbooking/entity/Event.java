package com.eventbooking.entity;

import com.eventbooking.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@SuperBuilder
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event extends BaseEntity {

    @Column(nullable = false)
    String title;

    @Column(name = "date_time", nullable = false)
    LocalDateTime dateTime;

    @Column(nullable = false)
    String location;

    @Column(precision = 10, scale = 8, nullable = false)
    BigDecimal latitude;

    @Column(precision = 11, scale = 8, nullable = false)
    BigDecimal longitude;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    BigDecimal price = new BigDecimal(0);

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(length = 512, name = "image_url")
    String imageUrl;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Booking> bookings;
}
