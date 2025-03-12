package com.demo.ai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.ai.model.DocumentEntity;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
        
	List<DocumentEntity> findAll();
	    
    Optional<DocumentEntity> findByFileName(String fileName);



}