package com.prajwal.tablebookapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationDto {

    private Long reservationId;

    private Long userId;

    private Long tableId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;
}
