package com.prajwal.tablebookapp.dto;

import com.prajwal.tablebookapp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

    private String username;

    private String email;

    private String password;

    private String phoneNo;

    private Role role;

}
