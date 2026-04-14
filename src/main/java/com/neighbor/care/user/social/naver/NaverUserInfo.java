package com.neighbor.care.user.social.naver;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class NaverUserInfo {
    private final String id;
    private final String email;
    private final String name;
    private final String profileImage;
    private final LocalDate birthDate;
    private final String phoneNumber;


    public NaverUserInfo(Map<String, Object> attributes){
        Map<String, Object> response = (Map<String, Object>)attributes.get("response");

        if(response == null){
            throw new IllegalArgumentException("네이버 OAuth2 response 값이 없습니다.");
        }
        this.id = (String) response.get("id");
        this.email = (String) response.get("email");
        this.name = (String) response.get("name");
        this.profileImage = (String) response.get("profile_image");
        StringBuilder sb = new StringBuilder();
        sb.append(response.get("birthyear")).append("-").append(response.get("birthday"));
        LocalDate data = LocalDate.parse(sb, DateTimeFormatter.ofPattern("yyyy-M-d"));
        this.birthDate = data;
        this.phoneNumber = (String) response.get("mobile");
    }
    public String getId() {return id;}
    public String getEmail() {return email;}
    public String getName() {return name;}
    public String getProfileImage() {return profileImage;}
    public LocalDate getBirthDate() {return birthDate;}
    public String getPhoneNumber() {return phoneNumber;}
}
