package com.spark.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.CommonEntity;

@Repository
public interface CommonRepository extends JpaRepository<CommonEntity, String> {

	@Query(value = "SELECT `name`,`com_id` as `code` from `common` where `com_id` like :string",
	    nativeQuery = true)
	List<Map<String,Object>> findCom(@Param("string")String string);

}
