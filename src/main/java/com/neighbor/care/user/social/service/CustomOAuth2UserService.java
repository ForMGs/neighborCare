package com.neighbor.care.user.social.service;

import com.neighbor.care.user.entity.User;
import com.neighbor.care.user.repo.UserJpaRepository;
import com.neighbor.care.user.social.naver.NaverUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Function : social 아이디와 회원 정보를 연결..
 * */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserJpaRepository userJpaRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException {
        System.out.println("======= CustomOAuth2UserService 생성. =======");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if(!"naver".equals(registrationId)){
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }
        System.out.println("naver에 access_token으로 가져온 정보 : \n"+ oAuth2User.getAttributes().toString());
        NaverUserInfo naverUserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        User user = userJpaRepository
                .findBySocialIdAndProvider(naverUserInfo.getId(),registrationId)
                .map(existing ->{
                    existing.updateProfile(
                            naverUserInfo.getEmail(),
                            naverUserInfo.getName(),
                            naverUserInfo.getProfileImage()
                    );
                    return existing;
                })
                .orElseGet(()-> userJpaRepository.save(
                        User.builder()
                                .socialId(naverUserInfo.getId())
                                .phoneNumber(naverUserInfo.getPhoneNumber())
                                .birthDate(naverUserInfo.getBirthDate())
                                .provider(registrationId)
                                .name(naverUserInfo.getName())
                                .status(User.Status.INACTIVE)
                                .role(User.Role.GUARDIAN)
                                .build()
                ));
        Map<String, Object> customAttributes = new HashMap<>(oAuth2User.getAttributes());
        customAttributes.put("id", naverUserInfo.getId());
        customAttributes.put("localUserId", user.getId());
        customAttributes.put("localRole",user.getRole().name());
        customAttributes.put("userName", naverUserInfo.getName());
//        for(String key : customAttributes.keySet() ){
//            System.out.println(key + " : " + customAttributes.get(key));
//        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                customAttributes,
                "id"
        );

    }
}
