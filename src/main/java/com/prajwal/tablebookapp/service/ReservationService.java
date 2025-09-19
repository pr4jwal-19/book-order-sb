package com.prajwal.tablebookapp.service;

import com.prajwal.tablebookapp.dto.BookTableRequest;
import com.prajwal.tablebookapp.dto.ReservationDto;
import com.prajwal.tablebookapp.exception.*;
import com.prajwal.tablebookapp.model.*;
import com.prajwal.tablebookapp.repo.CafeTableRepo;
import com.prajwal.tablebookapp.repo.ReservationRepo;
import com.prajwal.tablebookapp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepo reservationRepo;
    private final CafeTableRepo cafeTableRepo;
    private final UserRepo userRepo;

    @Autowired
    public ReservationService(ReservationRepo reservationRepo, CafeTableRepo cafeTableRepo, UserRepo userRepo) {
        this.reservationRepo = reservationRepo;
        this.cafeTableRepo = cafeTableRepo;
        this.userRepo = userRepo;
    }


    public ReservationDto bookTable(BookTableRequest tableRequest) {
        CafeTable table = cafeTableRepo.findById(tableRequest.getTableId())
                .orElseThrow(() -> new TableNotAvailableException(tableRequest.getTableId()));

        // status -> BOOKED -> guest has arrived and is seated
        if (table.getStatus() == TableStatus.BOOKED) {
            throw new TableNotAvailableException(tableRequest.getTableId());
        }

        // check for past-time booking
        if (tableRequest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedActionException("Cannot book table in the past.");
        }

        // check for time validity
        if (tableRequest.getEndTime().isBefore(tableRequest.getStartTime()) ||
                tableRequest.getEndTime().isEqual(tableRequest.getStartTime())) {
            throw new ReservationConflictException(table.getTableId());
        }

        // check for overlapping reservations
        List<Reservation> conflicts = reservationRepo.findOverlappingReservations(
                table.getTableId(),
                tableRequest.getStartTime(),
                tableRequest.getEndTime()
        );

        if (!conflicts.isEmpty()) {
            throw new ReservationConflictException(table.getTableId());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Users currUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        Reservation reservation = Reservation.builder()
                .cafeTable(table)
                .user(currUser)
                .startTime(tableRequest.getStartTime())
                .endTime(tableRequest.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build();

        table.setStatus(TableStatus.RESERVED);
        cafeTableRepo.save(table);

        return toDto(reservationRepo.save(reservation));
    }

    public Page<ReservationDto> getReservationsFromCurrentUser(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Users currUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return reservationRepo.findByUserUserId(currUser.getUserId(), pageable)
                .map(this::toDto);
    }

    // admin control panel - service
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepo.findAll(pageable)
                .map(this::toDto);
    }

    // admin control panel - service
    public void updateReservationStatus(Long reservationId) {

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new UnauthorizedActionException("Can only check-in CONFIRMED reservations.");
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationRepo.save(reservation);
    }

    // admin control panel - service
    public void cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        CafeTable table = reservation.getCafeTable();
        table.setStatus(TableStatus.AVAILABLE);
        cafeTableRepo.save(table);

        // can do a soft delete by changing status to CANCELLED
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepo.save(reservation);
        //reservationRepo.delete(reservation); // later removed during cron job
    }

    private ReservationDto toDto(Reservation reservation) {
        return ReservationDto.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUser().getUserId())
                .tableId(reservation.getCafeTable().getTableId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus().name())
                .build();
    }
}
