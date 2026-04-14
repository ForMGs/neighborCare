package com.neighbor.care.login;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    public ResponseEntity<String> getToken(){

        return ResponseEntity.ok("토큰");
    }

    public record loginInDTO(
            String grantType,
            String clientId,
            String clientSecret,
            String code //로그인 인증 요청 API 호출에 성공하고 리턴받은 인증코드값 (authorization code)
            ,String state // 발급 때 필수 사이트 간 요청 위조(cross-site request forgery) 공격을 방지하기 위해 애플리케이션에서 생성한 상태 토큰
                            //값으로 URL 인코딩을 적용한 값을 사용.
            ,String refreshToken //갱신 때 필수
            ,String accessToken // 삭제 때 필수..
            ,String serviceProvider // 삭제때 필수 .. 인증 제공자 이름을 NAVER 로 세팅해 전송..
    ){}
    public record loginOutDto(
            String accessToken ,
            String refreshToken ,
            String tokenType , // 접근 토큰의 타입으로 Bearer 와 Mac 두 가지를 지원.
            int expiresIn ,
            String error,
            String errorDescription
    ){}

}
