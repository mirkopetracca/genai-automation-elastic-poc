package com.demo.ai.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.demo.ai.model.DevelopmentEntity;
import com.demo.ai.model.DevelopmentMetadataEntity;
import com.demo.ai.model.GeneratedCodeEntity;
import com.demo.ai.model.ProjectEntity;
import com.demo.ai.repository.DevelopmentMetadataRepository;
import com.demo.ai.repository.DevelopmentRepository;
import com.demo.ai.repository.DocumentRepository;
import com.demo.ai.repository.ProjectRepository;

@Service
public class GitHubIntegrationService {

	Log log = LogFactory.getLog(this.getClass());

	@Value("${github.access.token}")
	private String accessToken;

	@Value("${openai.api.key}")
	private String openaiApiKey;

	@Value("${openai.model}")
	private String openaiModel;

	private final ProjectRepository projectRepository;
	private final DevelopmentRepository developmentRepository;
	private final DocumentRepository documentRepository;
	private final GeneratedCodeService generatedCodeService;
	private final DevelopmentMetadataRepository developmentMetadataRepository;
	private final AIService aiService;

	private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

	public GitHubIntegrationService(ProjectRepository projectRepository, DevelopmentRepository developmentRepository, DocumentRepository documentRepository,
			GeneratedCodeService generatedCodeService, DevelopmentMetadataRepository developmentMetadataRepository, AIService aiService) {

		this.projectRepository = projectRepository;
		this.developmentRepository = developmentRepository;
		this.documentRepository = documentRepository;
		this.generatedCodeService = generatedCodeService;
		this.developmentMetadataRepository = developmentMetadataRepository;
		this.aiService = aiService;

	}

	public ResponseEntity<Map<String, Object>> analyzeRepository(Long projectId) {

		try {

			ProjectEntity project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Progetto non trovato"));

			String repoUrl = project.getRepositoryUrl();
			String sourcePath = project.getSourcePath();

			GitHub github = GitHub.connectUsingOAuth(accessToken);
			String repoName = repoUrl.replace("https://github.com/", "");
			GHRepository repository = github.getRepository(repoName);

			List<Map<String, Object>> analyzedFiles = new ArrayList<>();
			List<String> directories = new ArrayList<>();

			scanSourceCodeDirectory(repository, sourcePath, analyzedFiles, directories);

			Map<String, Object> repoAnalysis = Map.of("repository", repoUrl, "sourcePath", sourcePath, "analyzedFiles", analyzedFiles, "directories",
					directories);

			return ResponseEntity.ok(repoAnalysis);

		} catch (IOException e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Impossibile analizzare il repository: " + e.getMessage()));

		}

	}

	public ResponseEntity<Map<String, Object>> generateCode(Long developmentId, String customPrompt) {

		try {

			DevelopmentEntity development = developmentRepository.findById(developmentId)
					.orElseThrow(() -> new IllegalArgumentException("Development non trovato"));
			ProjectEntity project = development.getProject();

			String repoUrl = project.getRepositoryUrl();
			String sourcePath = project.getSourcePath();
			String tech = project.getTechnologies();

			GitHub github = GitHub.connectUsingOAuth(accessToken);
			String repoName = repoUrl.replace("https://github.com/", "");
			GHRepository repository = github.getRepository(repoName);

			List<Map<String, Object>> analyzedFiles = new ArrayList<>();
			scanSourceCodeDirectory(repository, sourcePath, analyzedFiles, new ArrayList<>());

			Optional<DevelopmentMetadataEntity> metadata = developmentMetadataRepository.findByDevelopmentId(developmentId);
			StringBuilder context = new StringBuilder("### Contesto del progetto\n");
			context.append("Tecnologia: ").append(project.getTechnologies()).append("\n");
			context.append("Percorso sorgente: ").append(project.getSourcePath()).append("\n\n");

			context.append("\n### Codice esistente rilevante\n");

			for (Map<String, Object> file : analyzedFiles) {

				if (!file.get("content").toString().isEmpty()) {

					context.append("#### ").append(file.get("path")).append("\n");
					context.append("```" + tech + "\n").append(file.get("content")).append("\n```\n");

				}

			}

			context.append("\n### Documentazione di riferimento\n");

			if (metadata.isPresent()) {

				DevelopmentMetadataEntity metadataObj = metadata.get();
				context.append("\n### Requisiti funzionali\n");
				context.append(metadataObj.getFunctionalRequirements()).append("\n\n");
				context.append("\n### Casi d'uso\n");
				context.append(metadataObj.getUseCases()).append("\n\n");
				context.append("\n### Input / Output \n");
				context.append(metadataObj.getInputOutputData()).append("\n\n");
				context.append("\n### Dipendenze Tecniche\n");
				context.append(metadataObj.getTechnicalDependencies()).append("\n\n");

			}

			context.append("\n### Richiesta\n");
			context.append("Genera tutto il codice per implementare la funzionalità: ").append(development.getTitle()).append("\n");
			context.append("Seguendo le convenzioni del progetto e integrandosi con i file già esistenti nel progetto forniti nel contesto.\n");
			context.append("Se necessario, estendi i file che trovi nel codice già esistente.\n");
			context.append("In ogni caso genera sempre i file completi.\n");
			context.append("Nel nome del file mantieni anche il percorso del repository in cui si trova il file.\n");
			context.append("Non aggiungere commenti o altre considerazioni, ad esempio conclusioni finali, genera soltanto i file con il codice.\n");
			context.append(StringUtils.hasText(customPrompt) ? "Inoltre " + customPrompt + "\n" : "\n");

			String aiResponse = aiService.callAnthropic(context.toString());
			Map<String, String> structuredCode = parseGeneratedCode(aiResponse);

			generatedCodeService.deleteGeneratedCodeByDevelopmentId(developmentId);

			structuredCode.forEach((fileName, fileContent) -> generatedCodeService.saveGeneratedCode(developmentId, fileName, fileContent));

			Map<String, Object> response = Map.of("repository", repoUrl, "developmentId", developmentId, "generatedFiles", structuredCode);

			return ResponseEntity.ok(response);

		} catch (IOException e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Errore nella generazione del codice: " + e.getMessage()));

		}

	}

	public ResponseEntity<String> commitGeneratedCode(Long developmentId) {

		try {

			DevelopmentEntity development = developmentRepository.findById(developmentId)
					.orElseThrow(() -> new IllegalArgumentException("Development non trovato"));
			ProjectEntity project = development.getProject();

			if (project == null) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Progetto associato non trovato per development ID: " + developmentId);

			}

			String repoUrl = project.getRepositoryUrl();
			String sourcePath = project.getSourcePath();

			GitHub github = GitHub.connectUsingOAuth(accessToken);
			String repoName = repoUrl.replace("https://github.com/", "");
			GHRepository repository = github.getRepository(repoName);

			List<GeneratedCodeEntity> generatedFiles = generatedCodeService.getGeneratedCodeByDevelopmentId(developmentId);

			if (generatedFiles.isEmpty()) {

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nessun codice generato trovato per il development ID: " + developmentId);

			}

			String branchName = "feature-development-" + developmentId;
			repository.createRef("refs/heads/" + branchName, repository.getBranch("main").getSHA1());

			for (GeneratedCodeEntity file : generatedFiles) {

				String filePath = file.getFileName();
				String fileContent = file.getFileContent();

				try {

					GHContent existingContent = repository.getFileContent(filePath, branchName);
					existingContent.update(fileContent, "Aggiornato codice generato per development " + developmentId, branchName);

				} catch (IOException e) {

					repository.createContent().content(fileContent).message("Aggiunto codice generato per development " + developmentId).branch(branchName)
							.path(filePath).commit();

				}

			}

			repository.createPullRequest("[Auto] Codice generato per development " + developmentId, branchName, "main", "Codice generato automaticamente.");

			return ResponseEntity.ok("Codice generato committato e pull request creata su GitHub");

		} catch (IOException e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nell'invio del codice a GitHub: " + e.getMessage());

		}

	}

	private void scanSourceCodeDirectory(GHRepository repository, String path, List<Map<String, Object>> analyzedFiles, List<String> directories)
			throws IOException {

		List<GHContent> contents = repository.getDirectoryContent(path);

		for (GHContent content : contents) {

			if (content.isFile()) {

				Map<String, Object> fileData = new HashMap<>();
				fileData.put("path", content.getPath());
				fileData.put("content", content.getContent());
				analyzedFiles.add(fileData);

			} else if (content.isDirectory()) {

				directories.add(content.getPath());
				scanSourceCodeDirectory(repository, content.getPath(), analyzedFiles, directories);

			}

		}

	}

	private Map<String, String> parseGeneratedCode(String response) {

		Map<String, String> files = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("### file: (.*?)\n(.*?)(?=### file:|$)", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(response);

		while (matcher.find()) {

			String fileName = matcher.group(1).trim();
			String fileContent = matcher.group(2).trim();

			// 🔹 Rimuove sempre la prima e l'ultima riga dal file content
			String[] lines = fileContent.split("\n");

			if (lines.length > 2) { // Assicura che ci siano almeno 3 righe per rimuovere la prima e l'ultima

				fileContent = String.join("\n", Arrays.copyOfRange(lines, 1, lines.length - 1)).trim();

			}

			files.put(fileName, fileContent);

		}

		return files;

	}

}
