package com.eventbooking.service;

import com.eventbooking.dto.event.EventCreateRequest;
import com.eventbooking.dto.event.EventResponse;
import com.eventbooking.dto.event.EventSearchCriteria;
import com.eventbooking.dto.event.EventUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
  /** Tạo sự kiện mới (Yêu cầu quyền ADMIN) */
  EventResponse create(EventCreateRequest request);

  /** Cập nhật thông tin sự kiện */
  EventResponse update(Long id, EventUpdateRequest request);

  /** Xoá sự kiện */
  void delete(Long id);

  /** Lấy danh sách sự kiện theo tiêu chí (type, search) */
  Page<EventResponse> search(EventSearchCriteria criteria, Pageable pageable);

  /** Xem chi tiết sự kiện */
  EventResponse getById(Long id);
}
