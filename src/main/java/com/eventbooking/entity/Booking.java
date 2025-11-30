package com.eventbooking.entity;

import com.eventbooking.common.base.BaseEntity;
import com.eventbooking.common.constant.BookingStatus;
import com.eventbooking.common.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "event_id")
  private Event event;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
  private BigDecimal totalPrice;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private BookingStatus status = BookingStatus.PENDING;

  @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
  private Payment payment;

  public Booking(User user, Event event, Payment payment) {
    this.user = user;
    this.event = event;
    this.payment = payment;

    payment.setBooking(this);
    payment.setAmount(totalPrice);
    payment.setStatus(PaymentStatus.FAILED);
  }
}
