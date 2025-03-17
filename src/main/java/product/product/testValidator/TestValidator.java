package product.product.testValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import product.product.configuration.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/*
This class TestValidator, is responsible for dynamically running JUnit tests via Maven in the user's project.
It acts like an automatic test executor after tests are generated.
 */
@Component
public class TestValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestValidator.class);

    private final Config config;


    public TestValidator(Config config) {
        this.config = config;
    }

    //Runs Maven tests dynamically for user's project.
    public void runTests(String projectRootPath) {
        try {
            LOGGER.info("🚀 Starting Maven test execution for project: {}", projectRootPath);

            // Detect OS and prepare Maven command
            String mvnCommand = isWindows() ? "cmd.exe /c mvn test" : "mvn test";

            // Build process inside project root
            ProcessBuilder processBuilder = new ProcessBuilder(mvnCommand.split(" "));
            processBuilder.directory(new File(projectRootPath)); // Set working dir where pom.xml exists
            processBuilder.redirectErrorStream(true); // Combine stdout + stderr

            LOGGER.info("🔨 Executing Maven command: {}", mvnCommand);

            // Start Maven process
            Process process = processBuilder.start();

            // Live read process output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Wait for process to complete and capture exit code
            int exitCode = process.waitFor();

            // Handle exit code
            if (exitCode == 0) {
                LOGGER.info(" Tests executed successfully without errors.");
            } else {
                LOGGER.error(" Tests failed. Please check the Maven output. Exit code: {}", exitCode);
            }

        } catch (Exception e) {
            LOGGER.error(" Error during test validation: {}", e.getMessage(), e);
        }
    }

    //Overloaded method to fallback to config-defined project root path if user doesn't provide.
    public void runTests() {
        runTests(config.getProjectRootPath());
    }

    /*
    - This method detects if OS is Windows for command compatibility.
    - returns true if running on Windows, false otherwise.
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
