package com.prajwal.tablebookapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp="^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNo;

    private String address;

}
