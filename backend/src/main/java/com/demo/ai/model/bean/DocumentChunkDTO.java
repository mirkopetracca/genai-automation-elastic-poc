package com.demo.ai.model.bean;

public class DocumentChunkDTO {

	private Long documentId;
	private String documentName;
	private String chunkTitle;
	private String chunkText;

	public DocumentChunkDTO(Long documentId, String documentName, String chunkTitle, String chunkText) {

		this.documentId = documentId;
		this.documentName = documentName;
		this.chunkTitle = chunkTitle;
		this.chunkText = chunkText;

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

	public String getChunkTitle() {

		return chunkTitle;

	}

	public void setChunkTitle(String chunkTitle) {

		this.chunkTitle = chunkTitle;

	}

	public String getChunkText() {

		return chunkText;

	}

	public void setChunkText(String chunkText) {

		this.chunkText = chunkText;

	}

}
