package com.neighbor.care.user.repo;

import com.neighbor.care.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User,Long> {
    Optional<User> findBySocialIdAndProvider(String socialId, String provider);
    @Override
    void deleteById(Long aLong);
}
