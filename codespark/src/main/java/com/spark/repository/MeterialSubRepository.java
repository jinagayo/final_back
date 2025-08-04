package com.spark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.spark.Entity.AttendanceEntity;
import com.spark.Entity.MeterialSubEntity;

public interface MeterialSubRepository  extends JpaRepository<MeterialSubEntity, Integer>{
	
	@Query("SELECT ms FROM MeterialSubEntity ms JOIN MeterialEntity m ON ms.meterialId = m.meterId WHERE ms.stdId = :stdId AND m.classId = :classId")
	List<MeterialSubEntity> findByStdIdAndClassId(@Param("stdId") String stdId, @Param("classId") Integer classId);

	 MeterialSubEntity findByMeterialIdAndStdId(int meterialId, String stdId);

	Optional<MeterialSubEntity> findByMeterialIdAndStdId(Integer meterialId, String studentId);
	 
	 List<MeterialSubEntity> findByMeterialId(Integer metId);

}
