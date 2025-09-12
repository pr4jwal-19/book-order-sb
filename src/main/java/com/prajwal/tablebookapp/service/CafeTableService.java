package com.prajwal.tablebookapp.service;

import com.prajwal.tablebookapp.dto.CafeTableDto;
import com.prajwal.tablebookapp.exception.ResourceNotFoundException;
import com.prajwal.tablebookapp.exception.TableDeletionFailedException;
import com.prajwal.tablebookapp.exception.TableNotAvailableException;
import com.prajwal.tablebookapp.model.CafeTable;
import com.prajwal.tablebookapp.model.TableStatus;
import com.prajwal.tablebookapp.repo.CafeTableRepo;
import com.prajwal.tablebookapp.repo.ReservationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CafeTableService {

    private final CafeTableRepo cafeTableRepo;
    private final ReservationRepo reservationRepo;

    @Autowired
    public CafeTableService(CafeTableRepo cafeTableRepo, ReservationRepo reservationRepo) {
        this.cafeTableRepo = cafeTableRepo;
        this.reservationRepo = reservationRepo;
    }

    // admin flow
    public CafeTableDto addTable(CafeTableDto cafeTableDto) {
        CafeTable table = CafeTable.builder()
                .tableNo(cafeTableDto.getTableNo())
                .capacity(cafeTableDto.getCapacity())
                .status(TableStatus.AVAILABLE) // new tables -> always green
                .build();
        return toDto(cafeTableRepo.save(table));
    }

    public CafeTableDto updateTable(Long tableId, CafeTableDto cafeTableDto) {
        CafeTable table = cafeTableRepo.findById(tableId)
                .orElseThrow(() -> new TableNotAvailableException(tableId));

        table.setTableNo(cafeTableDto.getTableNo());
        table.setCapacity(cafeTableDto.getCapacity());
        table.setStatus(cafeTableDto.getStatus());

        return toDto(cafeTableRepo.save(table));
    }

    // check if the table is booked or reserved before deleting
    public void deleteTable(Long tableId) {

        CafeTable table = cafeTableRepo.findById(tableId)
                        .orElseThrow(() -> new ResourceNotFoundException("Table", tableId));

        boolean hasActiveReservations = reservationRepo.hasActiveReservations(
                tableId,
                LocalDateTime.now());

        if (hasActiveReservations) {
            // there are active or upcoming reservations for this table
            throw new TableDeletionFailedException(table.getTableNo());
        }

        cafeTableRepo.deleteById(tableId);
    }

    public List<CafeTableDto> getAllTables() {
        return cafeTableRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CafeTableDto toDto(CafeTable table) {
        return CafeTableDto.builder()
                .tableId(table.getTableId())
                .tableNo(table.getTableNo())
                .capacity(table.getCapacity())
                .status(table.getStatus())
                .build();
    }
}
