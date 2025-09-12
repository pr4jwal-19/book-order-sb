package com.prajwal.tablebookapp.repo;

import com.prajwal.tablebookapp.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUsersUserId(Long userId);
    List<Reservation> findByCafeTableTableId(Long tableId);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.cafeTable.tableId = :tableId " +
            "AND r.status = 'CONFIRMED' " +
            "AND ( (r.startTime < :endTime) AND (r.endTime > :startTime))")
    List<Reservation> findOverlappingReservations(
            @Param("tableId") Long tableId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reservation r " +
           "WHERE r.cafeTable.tableId = :tableId " +
            "AND r.status = 'CONFIRMED' " +
           "AND r.endTime > :now ")
    boolean hasActiveReservations(
            @Param("tableId") Long tableId,
            @Param("now") LocalDateTime now
    );

}
