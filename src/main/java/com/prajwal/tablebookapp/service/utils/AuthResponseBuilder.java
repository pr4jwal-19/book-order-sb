package com.prajwal.tablebookapp.service.utils;

import com.prajwal.tablebookapp.dto.AuthResponseDto;
import com.prajwal.tablebookapp.dto.UserDto;
import com.prajwal.tablebookapp.model.Users;

public class AuthResponseBuilder {

    public static AuthResponseDto buildAuthResponseDto(Users user, String token) {

        return AuthResponseDto.builder()
                .token(token)
                .user(UserDto.builder()
                        .usrId(user.getUserId())
                        .userName(user.getUsername())
                        .email(user.getEmail())
                        .userVerified(user.isUserVerified())
                        .build()
                )
                .build();

    }

}
