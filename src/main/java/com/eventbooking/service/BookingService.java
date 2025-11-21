package com.eventbooking.service;

import com.eventbooking.dto.booking.BookingCreateRequest;
import com.eventbooking.dto.booking.BookingResponse;
import com.eventbooking.dto.booking.TicketResponse;
import java.util.List;

public interface BookingService {
  /** Đặt vé cho sự kiện, kiểm tra eventId và quantity */
  BookingResponse create(BookingCreateRequest request);

  /** Lấy danh sách vé đã mua của người dùng hiện tại */
  List<TicketResponse> getMyTickets();
}
