package com.iot.system.config;

import com.iot.system.repository.BlacklistedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    private static final List<String> EXCLUDED_PATHS = Arrays.asList("/auth/login", "/auth/register");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String requestPath = request.getServletPath();
        // Ignora as rotas de autenticação e registro
        if (EXCLUDED_PATHS.contains(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String userIdentifier;
        final String jwtToken;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.info("No JWT token found in request headers");
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        if (jwtToken.isEmpty()) {
            logger.error("JWT Token is empty");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        logger.info("JWT Token: " + jwtToken);

        // Verifica se o token está na blacklist
        if (blacklistedTokenRepository.findByToken(jwtToken).isPresent()) {
            logger.error("JWT Token is blacklisted");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            userIdentifier = jwtService.extractUsername(jwtToken);
        } catch (Exception e) {
            logger.error("Error extracting username from token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        logger.info("User Identifier: " + userIdentifier);

        if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userIdentifier);
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("User authenticated and context set");
            } else {
                logger.warn("JWT Token is invalid");
            }
        }

        filterChain.doFilter(request, response);
    }
}