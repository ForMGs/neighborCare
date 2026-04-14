package com.neighbor.care.user.service;

import com.neighbor.care.user.dto.UserInDTO;

public interface UserServiceInterface {
    void userRegist(UserInDTO inDTO);
    void deleteUser(Long id);

}
