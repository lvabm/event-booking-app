package com.eventbooking.entity;

import com.eventbooking.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "reminders")
public class Reminder extends BaseEntity {

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @Column(name = "event_reminder")
  private Boolean eventReminder = true;
}
