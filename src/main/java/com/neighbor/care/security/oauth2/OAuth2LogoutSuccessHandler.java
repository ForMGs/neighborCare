package com.neighbor.care.security.oauth2;

import com.neighbor.care.redis.service.RedisTokenCycleSvc;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private final RedisTokenCycleSvc redisTokenCycleSvc;
    private final JwtParser jwtParser;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication){
        System.out.println("logout = " + authentication);
        if(authentication != null){
            String userId = authentication.getName();
            System.out.println("userId" + userId);
        }
        String refreshToken = "";
        if(request.getCookies() != null){
            System.out.println("쿠키가. ..");
            for(Cookie cookie : request.getCookies()){
                System.out.println(cookie.getName());
                if("accessToken".equals(cookie.getName())){
                    refreshToken =  cookie.getValue();
                }
            }
        }
        Claims claims = jwtParser.parseSignedClaims(refreshToken).getPayload();
        Long userId = Long.valueOf(claims.getSubject());

        String byUserId = redisTokenCycleSvc.findByUserId(userId);
        if(byUserId != null){
            redisTokenCycleSvc.deleteByUserId(userId);
        }

        ResponseCookie accessCookie = ResponseCookie.from("accessToken","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken","")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth/refresh")
                .maxAge(0)
                .build();

        ResponseCookie jsessionCookie = ResponseCookie.from("JSESSIONID","")
                        .httpOnly(true)
                        .secure(false)
                        .maxAge(0)
                        .path("/")
                        .build();
        response.addHeader("Set-Cookie",accessCookie.toString());
        response.addHeader("Set-Cookie",refreshCookie.toString());
        response.addHeader("Set-Cookie",jsessionCookie.toString());

        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
