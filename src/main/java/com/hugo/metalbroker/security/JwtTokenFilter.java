package com.hugo.metalbroker.security;

import java.io.IOException;

import com.hugo.metalbroker.model.user.UserDTO;
import com.hugo.metalbroker.repository.UserRepo;
import com.hugo.metalbroker.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JWTUtils jwtTokenUtils;
    private final UserRepo userRepo;

    public JwtTokenFilter(JWTUtils jwtTokenUtil, UserRepo userRepo) {
        this.jwtTokenUtils = jwtTokenUtil;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.split(" ")[1].trim();
        Claims claims = jwtTokenUtils.decodeJWTToken(token);
        if (claims == null) {
            chain.doFilter(request, response);
            return;
        }

        String username = claims.getSubject();
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDTO userData = userRepo.getUserByUsername(username);
            UserDetailsImpl userDetails = new UserDetailsImpl(userData);

            if (jwtTokenUtils.validate(token, userData.getUsername())) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

}