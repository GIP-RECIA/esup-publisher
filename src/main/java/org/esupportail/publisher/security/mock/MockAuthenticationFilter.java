package org.esupportail.publisher.security.mock;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.esupportail.publisher.domain.User;
import org.esupportail.publisher.repository.UserRepository;
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

@RequiredArgsConstructor
public class MockAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

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

        // Resolved from DB (seeded as "mock-user" in T_USER) so that @CreatedBy auditing
        // (SpringSecurityAuditorAware) attributes content to this user instead of falling
        // back to SYSTEM, which would make it invisible in "owned by me" views.
        User internalUser = userRepository.findById("mock-user").orElse(null);

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN),
            new SimpleGrantedAuthority(AuthoritiesConstants.USER)
        );
        CustomUserDetails principal = new CustomUserDetails(userDTO, internalUser, authorities);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
        filterChain.doFilter(request, response);
    }
}
