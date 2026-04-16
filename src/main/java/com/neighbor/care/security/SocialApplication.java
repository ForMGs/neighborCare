package com.neighbor.care.security;

import com.neighbor.care.auth.filter.JwtAuthenticationFilter;
import com.neighbor.care.security.cors.CorsConfig;
import com.neighbor.care.security.oauth2.OAuth2LoginSuccessHandler;
import com.neighbor.care.security.oauth2.OAuth2LogoutSuccessHandler;
import com.neighbor.care.user.social.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity.RequestMatcherConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RequiredArgsConstructor
public class SocialApplication {

    //...
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        System.out.println("======= [1] securityFilterChain =======");
        http.cors(cors ->{})
                .csrf(c -> c.disable())
                .headers(headers->headers
                        .frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(a->a
                .requestMatchers("/","/error","/webjars/**").permitAll()
                        .requestMatchers("/api/user/naver",
                                "/api/user/me",
                                "/api/user/logout",
                                "/api/auth/refresh").permitAll()
                        .requestMatchers("/h2-console/**","/favicon.ico","/h2-console").permitAll()
                .anyRequest().authenticated()
        ).exceptionHandling(e -> e
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        ).sessionManagement(s->s
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .oauth2Login(
                oauth2 -> oauth2
                        .userInfoEndpoint( userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            System.out.println("blocked path ="+ request.getRequestURI());
                            System.out.println(exception);
                            request.getSession().setAttribute("error.message", exception.getMessage());
                        })
        ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->logout
                        .logoutUrl("/api/user/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll());

        return http.build();
    }

    @GetMapping("/error")
    public String error(HttpServletRequest request){
        String message = (String) request.getSession().getAttribute("error.message");
        request.getSession().removeAttribute("error.message");
        return message;
    }
}
