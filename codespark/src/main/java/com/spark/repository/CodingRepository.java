package com.spark.repository;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spark.Entity.CodingEntity;

@Repository
public interface CodingRepository extends JpaRepository<CodingEntity,Integer> {
	// 1. 기본 검색 (제목 + 문제 내용)
    @Query("SELECT c FROM CodingEntity c WHERE c.isActive = 1 " +
           "AND (:search IS NULL OR :search = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.qeustion) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CodingEntity> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    // 2. 카테고리별 검색
    @Query("SELECT c FROM CodingEntity c WHERE c.isActive = 1 " +
           "AND (:category IS NULL OR :category = '' OR c.filed LIKE CONCAT('%', :category, '%')) " +
           "AND (:search IS NULL OR :search = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.qeustion) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CodingEntity> findByCategoryAndSearch(
                                              @Param("search") String search, 
                                              Pageable pageable);
    
    // 3. 레벨별 검색
    @Query("SELECT c FROM CodingEntity c WHERE c.isActive = 1 " +
           "AND c.level = :level " +
           "AND (:category IS NULL OR :category = '' OR c.filed LIKE CONCAT('%', :category, '%')) " +
           "AND (:search IS NULL OR :search = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.qeustion) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CodingEntity> findByLevelAndCategoryAndSearch(@Param("level") int level,
                                                       @Param("search") String search, 
                                                       Pageable pageable);
    
    // 4. 전체 조건 검색 (가장 많이 사용될 메서드)
    @Query("SELECT c FROM CodingEntity c WHERE c.isActive = 1 " +
           "AND (:search IS NULL OR :search = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.qeustion) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:level IS NULL OR c.level = :level)")
    Page<CodingEntity> findByAllConditions(
                                          @Param("search") String search,
                                          @Param("level") Integer level,
                                          Pageable pageable);
    
    // 5. 카운트 조회 (전체 조건)
    @Query("SELECT COUNT(c) FROM CodingEntity c WHERE c.isActive = 1 " +
           "AND (:category IS NULL OR :category = '' OR c.filed LIKE CONCAT('%', :category, '%')) " +
           "AND (:search IS NULL OR :search = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.qeustion) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:level IS NULL OR c.level = :level)")
    long countByAllConditions(
                             @Param("search") String search,
                             @Param("level") Integer level);
    

    
    // 7. 난이도별 문제 개수 조회
    @Query("SELECT c.level, COUNT(c) FROM CodingEntity c WHERE c.isActive = 1 GROUP BY c.level")
    List<Object[]> countByLevel();
    
    // 8. 활성 상태인 모든 문제 조회
    List<CodingEntity> findByIsActiveOrderByCreateAtDesc(int isActive);
    
    // 9. 제목으로 정확한 문제 찾기
    CodingEntity findByTitleAndIsActive(String title, int isActive);
    
    // 10. 최근 등록된 문제들
    @Query("SELECT c FROM CodingEntity c WHERE c.isActive = 1 ORDER BY c.createAt DESC")
    List<CodingEntity> findRecentProblems(Pageable pageable);

	Optional<CodingEntity> findByCodeIdAndIsActive(int codeId, int i);

	@Query(value = """
			SELECT
			    c.code_id as id,
			    'coding' as type,
			    c.question as question,
			    c.level as level,
			    c.title as title,
			    c.language as language,
			    DATE_FORMAT(c.create_at, '%Y-%m-%d') as date,
			    SUBSTRING(c.question, 1, 100) as excerpt,
			    c.type as problem_type,
			    c.filed as field
			FROM coding c
			WHERE c.is_active = 1
			  AND (c.title LIKE CONCAT('%', ?1, '%')
			       OR c.language LIKE CONCAT('%', ?1, '%')
			       OR c.question LIKE CONCAT('%', ?1, '%')
			       OR c.filed LIKE CONCAT('%', ?1, '%'))
			ORDER BY c.create_at DESC
			""", nativeQuery = true)
			List<Map<String, Object>> searchCoding(@Param("keyword") String keyword);
}
