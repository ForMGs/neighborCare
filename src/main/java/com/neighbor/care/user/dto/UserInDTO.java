package com.neighbor.care.user.dto;

import com.neighbor.care.user.entity.User;

public record UserInDTO(
        String loginId
        , String passwordHash
        , String name
        , String phoneNumber
        , String birthDate
        , User.Role role
        , User.Status satus
) {
}
