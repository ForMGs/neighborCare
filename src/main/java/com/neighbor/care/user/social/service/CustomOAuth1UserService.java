package com.neighbor.care.user.social.service;

import com.neighbor.care.user.entity.User;
import com.neighbor.care.user.repo.UserJpaRepository;
import com.neighbor.care.user.social.google.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth1UserService extends OidcUserService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("=======CustomOAuth1UserService 생성 or 진입 ======");
        System.out.println("[1]userRequest = " + userRequest.getAccessToken().getTokenValue());
        System.out.println("[2]userRequest = " + userRequest.getAdditionalParameters());
        System.out.println("[3]userRequest = " + userRequest.getClientRegistration());

        OidcUser oidcUser = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId : " +registrationId );

        if(!"google".equals(registrationId)){
            System.out.println("지원하지 않는 로그인 입니다.");
            throw new IllegalArgumentException("허용되지 않은 소셜 로그인입니다.");
        }

        if("google".equals(registrationId)){
            System.out.println("google 에서 가져온 정보 : "+ oidcUser.getAttributes().toString());
            GoogleUserInfo googleUserInfo = new GoogleUserInfo(oidcUser.getAttributes());
            User user = userJpaRepository.findBySocialIdAndProvider(googleUserInfo.getId(), registrationId)
                    .map(existing ->{
                        existing.updateProfile(
                                googleUserInfo.getEmail(),
                                googleUserInfo.getName(),
                                googleUserInfo.getImageUrl()
                        );
                        return existing;
                    }).orElseGet(()-> userJpaRepository.save(
                            User.builder()
                                    .socialId(googleUserInfo.getId())
                                    .name(googleUserInfo.getName())
                                    .phoneNumber("")
                                    .birthDate(null)
                                    .provider(registrationId)
                                    .status(User.Status.INACTIVE)
                                    .role(User.Role.GUARDIAN)
                                    .build()
                    ));

            Map<String, Object> customAttributes = new HashMap<>(oidcUser.getAttributes());
            customAttributes.put("id",googleUserInfo.getId());
            customAttributes.put("localUserId",user.getId());
            customAttributes.put("localRole",user.getRole().name());
            customAttributes.put("userName",googleUserInfo.getName());
                return new DefaultOidcUser(
                        Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()))
                        ,oidcUser.getIdToken()
                        ,new OidcUserInfo(customAttributes)
                        ,"id"
                );
        }

        return null;
    }

}
