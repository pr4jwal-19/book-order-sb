package com.prajwal.tablebookapp.scheduler;

import com.prajwal.tablebookapp.model.CafeTable;
import com.prajwal.tablebookapp.model.Reservation;
import com.prajwal.tablebookapp.model.ReservationStatus;
import com.prajwal.tablebookapp.model.TableStatus;
import com.prajwal.tablebookapp.repo.CafeTableRepo;
import com.prajwal.tablebookapp.repo.ReservationRepo;
import com.prajwal.tablebookapp.service.utils.EmailNotificationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
//@Profile("prod")
public class JobScheduler {

    private final ReservationRepo reservationRepo;
    private final CafeTableRepo cafeTableRepo;
    private final EmailNotificationService emailNotificationService;

    @Autowired
    public JobScheduler(ReservationRepo reservationRepo, CafeTableRepo cafeTableRepo, EmailNotificationService emailNotificationService) {
        this.reservationRepo = reservationRepo;
        this.cafeTableRepo = cafeTableRepo;
        this.emailNotificationService = emailNotificationService;
    }

    // For example, it could set tables to AVAILABLE if their reservation
    // end time has passed, or to BOOKED if a reservation is currently active.
    @Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void updateTableStatuses() {

        LocalDateTime now = LocalDateTime.now();
        log.info("Running updateTableStatuses job at {}", now);

        List<Reservation> relevantReservations = reservationRepo.findActiveOrUpcomingReservations(
                now.minusMinutes(20),
                now.plusMinutes(20)
        );

        if (relevantReservations.isEmpty()) {
            log.info("No relevant reservations found. Exiting job.");
            return;
        }

        log.info("Found {} relevant reservations to process.", relevantReservations.size());

        for (Reservation r: relevantReservations) {
            try {
                CafeTable currTable = r.getCafeTable();

                if (now.isBefore(r.getStartTime())) {
                    currTable.setStatus(TableStatus.RESERVED);
                }
                else if (now.isBefore(r.getEndTime().plusMinutes(15)) && (r.getStatus() == ReservationStatus.CHECKED_IN)) {
                    currTable.setStatus(TableStatus.BOOKED);
                }
                else {
                    currTable.setStatus(TableStatus.AVAILABLE);
                }
                cafeTableRepo.save(currTable);
                log.debug("Updated table {} -> {}", currTable.getTableId(), currTable.getStatus());
            } catch (Exception e) {
                log.error("Failed to update table for reservation {}", r.getReservationId(), e);
            }
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
        reservationRepo.deleteCompletedReservationsPastEndTime(cutoff);
        log.info("Clean up old completed reservations job completed.");

    }

    @Scheduled(cron = "0 */5 * * * *") // every 5 minutes
    @Transactional
    public void autoCancelStaleReservations() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusMinutes(15); // 15 minutes ago

        log.info("Running auto-cancel stale reservations job at {}", now);

        List<Reservation> staleReservations = reservationRepo.findStaleReservations(cutoff);

        log.info("Found {} stale reservations to cancel.", staleReservations.size());

        for (Reservation r: staleReservations) {
            try {
                r.setStatus(ReservationStatus.CANCELLED);

                CafeTable currTable = r.getCafeTable();
                currTable.setStatus(TableStatus.AVAILABLE);
                cafeTableRepo.save(currTable);
                reservationRepo.save(r);

                log.info("Auto-cancelled stale reservation {}", r.getReservationId());

                emailNotificationService.sendReservationReminder(
                        r.getUser().getEmail(),
                        "Reservation Cancelled Due to No-Show",
                        String.format(
                                "Dear %s,<br><br>" +
                                        "We regret to inform you that your reservation at our cafe has been automatically cancelled due to a no-show.<br>" +
                                        "Reservation Details:<br>" +
                                        "Table Number: %s<br>" +
                                        "Start Time: %s<br>" +
                                        "End Time: %s<br><br>" +
                                        "If you have any questions or would like to make a new reservation, please feel free to contact us.<br><br>" +
                                        "Best regards,<br>" +
                                        "Cafe Team",
                                r.getUser().getEmail(),
                                r.getCafeTable().getTableNo(),
                                r.getStartTime(),
                                r.getEndTime()
                        )
                );

                log.info("Notification email sent for auto-cancelled reservation {}", r.getReservationId());
            }catch (Exception e) {
                log.error("Failed to auto-cancel stale reservation {}", r.getReservationId(), e);
            }
        }

        log.info("Auto-cancel stale reservations job completed.");
    }

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void autoMarkCompletedReservations() {

        LocalDateTime now = LocalDateTime.now();
        log.info("Running auto-mark completed reservations job at {}", now);

        List<Reservation> toBeCompleted = reservationRepo.findReservationsToMarkCompleted(now);
        log.info("Found {} reservations to mark completed.", toBeCompleted.size());

        for (Reservation r: toBeCompleted) {
            try {
                r.setStatus(ReservationStatus.COMPLETED);
                reservationRepo.save(r);

                CafeTable currTable = r.getCafeTable();
                currTable.setStatus(TableStatus.AVAILABLE);
                cafeTableRepo.save(currTable);

                log.info("Marked reservation {} as COMPLETED and freed the table {}.", r.getReservationId(), currTable.getTableNo());
            } catch (Exception e) {
                log.error("Failed to mark reservation {} as COMPLETED.", r.getReservationId(), e);
            }
        }

        log.info("Auto-mark completed reservations job completed.");
    }

    @Scheduled(cron = "0 */2 * * * *") // every 2 minutes
    @Transactional
    public void sendReservationReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindowStart = now.plusMinutes(10);
        LocalDateTime reminderWindowEnd = now.plusMinutes(25);

        log.info("Sending reservation reminders for reservations starting between {} and {}", now, reminderWindowEnd);

        // Fetch reservations starting within the next 10 to 25 minutes
        List<Reservation> upcomingReservations = reservationRepo.findReservationsStartingSoon(
                reminderWindowStart,
                reminderWindowEnd
        );

        log.info("Found {} reservations starting soon.", upcomingReservations.size());

        for (Reservation r : upcomingReservations) {
            try {

                if (Boolean.TRUE.equals(r.isReminderSent())) {
                    log.debug("Skipping reminder for reservation {} (already sent).", r.getReservationId());
                    continue; // Skip
                }

                if (!r.getUser().isUserVerified()) {
                    log.debug("Skipping reminder for reservation {} (user not verified).", r.getReservationId());
                    continue; // Skip
                }

                String to = r.getUser().getEmail();
                String subject = "Reminder: Upcoming Reservation at Our Cafe";
                String body = String.format(
                        "Dear %s,<br><br>" +
                                "This is a friendly reminder that you have a reservation at our cafe.<br>" +
                                "Reservation Details:<br>" +
                                "Table Number: %s<br>" +
                                "Start Time: %s<br>" +
                                "End Time: %s<br><br>" +
                                "We look forward to serving you!<br><br>" +
                                "Best regards,<br>" +
                                "Cafe Team",
                        r.getUser().getEmail(),
                        r.getCafeTable().getTableNo(),
                        r.getStartTime(),
                        r.getEndTime()
                );

                emailNotificationService.sendReservationReminder(to, subject, body);

                r.setReminderSent(true);
                reservationRepo.save(r);

                log.info("Reservation reminder sent and flagged for reservation {}", r.getReservationId());
            } catch (Exception e) {
                log.error("Failed to send reminder for reservation {}", r.getReservationId(), e);
            }
        }

        log.info("Reservation reminders job completed.");
    }

}
