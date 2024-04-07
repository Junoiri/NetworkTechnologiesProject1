package com.example.networktechnologiesproject1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Filter class for processing JWT token authentication.
 */
public class JWTTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTTokenFilter.class);

    private final String key;

    /**
     * Constructor for JWTTokenFilter.
     */
    public JWTTokenFilter(String key) {
        this.key = key;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Bypass security filter chain for Swagger endpoints
        if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs") || requestURI.startsWith("/swagger-resources") || requestURI.startsWith("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1];

            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key.getBytes(StandardCharsets.UTF_8))
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String id = claims.getSubject();
                String role = (String) claims.get("role");

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        id, null, Collections.singletonList(new SimpleGrantedAuthority(role))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication successful for id: {}", id);
            } catch (Exception e) {
                logger.error("Authentication error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("No Authorization header found or Bearer token missing");
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
