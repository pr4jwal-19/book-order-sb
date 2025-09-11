package com.prajwal.tablebookapp.dto;

import com.prajwal.tablebookapp.model.TableStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CafeTableDto {

    private Long tableId;

    private String tableNo;

    private Integer capacity;

    private TableStatus status;

}
