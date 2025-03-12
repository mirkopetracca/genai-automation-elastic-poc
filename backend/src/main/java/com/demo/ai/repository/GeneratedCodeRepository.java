package com.demo.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.ai.model.GeneratedCodeEntity;

@Repository
public interface GeneratedCodeRepository extends JpaRepository<GeneratedCodeEntity, Long> {
	
	List<GeneratedCodeEntity> findByDevelopmentId(Long developmentId);

	void deleteByDevelopmentId(Long developmentId);
	
}
