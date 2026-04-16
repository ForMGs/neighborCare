package com.neighbor.care.user.social.kakao;

import java.time.LocalDate;
import java.util.Map;

public class KakaoUserInfo {

    private final String id;
    private final String email;
    private final String name;
    private final String profileImage;
    private final LocalDate birthDate;
    private final String phoneNumber;

    public KakaoUserInfo(Map<String, Object> attributes){

        Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
        if(properties == null){
            throw new IllegalArgumentException("카카오 OAuth2 프로퍼티 값이 없습니다.");
        }
        this.id = String.valueOf(attributes.get("id"));
        this.email = "";
        this.name=(String) properties.get("nickname");
        this.profileImage = (String) properties.get("profile_image");
        this.birthDate = null;
        this.phoneNumber = (String) properties.get("phoneNumber");
    }
    public String getId(){return id;}
    public String getEmail(){return email;}
    public String getName(){return name;}
    public String getProfileImage(){return profileImage;}
    public LocalDate getBirthDate(){return birthDate;}
    public String getPhoneNumber(){return phoneNumber;}

}
