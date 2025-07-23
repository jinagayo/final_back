package com.spark.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spark.Entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {


    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    UserEntity findByUserId(String userId);
    
    List<UserEntity> findByState(String state);  // state 기준 조회
    List<UserEntity> findByPosition(String position);  // position 기준 조회
    List<UserEntity> findByStateAndPosition(String state, String position);  // 복합 조회
    
    long countByState(String state);  // 상태별 개수

    @Query("SELECT u FROM UserEntity u WHERE u.state = 'PENDING' AND " +
            "(u.position = 'teacher' OR u.position = '강사' OR u.position = '1' OR u.position = '0')")
     List<UserEntity> findPendingTeachers();

}
