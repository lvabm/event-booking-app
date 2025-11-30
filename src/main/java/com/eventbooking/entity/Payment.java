package com.eventbooking.entity;

import com.eventbooking.common.base.BaseEntity;
import com.eventbooking.common.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@SuperBuilder
@Table(name = "payments")
public class Payment extends BaseEntity {

  @OneToOne(optional = false)
  @JoinColumn(name = "booking_id", unique = true)
  private Booking booking;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private PaymentStatus status = PaymentStatus.PAID;

  public Payment(BigDecimal amount) {
    this.amount = amount;
  }
}
