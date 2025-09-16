package com.prajwal.tablebookapp.dto;

import com.prajwal.tablebookapp.model.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDto {

    private String token;

    private Users user;

}
