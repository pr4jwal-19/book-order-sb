package com.prajwal.tablebookapp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prajwal.tablebookapp.dto.ResponseWrapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        ResponseWrapper<String> errorResponse =
                new ResponseWrapper<>(false, "Unauthorized access: " + authException.getMessage(), null);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");

        new ObjectMapper().writeValue(
                response.getOutputStream(),
                errorResponse
        );

    }
}
