package org.esupportail.publisher.security.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.esupportail.publisher.security.AuthoritiesConstants;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.web.rest.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MockAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UserDTO userDTO = new UserDTO(
            "mock-user",
            "Mock User",
            true,
            true,
            "mock@example.com",
            Map.of()
        );

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN),
            new SimpleGrantedAuthority(AuthoritiesConstants.USER)
        );
        CustomUserDetails principal = new CustomUserDetails(userDTO, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
        filterChain.doFilter(request, response);
    }
}
