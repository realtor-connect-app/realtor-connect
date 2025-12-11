package com.makurohashami.realtorconnect.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.makurohashami.realtorconnect.dto.Error;
import com.makurohashami.realtorconnect.dto.apiresponse.ApiError;
import com.makurohashami.realtorconnect.dto.auth.JwtToken;
import com.makurohashami.realtorconnect.entity.user.User;
import com.makurohashami.realtorconnect.service.auth.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.makurohashami.realtorconnect.util.ApiResponseUtil.wrapError;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            JwtToken token = jwtService.parseToken(jwt);
            if (token.getUsername() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = new User(token.getUsername(), token.getRole());
                if (jwtService.isTokenValid(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            Error error = new Error(
                    Instant.now(),
                    "Bad JWT token. Please try to log in again",
                    ex.getMessage(),
                    request.getRequestURI()
            );
            log.error("ExpiredJwtException - {}", error.details());
            ResponseEntity<ApiError<Error>> apiResponse = wrapError(error, HttpStatus.UNAUTHORIZED);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(mapper.writeValueAsString(apiResponse.getBody()));
        }
    }
}
