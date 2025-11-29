package com.eventbooking.service;

import com.eventbooking.dto.booking.TicketResponse;

import java.util.List;

public interface TicketService {
  /**
   * Lấy danh sách vé đã mua của người dùng hiện tại
   * @param userId ID của người dùng
   * @return Danh sách vé
   */
  List<TicketResponse> getMyTickets(Long userId);
}

