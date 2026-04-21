package com.neighbor.care.user.social.google;

import java.util.Map;

public class GoogleUserInfo {
    private final String id;
    private final String name;
    private final String imageUrl;
    private final String email;

    public GoogleUserInfo(Map<String, Object> attributes){

//        Map<String, Object> properties = (Map<String, Object>) attributes.get("signed");
        if(attributes == null){
            throw new IllegalArgumentException("Google OAuth2 프로퍼티 값이 없습니다.");
        }
        this.id = String.valueOf(attributes.get("sub"));
        this.name = (String) attributes.get("name");
        this.imageUrl = (String) attributes.get("picture");
        this.email = (String) attributes.get("email");
    }
    public String getId(){return id;}
    public String getEmail(){return email;}
    public String getName(){return name;}
    public String getImageUrl(){return imageUrl;}
}
