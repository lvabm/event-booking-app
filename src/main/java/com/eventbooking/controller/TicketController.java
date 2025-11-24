package com.eventbooking.controller;

import com.eventbooking.common.base.BaseResponse;
import com.eventbooking.dto.booking.TicketResponse;
import com.eventbooking.exception.UnauthorizedException;
import com.eventbooking.service.TicketService;
import com.eventbooking.util.ApiResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

  private final TicketService ticketService;

  @GetMapping
  public ResponseEntity<BaseResponse<List<TicketResponse>>> getMyTickets(
      HttpServletRequest request) {
    
    // Lấy user ID từ request attribute (đã được set bởi JWT filter)
    Long userId = (Long) request.getAttribute("userId");
    
    if (userId == null) {
      throw new UnauthorizedException(
          "Unauthorized – Please login to access this resource");
    }

    List<TicketResponse> tickets = ticketService.getMyTickets(userId);

    return ResponseEntity.ok(
        ApiResponseBuilder.success("Tickets fetched successfully", tickets));
  }
}

