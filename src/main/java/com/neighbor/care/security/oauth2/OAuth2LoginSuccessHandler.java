package com.neighbor.care.security.oauth2;

import com.neighbor.care.auth.jwt.JwtUtil;
import com.neighbor.care.redis.service.RedisTokenCycleSvc;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    private final JwtUtil jwtUtil;
    private final RedisTokenCycleSvc redisTokenCycleSvc;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        System.out.println("======= OAuth2LoginSuccessHandler. =======");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println(oAuth2User.toString());
        System.out.println(oAuth2User.getAttributes());
        Long localUserId = ((Number) oAuth2User.getAttributes().get("localUserId")).longValue();
        String role = (String) oAuth2User.getAttributes().get("localRole");
        String name = (String) oAuth2User.getAttributes().get("userName");
        System.out.println("name = " + name);

        String accessToken = jwtUtil.createAccessToken(localUserId, role , name);
        String refreshToken = jwtUtil.createRefreshToken(localUserId , role,name);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(60 * 30)
                        .sameSite("Lax")
                        .build();
        //Redis[start]
        System.out.println("localUserId :"+localUserId);
        System.out.println("refreshToken : "+ refreshToken);
        System.out.println("refreshTokenExpiration = " + refreshTokenExpiration);
        redisTokenCycleSvc.save(localUserId, refreshToken , refreshTokenExpiration);
        //Redis[end]


        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/api/auth/refresh")
                        .maxAge(60 * 60*24*7)
                        .sameSite("Lax")
                        .build();
        response.addHeader("Set-Cookie",accessCookie.toString());
        response.addHeader("Set-Cookie",refreshCookie.toString());
        System.out.println("redirect url : "+request.getParameter("state"));
        response.sendRedirect("http://localhost:8080/");
        clearAuthenticationAttributes(request);
    }
}
