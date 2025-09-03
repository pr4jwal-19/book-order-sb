package com.prajwal.tablebookapp.security;

import com.prajwal.tablebookapp.service.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    //private final ApplicationContext applicationContext;

    @Autowired
    public JwtFilter(JwtUtils jwtUtils) {
        //this.applicationContext = applicationContext;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            jwtToken = authorizationHeader.substring(7);
            email = jwtUtils.extractUsername(jwtToken);

        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //UserDetails userDetails = applicationContext.getBean(CustomUserDetailsService.class).loadUserByUsername(email);

            if (jwtUtils.validateToken(jwtToken, email)) {

                List<String> roles = jwtUtils.extractRoles(jwtToken);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                roles.stream().map(SimpleGrantedAuthority::new).toList()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
