package com.demo.ai.model.bean;

import java.util.List;

public class DocumentWithChunksDTO {

	private Long documentId;
	private String documentName;
	private List<String> chunkTitles;
	private List<String> chunkTextes;

	public DocumentWithChunksDTO(Long documentId, String documentName, List<String> chunkTitles, List<String> chunkTextes) {

		this.documentId = documentId;
		this.documentName = documentName;
		this.chunkTitles = chunkTitles;
		this.chunkTextes = chunkTextes;

	}

	public Long getDocumentId() {

		return documentId;

	}

	public void setDocumentId(Long documentId) {

		this.documentId = documentId;

	}

	public String getDocumentName() {

		return documentName;

	}

	public void setDocumentName(String documentName) {

		this.documentName = documentName;

	}

	public List<String> getChunkTitles() {

		return chunkTitles;

	}

	public void setChunkTitles(List<String> chunkTitles) {

		this.chunkTitles = chunkTitles;

	}

	public List<String> getChunkTextes() {

		return chunkTextes;

	}

	public void setChunkTextes(List<String> chunkTextes) {

		this.chunkTextes = chunkTextes;

	}

}
