package com.prajwal.tablebookapp.scheduler;

import com.prajwal.tablebookapp.model.CafeTable;
import com.prajwal.tablebookapp.model.Reservation;
import com.prajwal.tablebookapp.model.TableStatus;
import com.prajwal.tablebookapp.repo.CafeTableRepo;
import com.prajwal.tablebookapp.repo.ReservationRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TableStatusScheduler {

    private final ReservationRepo reservationRepo;
    private final CafeTableRepo cafeTableRepo;

    @Autowired
    public TableStatusScheduler(ReservationRepo reservationRepo, CafeTableRepo cafeTableRepo) {
        this.reservationRepo = reservationRepo;
        this.cafeTableRepo = cafeTableRepo;
    }

    // For example, it could set tables to AVAILABLE if their reservation
    // end time has passed, or to BOOKED if a reservation is currently active.
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void updateTableStatuses() {

        List<Reservation> allReservations = reservationRepo.findAll();
        if (allReservations.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (Reservation r: allReservations) {
            CafeTable currTable = r.getCafeTable();

            if (now.isBefore(r.getStartTime())) {
                currTable.setStatus(TableStatus.RESERVED);
            }
            else if (!now.isBefore(r.getStartTime()) && now.isBefore(r.getEndTime())) {
                currTable.setStatus(TableStatus.BOOKED);
            }
            else if (now.isBefore(r.getEndTime().plusMinutes(15))) {
                currTable.setStatus(TableStatus.BOOKED);
            }
            else {
                currTable.setStatus(TableStatus.AVAILABLE);
            }
            cafeTableRepo.save(currTable);
        }

    }
}
