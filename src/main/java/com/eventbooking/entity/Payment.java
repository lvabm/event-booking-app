package com.eventbooking.entity;

import com.eventbooking.common.base.BaseEntity;
import com.eventbooking.common.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

  @OneToOne(optional = false)
  @JoinColumn(name = "booking_id", unique = true)
  private Booking booking;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status = PaymentStatus.PAID;
}
