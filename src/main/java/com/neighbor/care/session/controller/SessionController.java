package com.neighbor.care.session.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping("/test")
    public String sessionTest(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if(session == null)
        {
            return "세션 없음";
        }
        return "콘솔 확인";
    }
}
