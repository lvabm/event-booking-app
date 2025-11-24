package com.eventbooking.dto.booking;

import com.eventbooking.dto.event.EventInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
  private Long ticketId;
  private EventInfo event;
  private Integer quantity;
  private String status;
}
