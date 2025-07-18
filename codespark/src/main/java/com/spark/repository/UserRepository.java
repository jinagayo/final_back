package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

}
