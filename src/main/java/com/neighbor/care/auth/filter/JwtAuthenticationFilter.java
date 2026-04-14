package com.neighbor.care.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParser jwtParser;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        System.out.println("======= JwtAuthenticationFilter. =======");


        String token = resolveToken(request);
        try{
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);

            UserDetails principal = org.springframework.security.core.userdetails.User
                    .withUsername(String.valueOf(userId))
                    .password("")
                    .username(claims.get("name",String.class))
                    .authorities(role)
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch (Exception ignored){}
        filterChain.doFilter(request,response);
    }

    private String resolveToken(HttpServletRequest request){

        String bearer = request.getHeader("Authorization");
        if(bearer != null && bearer.startsWith("Bearer ")){
            return bearer.substring(7);
        }

        if(request.getCookies() != null){
            System.out.println("쿠키가. ..");
            for(Cookie cookie : request.getCookies()){
                System.out.println(cookie.getName());
                if("accessToken".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
