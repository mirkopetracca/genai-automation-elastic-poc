package com.demo.ai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.demo.ai.model.DevelopmentMetadataEntity;

@Repository
public interface DevelopmentMetadataRepository extends JpaRepository<DevelopmentMetadataEntity, Long> {
	Optional<DevelopmentMetadataEntity> findByDevelopmentId(Long developmentId);

	@Modifying
	@Transactional
	@Query("DELETE FROM DevelopmentMetadataEntity d WHERE d.development.id = :developmentId")
	void deleteByDevelopmentId(Long developmentId);
	
}