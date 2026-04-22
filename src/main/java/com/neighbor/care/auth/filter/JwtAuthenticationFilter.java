package com.neighbor.care.auth.filter;

import com.neighbor.care.user.dto.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
        if(!request.getRequestURI().startsWith("/api/auth")
                && !request.getRequestURI().startsWith("/api/user/me")
                && !request.getRequestURI().startsWith("/api/user/logout")){
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request,response);
            return;
        }
        System.out.println("======= JwtAuthenticationFilter. =======");
        HttpSession session = request.getSession(false);
        if(session !=null){
            System.out.println("session = "+ session.getId());

        }else{
            System.out.println("session null");
        }
        System.out.println("RequestedSessionId = "+ request.getRequestedSessionId());


        String token = resolveToken(request);
        System.out.println("token : "+token );

        if(token == null){
            SecurityContextHolder.clearContext();
        }

        try{
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            System.out.println("claims : " + claims);
            Long userId = Long.valueOf(claims.getSubject());
            String name = claims.get("name", String.class);
            String role = claims.get("role", String.class);

            CustomUserDetails principal = new CustomUserDetails(userId,name, role);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("filter 내부 : " + authentication);
        }catch (Exception e){
            SecurityContextHolder.clearContext();
        }
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
