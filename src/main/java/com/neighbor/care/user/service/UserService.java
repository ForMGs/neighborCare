package com.neighbor.care.user.service;

import com.neighbor.care.user.entity.User;
import com.neighbor.care.user.repo.UserJpaRepository;
import com.neighbor.care.user.dto.UserInDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserServiceInterface{

    private final UserJpaRepository jpaRepo;


    @Override
    @Transactional
    public void userRegist(UserInDTO inDTO) {
//        jpaRepo.save(User.builder()
//                .status(User.Status.INACTIVE)
//                .birthDate(inDTO.birthDate())
//                .socialId(inDTO.loginId())
//                .name(inDTO.name())
//                .phoneNumber(inDTO.phoneNumber())
//                .role(User.Role.GUARDIAN)
//                .build());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        jpaRepo.deleteById(id);

    }


}
