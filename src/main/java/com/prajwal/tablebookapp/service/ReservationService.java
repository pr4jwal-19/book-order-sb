package com.prajwal.tablebookapp.service;

import com.prajwal.tablebookapp.dto.BookTableRequest;
import com.prajwal.tablebookapp.dto.ReservationDto;
import com.prajwal.tablebookapp.exception.ReservationConflictException;
import com.prajwal.tablebookapp.exception.ReservationNotFoundException;
import com.prajwal.tablebookapp.exception.TableNotAvailableException;
import com.prajwal.tablebookapp.exception.UserNotFoundException;
import com.prajwal.tablebookapp.model.*;
import com.prajwal.tablebookapp.repo.CafeTableRepo;
import com.prajwal.tablebookapp.repo.ReservationRepo;
import com.prajwal.tablebookapp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
                .users(currUser)
                .startTime(tableRequest.getStartTime())
                .endTime(tableRequest.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build();

        table.setStatus(TableStatus.RESERVED);
        cafeTableRepo.save(table);

        return toDto(reservationRepo.save(reservation));
    }

    public List<ReservationDto> getReservationsFromCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Users currUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return reservationRepo.findByUsersUserId(currUser.getUserId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // admin control panel - service
    public List<ReservationDto> getAllReservations() {
        return reservationRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
                .userId(reservation.getUsers().getUserId())
                .tableId(reservation.getCafeTable().getTableId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus().name())
                .build();
    }
}
