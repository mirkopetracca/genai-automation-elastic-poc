package com.demo.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.ai.model.DocumentChunkEntity;
import com.demo.ai.model.bean.DocumentChunkDTO;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunkEntity, Long> {

	@Query(value = """
			SELECT *
			FROM document_chunks
			ORDER BY (CAST(embedding AS vector(1536)) <-> CAST(:queryEmbedding AS vector(1536))) 
			+ (1 - ts_rank(tsvector_content, plainto_tsquery(:query))) 
			ASC
			LIMIT 20
			""", nativeQuery = true)
	List<DocumentChunkEntity> searchHybrid(@Param("queryEmbedding") float[] queryEmbedding, @Param("query") String query);

	@Query(value = """
			SELECT * FROM document_chunks WHERE id = ANY(:ids)
			""", nativeQuery = true)
	List<DocumentChunkEntity> findChunksByIds(@Param("ids") Long[] ids);

	@Query("""
			SELECT new com.demo.ai.model.bean.DocumentChunkDTO(d.id, d.fileName, c.title, c.chunkText)
			FROM DocumentChunkEntity c
			JOIN c.document d
			JOIN c.developments dev
			WHERE dev.id = :developmentId
			""")
	List<DocumentChunkDTO> findChunksByDevelopmentId(@Param("developmentId") Long developmentId);

}
