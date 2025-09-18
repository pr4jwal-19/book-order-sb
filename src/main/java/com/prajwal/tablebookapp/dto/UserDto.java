package com.prajwal.tablebookapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long usrId;

    private String email;

    private String userName;

    private boolean userVerified;

}
