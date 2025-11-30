package com.eventbooking.service;

import com.eventbooking.dto.event.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
  /** Tạo sự kiện mới (Yêu cầu quyền ADMIN) */
  EventDetailsResponse create(EventRequest request);

  /** Cập nhật thông tin sự kiện */
  EventDetailsResponse update(Long id, EventRequest request);

  /** Xoá sự kiện */
  void delete(Long id);

  /** Lấy danh sách sự kiện theo tiêu chí (type, search) */
  Page<EventResponse> search(EventSearchCriteria criteria, Pageable pageable);

  /** Xem chi tiết sự kiện */
  EventDetailsResponse getById(Long id);
}
