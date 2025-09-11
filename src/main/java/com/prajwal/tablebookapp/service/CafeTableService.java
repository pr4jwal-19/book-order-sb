package com.prajwal.tablebookapp.service;

import com.prajwal.tablebookapp.dto.CafeTableDto;
import com.prajwal.tablebookapp.model.CafeTable;
import com.prajwal.tablebookapp.model.TableStatus;
import com.prajwal.tablebookapp.repo.CafeTableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CafeTableService {

    private final CafeTableRepo cafeTableRepo;

    @Autowired
    public CafeTableService(CafeTableRepo cafeTableRepo) {
        this.cafeTableRepo = cafeTableRepo;
    }


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
                .orElseThrow(() -> new RuntimeException("Table not found"));

        table.setTableNo(cafeTableDto.getTableNo());
        table.setCapacity(cafeTableDto.getCapacity());
        table.setStatus(cafeTableDto.getStatus());

        return toDto(cafeTableRepo.save(table));
    }

    public void deleteTable(Long tableId) {
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
