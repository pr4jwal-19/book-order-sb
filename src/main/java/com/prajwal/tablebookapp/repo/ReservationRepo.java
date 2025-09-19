package com.prajwal.tablebookapp.repo;

import com.prajwal.tablebookapp.model.Reservation;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByUserUserId(Long userId, Pageable pageable);
    //List<Reservation> findByCafeTableTableId(Long tableId);

    // for table booking -> so only CONFIRMED reservations are considered
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.cafeTable.tableId = :tableId " +
            "AND r.status = 'CONFIRMED' " +
            "AND ( (r.startTime < :endTime) AND (r.endTime > :startTime))")
    List<Reservation> findOverlappingReservations(
            @Param("tableId") Long tableId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // check if table has any active reservations -- delete only when table status = AVAILABLE
    // can delete -> (i.e. rs : CANCELLED/COMPLETED)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reservation r " +
           "WHERE r.cafeTable.tableId = :tableId " +
            "AND r.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND r.endTime > :now ")
    boolean hasActiveReservations(
            @Param("tableId") Long tableId,
            @Param("now") LocalDateTime now
    );

    // updating table statuses every 2 minutes
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.status IN ('CONFIRMED', 'CHECKED_IN') " +
           "AND r.startTime <= :future " +
           "AND r.endTime >= :past ")
    List<Reservation> findActiveOrUpcomingReservations(
            @Param("past") LocalDateTime past,
            @Param("future") LocalDateTime future
    );

    // sending reminder emails to users 10-25 minutes before their reservation start-time
    // therefore, rs: CONFIRMED is okay
    @Query("SELECT r FROM Reservation r " +
           "WHERE r.status = 'CONFIRMED' " +
           "AND r.startTime BETWEEN :reminderWindowStart AND :reminderWindowEnd " +
            "AND r.reminderSent = false")
    List<Reservation> findReservationsStartingSoon(
            @Param("reminderWindowStart") LocalDateTime reminderWindowStart,
            @Param("reminderWindowEnd") LocalDateTime reminderWindowEnd
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.status = 'CANCELLED' AND r.endTime < :cutoff ")
    void deleteCancelledReservationsPastEndTime(
            @Param("cutoff") LocalDateTime cutoff
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.status = 'CONFIRMED' AND r.endTime < :cutoff ")
    void deleteExpiredConfirmedReservations(
            @Param("cutoff") LocalDateTime cutoff
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.status = 'COMPLETED' AND r.endTime < :cutoff ")
    void deleteCompletedReservationsPastEndTime(
            @Param("cutoff") LocalDateTime cutoff
    );

    @Query("SELECT r FROM Reservation r " +
           "WHERE r.status = 'CONFIRMED' " +
           "AND r.startTime < :cutoff ")
    List<Reservation> findStaleReservations(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT r FROM Reservation r " +
           "WHERE r.status = 'CHECKED_IN' " +
           "AND r.endTime < :now ")
    List<Reservation> findReservationsToMarkCompleted(@Param("now") LocalDateTime now);
}
