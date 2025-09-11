package com.prajwal.tablebookapp.repo;

import com.prajwal.tablebookapp.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUsersUserId(Long userId);
    List<Reservation> findByCafeTableTableId(Long tableId);

}
