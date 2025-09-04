package com.prajwal.tablebookapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<T> {

    private boolean success;
    private String message;
    private T data;

}
