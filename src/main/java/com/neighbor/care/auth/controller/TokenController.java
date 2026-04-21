package com.neighbor.care.auth.controller;

import com.neighbor.care.auth.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class TokenController {

    private final JwtParser jwtParser;
    private final JwtUtil jwtUtil;

    @RequestMapping("/refresh")
    public ResponseEntity<String> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ){
        String refreshToken = extractCookie(request, "refreshToken");
        System.out.println("authentication : " + authentication.getPrincipal());
        if(refreshToken == null || refreshToken.isBlank()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Claims claims = jwtParser.parseSignedClaims(refreshToken).getPayload();
            System.out.println("claims.toString() = " + claims.toString());
            Long userId = Long.valueOf(claims.getSubject());

            String role = claims.get("role", String.class);
            String name = claims.get("name",String.class);
            System.out.println("name = " + name);
            System.out.println("role = "+ role);

            String newAccessToken = jwtUtil.createAccessToken(userId, role,name);

            ResponseCookie accessCookie = ResponseCookie.from("accessToken",newAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(60*30)
                    .sameSite("Lax")
                    .build();
            response.addHeader("Set-Cookie",accessCookie.toString());
            System.out.println("refresh 완료 : "+ newAccessToken);
            return ResponseEntity.ok(role);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    private String extractCookie(HttpServletRequest request , String cookieName){
        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            return null;
        }

        for(Cookie cookie : cookies){
            System.out.println(cookie.getName() + " : "+ cookie.getValue());
            if(cookieName.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
}
