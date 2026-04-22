package com.neighbor.care.user.dto;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String name, String role){
        this.userId = userId;
        this.name =name;
        this.authorities = List.of(new SimpleGrantedAuthority(role));
    }

    public Long getUserId(){
        return userId;
    }
    public String getName(){
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
