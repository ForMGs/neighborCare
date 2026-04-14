package com.neighbor.care.user.controller;

import com.neighbor.care.user.dto.UserInDTO;
import com.neighbor.care.user.service.UserServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceInterface userSvc;

    //가입
    @PostMapping("/regist")
    public ResponseEntity<String> registUser(@RequestBody UserInDTO userInDTO){
        userSvc.userRegist(userInDTO);
        return ResponseEntity.ok("회원가입이 완료 되었습니다.");
    }


    //탈퇴.
    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody UserInDTO userInDTO){
        return ResponseEntity.ok("회원이 정상적으로 탈퇴되었습니다.");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logoutUser(){
        System.out.println("로그아웃..");
        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> selectMe(Authentication authentication){
        System.out.println("여기" + authentication);
        Map<String, Object> outDTO = new HashMap<>();
        if(authentication != null && authentication.isAuthenticated()){
            outDTO.put("name",authentication.getName());
            outDTO.put("roles", authentication.getAuthorities());
            System.out.println(authentication);
        }
        return ResponseEntity.ok(outDTO);
    }
}
