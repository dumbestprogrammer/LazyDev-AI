package product.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import product.product.codeAnalyzer.CodeAnalyzer;
import product.product.configuration.Config;
import product.product.dto.MethodMetaData;
import product.product.testGenerator.TestGenerator;
import product.product.testValidator.TestValidator;

import java.util.List;

/*
 So this is the entry point for LazyDev AI-powered CLI tool.
 Pipeline: Analyze source code -> Generate JUnit tests -> Validate via Maven tests (mvn tests).



Its primary purpose is to automate the end-to-end lifecycle of JUnit test creation and validation, including:
-> Source Code Analysis — parsing code to extract method metadata.
-> JUnit Test Generation — creating unit test templates for the analyzed methods.
-> Test Validation — running generated tests using Maven to validate correctness.

Command-line Interface (CLI) — exposing these features via CLI commands (generate-tests, analyze-only, validate-only).
It uses Spring Boot for dependency injection and CommandLineRunner for CLI execution.
 */

@SpringBootApplication
public class LazyDevApplication implements CommandLineRunner {
	//This Command line runner allow this class to run CLI commands when the application starts (when packaged as a JAR).

	private final CodeAnalyzer codeAnalyzer;
	//CodeAnalyzer analyzes source code so it can extract methods.

	private final TestGenerator testGenerator;
	//TestGenerator generates the JUnit test files.

	private final TestValidator testValidator;
	//TestValidator: runs Maven tests for validation.

	private final Config config;
	//At last this Config holds configuration like source,test paths.. etc.


	@Autowired
	public LazyDevApplication(CodeAnalyzer codeAnalyzer,
							  TestGenerator testGenerator,
							  TestValidator testValidator,
							  Config config) {
		this.codeAnalyzer = codeAnalyzer;
		this.testGenerator = testGenerator;
		this.testValidator = testValidator;
		this.config = config;
	}

	public static void main(String[] args) {
		SpringApplication.run(LazyDevApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (args.length < 2) {
			printHelp();
			return;
		}

		String command = args[0].trim().toLowerCase(); // Normalize command
		String sourceFolder = args[1].trim(); // Normalize path

		//Dynamically calculate project root from provided source path
		String projectRootPath = sourceFolder.replace("/src/main/java", "").replace("\\src\\main\\java", "");

		//Command dispatcher
		switch (command) {  //This switch checks what the user wants to do and runs the appropriate process.
			case "generate-tests" -> handleGenerateTests(sourceFolder, projectRootPath);
			case "analyze-only" -> handleAnalyzeOnly(sourceFolder);
			case "validate-only" -> handleValidateOnly(projectRootPath);
			default -> printHelp(); // Fallback to help if unknown command
		}
	}



	//Full pipeline: Analyze -> Generate tests -> Validate (mvn test).

	private void handleGenerateTests(String sourceFolder, String projectRootPath) {
		try {
			System.out.println(" Starting analysis of source code from: " + sourceFolder);
			List<MethodMetaData> metaDataList = codeAnalyzer.analyzeSource(sourceFolder);

			if (metaDataList.isEmpty()) {
				System.out.println(" No methods found to generate tests for.");
				return;
			}

			System.out.println(" Analysis completed. Methods found: " + metaDataList.size());

			// Generate tests
			System.out.println("  Generating JUnit test cases...");
			testGenerator.generateTests(metaDataList, projectRootPath);
			System.out.println(" Test cases generated and saved to: " + config.getTestPath());

			// Run tests
			System.out.println(" Running generated tests via Maven...");
			testValidator.runTests(projectRootPath);
			System.out.println(" Test execution and validation completed.");

		} catch (Exception e) {
			System.err.println(" Error during pipeline execution: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*Analyze code only (no test generation).
	  - Runs only analysis and prints method metadata.
      - Good for inspecting what will be tested.
	 */
	private void handleAnalyzeOnly(String sourceFolder) {
		try {
			System.out.println(" Starting analysis of source code only...");
			List<MethodMetaData> metaDataList = codeAnalyzer.analyzeSource(sourceFolder);

			if (metaDataList.isEmpty()) {
				System.out.println(" No methods found during analysis.");
			} else {
				System.out.println(" Analysis completed. Methods found: " + metaDataList.size());
				metaDataList.forEach(System.out::println);
			}
		} catch (Exception e) {
			System.err.println(" Error during analysis: " + e.getMessage());
			e.printStackTrace();
		}
	}



	/* Validate existing tests using Maven (no analysis/generation).
	   - Runs Maven tests only, without analysis or test generation.
       - Useful for re-validating tests.
	 */
	private void handleValidateOnly(String projectRootPath) {
		try {
			System.out.println(" Running Maven tests only on project: " + projectRootPath);
			testValidator.runTests(projectRootPath);
			System.out.println(" Maven tests completed.");
		} catch (Exception e) {
			System.err.println(" Error running Maven tests: " + e.getMessage());
			e.printStackTrace();
		}
	}



	//CLI Usage help, detailed command usage with examples and configuration notes.
	private void printHelp() {
		System.out.println("""
                Invalid or missing command!

                Usage:
                --------------------------------------
                lazydev generate-tests <source_folder>  -> Analyze code, generate tests, and run them
                lazydev analyze-only <source_folder>   -> Analyze code without generating tests
                lazydev validate-only <project_root>   -> Only run existing Maven tests

                Example:
                java -jar lazydev.jar generate-tests C:/MyProject/src/main/java

                You can configure paths in application.properties:
                lazydev.source-path=src/main/java
                lazydev.test-path=src/test/java
                lazydev.project-root-path=./
                --------------------------------------
                """);
	}
}
