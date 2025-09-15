package com.prajwal.tablebookapp.scheduler;

import com.prajwal.tablebookapp.model.CafeTable;
import com.prajwal.tablebookapp.model.Reservation;
import com.prajwal.tablebookapp.model.TableStatus;
import com.prajwal.tablebookapp.repo.CafeTableRepo;
import com.prajwal.tablebookapp.repo.ReservationRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class JobScheduler {

    private final ReservationRepo reservationRepo;
    private final CafeTableRepo cafeTableRepo;

    @Autowired
    public JobScheduler(ReservationRepo reservationRepo, CafeTableRepo cafeTableRepo) {
        this.reservationRepo = reservationRepo;
        this.cafeTableRepo = cafeTableRepo;
    }

    // For example, it could set tables to AVAILABLE if their reservation
    // end time has passed, or to BOOKED if a reservation is currently active.
    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void updateTableStatuses() {

        List<Reservation> allReservations = reservationRepo.findAll();

        if (allReservations.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        log.info("⏳ Running updateTableStatuses job at {}", now);

        List<Reservation> relevantReservations = reservationRepo.findActiveOrUpcomingReservations(
                now.minusMinutes(20),
                now.plusMinutes(20)
        );

        log.info("Found {} relevant reservations to process.", relevantReservations.size());

        for (Reservation r: relevantReservations) {
            CafeTable currTable = r.getCafeTable();

            if (now.isBefore(r.getStartTime())) {
                currTable.setStatus(TableStatus.RESERVED);
            }
            else if (now.isBefore(r.getEndTime().plusMinutes(15))) {
                currTable.setStatus(TableStatus.BOOKED);
            }
            else {
                currTable.setStatus(TableStatus.AVAILABLE);
            }
            cafeTableRepo.save(currTable);
            log.debug("Updated table {} -> {}", currTable.getTableId(), currTable.getStatus());
        }

        log.info("Update table statuses completed.");
    }

    @Scheduled(cron = "0 0 12 * * SUN") // every Sunday at noon
    @Transactional
    public void cleanUpOldReservations() {

        LocalDateTime cutoff = LocalDateTime.now().minusDays(7); // 7 days ago
        log.info("Clean up old reservations job at {}", cutoff);

        reservationRepo.deleteCancelledReservationsPastEndTime(cutoff);
        log.info("Clean up old cancelled reservations job completed.");
        reservationRepo.deleteExpiredConfirmedReservations(cutoff);
        log.info("Clean up old expired reservations job completed.");
    }
}
