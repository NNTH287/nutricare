package com.nutricare.nutricare_api.infrastructure.adapter.in.web.security;

import com.nutricare.nutricare_api.infrastructure.adapter.out.security.JwtTokenProvider;
import com.nutricare.nutricare_api.infrastructure.adapter.out.security.TokenClaims;
import com.nutricare.nutricare_api.infrastructure.adapter.out.security.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        extractToken(request)
                .flatMap(token -> tokenProvider.validateAndParse(token, TokenType.ACCESS))
                .ifPresent(this::authenticate);

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    private void authenticate(TokenClaims claims) {
        AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(claims.userId(), claims.email(), claims.role());
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + claims.role().name()));
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
