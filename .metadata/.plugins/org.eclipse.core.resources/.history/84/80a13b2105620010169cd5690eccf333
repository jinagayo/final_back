package com.spark.repository;

import com.spark.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    // userId로 조회
    Optional<UserEntity> findByUserId(String userId);

    // email로 조회
    Optional<UserEntity> findByEmail(String email);

    // phone으로 조회
    Optional<UserEntity> findByPhone(String phone);

    // userId 중복 여부
    boolean existsByUserId(String userId);

    // email 중복 여부
    boolean existsByEmail(String email);

    // phone 중복 여부
    boolean existsByPhone(String phone);
}
