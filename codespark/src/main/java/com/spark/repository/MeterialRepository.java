package com.spark.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.MeterialEntity;
import com.spark.dto.MeterialDTO;


public interface MeterialRepository extends JpaRepository<MeterialEntity, Integer>{

	//classId 와 type 모두 조건
	List<MeterialEntity> findByClassIdAndType(Integer classId, String string);

	@Query("SELECT COALESCE(MAX(m.seq), 0) FROM MeterialEntity m WHERE m.classId = :classId")
	int findNextSeqByClassId(@Param("classId") Integer classId);


	@Query(value = "SELECT * FROM meterial where class_id=:classId order by seq",
	    nativeQuery = true)
	 List<MeterialEntity> findByClassId(@Param("classId")Integer classId);
  
	MeterialEntity findByMeterId(Integer id);
	
    @Modifying
	@Query(value="UPDATE `meterial` SET seq=:seq  where meter_id=:meterId",
		    nativeQuery = true)
	void changeSeq(@Param("meterId")int meterId, @Param("seq")int seq);


	

}
