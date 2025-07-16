package com.spark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.UserEntity;

public interface AuthRepository extends JpaRepository<UserEntity, String> {

	Optional<UserEntity> findByUserId(String user_id);

}
