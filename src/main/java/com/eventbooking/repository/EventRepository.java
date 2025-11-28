package com.eventbooking.repository;

import com.eventbooking.entity.Event;
import com.eventbooking.repository.projection.NearbyEventProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {

  @Query(
      """
      SELECT e FROM Event e
      WHERE (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
      ORDER BY e.dateTime ASC
      """)
  Page<Event> findAllWithSearch(@Param("keyword") String keyword, Pageable pageable);

  @Query(
      """
      SELECT e FROM Event e
      LEFT JOIN e.bookings b
      WHERE (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
      GROUP BY e
      ORDER BY COUNT(b) DESC, e.dateTime ASC
      """)
  Page<Event> findPopularEvents(@Param("keyword") String keyword, Pageable pageable);

  @Query(
      """
      SELECT e FROM Event e
      WHERE e.dateTime >= :now
        AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
      ORDER BY e.dateTime ASC
      """)
  Page<Event> findUpcomingEvents(
      @Param("now") LocalDateTime now, @Param("keyword") String keyword, Pageable pageable);

  @Query(
      value =
          """
          SELECT
            e.id AS id,
            e.title AS title,
            e.date_time AS dateTime,
            e.location AS location,
            e.price AS price,
            e.description AS description,
            e.image_url AS imageUrl,
            e.latitude AS latitude,
            e.longitude AS longitude,
            (6371 * acos(
              cos(radians(:userLat)) * cos(radians(e.latitude)) *
              cos(radians(e.longitude) - radians(:userLng)) +
              sin(radians(:userLat)) * sin(radians(e.latitude))
            )) AS distanceKm
          FROM events e
          WHERE (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (6371 * acos(
              cos(radians(:userLat)) * cos(radians(e.latitude)) *
              cos(radians(e.longitude) - radians(:userLng)) +
              sin(radians(:userLat)) * sin(radians(e.latitude))
            )) <= :radius
          ORDER BY distanceKm ASC
          """,
      countQuery =
          """
          SELECT COUNT(1)
          FROM events e
          WHERE (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (6371 * acos(
              cos(radians(:userLat)) * cos(radians(e.latitude)) *
              cos(radians(e.longitude) - radians(:userLng)) +
              sin(radians(:userLat)) * sin(radians(e.latitude))
            )) <= :radius
          """,
      nativeQuery = true)
  Page<NearbyEventProjection> findNearbyEvents(
      @Param("keyword") String keyword,
      @Param("userLat") double userLat,
      @Param("userLng") double userLng,
      @Param("radius") double radius,
      Pageable pageable);
}
