package com.eventbooking.repository;

import com.eventbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  
  @Query("SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
  List<Booking> findByUserId(@Param("userId") Long userId);
  
  @Query("SELECT b FROM Booking b JOIN FETCH b.event WHERE b.user.id = :userId ORDER BY b.createdAt DESC")
  List<Booking> findByUserIdWithEvent(@Param("userId") Long userId);
}
