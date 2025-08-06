package com.spark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spark.Entity.NoticeEntity;

@Repository
public interface NoticeRepository  extends JpaRepository<NoticeEntity,Integer> {

}
