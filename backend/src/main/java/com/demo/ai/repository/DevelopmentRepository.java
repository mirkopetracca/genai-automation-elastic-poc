package com.demo.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.ai.model.DevelopmentEntity;

@Repository
public interface DevelopmentRepository extends JpaRepository<DevelopmentEntity, Long> {
	List<DevelopmentEntity> findByProjectId(Long projectId);
}