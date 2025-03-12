package com.demo.ai.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "development_metadata")
public class DevelopmentMetadataEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "development_id", nullable = false, unique = true)
	private DevelopmentEntity development;

	@Lob
	private String functionalRequirements;

	@Lob
	private String useCases;

	@Lob
	private String inputOutputData;

	@Lob
	private String technicalDependencies;

	public Long getId() {

		return id;

	}

	public DevelopmentEntity getDevelopment() {

		return development;

	}

	public void setDevelopment(DevelopmentEntity development) {

		this.development = development;

	}

	public String getFunctionalRequirements() {

		return functionalRequirements;

	}

	public void setFunctionalRequirements(String functionalRequirements) {

		this.functionalRequirements = functionalRequirements;

	}

	public String getUseCases() {

		return useCases;

	}

	public void setUseCases(String useCases) {

		this.useCases = useCases;

	}

	public String getInputOutputData() {

		return inputOutputData;

	}

	public void setInputOutputData(String inputOutputData) {

		this.inputOutputData = inputOutputData;

	}

	public String getTechnicalDependencies() {

		return technicalDependencies;

	}

	public void setTechnicalDependencies(String technicalDependencies) {

		this.technicalDependencies = technicalDependencies;

	}
}
