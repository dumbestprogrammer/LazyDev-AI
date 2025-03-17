package product.product.testGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import product.product.ai.AIService;
import product.product.configuration.Config;
import product.product.dto.MethodMetaData;
import product.product.utility.CodeSanitizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


@Component
public class TestGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestGenerator.class);

    private final Config config;
    private final AIService aiService;
    private final CodeSanitizer codeSanitizer;


    public TestGenerator(Config config, AIService aiService, CodeSanitizer codeSanitizer) {
        this.config = config;
        this.aiService = aiService;
        this.codeSanitizer = codeSanitizer;
    }

    /*
     - Generates JUnit 5 test classes based on provided method metadata.

     - methodMetaDataList -> List of method metadata from source classes.
     - projectRootPath  ->  Path to the project root to dynamically generate tests inside src/test/java.
     */
    public void generateTests(List<MethodMetaData> methodMetaDataList, String projectRootPath) {
        String outputDirectory = projectRootPath + "/src/test/java"; //Target user's test directory ,src/test/java is used as the output location.

        if (methodMetaDataList == null || methodMetaDataList.isEmpty()) {
            LOGGER.warn(" No methods provided for test generation.");
            return;
        }

        LOGGER.info(" Generating test classes in: {}", outputDirectory);

        // Group methods by class name for one test class per source class
        Map<String, List<MethodMetaData>> classMethodMap = new HashMap<>();
        for (MethodMetaData metaData : methodMetaDataList) {
            classMethodMap.computeIfAbsent(metaData.getClassName(), k -> new ArrayList<>()).add(metaData);
        }

        // Generate test classes
        for (Map.Entry<String, List<MethodMetaData>> entry : classMethodMap.entrySet()) {
            String className = entry.getKey();
            List<MethodMetaData> methods = entry.getValue();
            generateTestClass(className, methods, outputDirectory);
        }
    }



    //Generates a test class for a specific Java class with multiple methods.
    private void generateTestClass(String sourceClassName, List<MethodMetaData> methods, String outputDirectory) {
        String simpleClassName = sourceClassName.substring(sourceClassName.lastIndexOf('.') + 1);
        String testClassName = simpleClassName + "Test";
        String packageName = extractPackageName(sourceClassName);

        StringBuilder testMethodsBlock = new StringBuilder();

        //Generates AI-based test methods and sanitize each one
        for (MethodMetaData method : methods) {
            String aiGeneratedTest = aiService.generateTestForMethod(method);
            String sanitizedTest = codeSanitizer.cleanAICode(aiGeneratedTest);
            testMethodsBlock.append(indentTestMethod(sanitizedTest)).append("\n\n"); // Properly indented and cleaned
        }

        //Wrap methods in a full test class with imports and package
        String finalTestClassCode = codeSanitizer.wrapInTestClass(packageName, testClassName, testMethodsBlock.toString());

        //Write to file
        writeTestFile(finalTestClassCode, outputDirectory, packageName, testClassName);
    }



    // Indents a given test method properly to fit inside a class body.
    private String indentTestMethod(String methodContent) {
        return Arrays.stream(methodContent.split("\n"))
                .map(line -> "    " + line) // 4 spaces indent
                .reduce("", (a, b) -> a + b + "\n");
    }

    /*
      - Writes the final test class content to a .java file in src/test/java.
      - Creates necessary package directories (if they don't exist).
     */
    private void writeTestFile(String content, String outputDirectory, String packageName, String testClassName) {
        try {
            String packagePath = packageName.replace('.', File.separatorChar);
            File targetDir = new File(outputDirectory + File.separator + packagePath);

            if (!targetDir.exists() && targetDir.mkdirs()) {
                LOGGER.info(" Created test directory: {}", targetDir.getAbsolutePath());
            }

            File testFile = new File(targetDir, testClassName + ".java");

            try (FileWriter writer = new FileWriter(testFile)) {
                writer.write(content);
                LOGGER.info(" Test class generated: {}", testFile.getAbsolutePath());
            }

        } catch (IOException e) {
            LOGGER.error(" Failed to write test class for {}: {}", testClassName, e.getMessage(), e);
        }
    }



    //Extracts the package name from a fully qualified class name.
    private String extractPackageName(String qualifiedClassName) {
        int lastDotIndex = qualifiedClassName.lastIndexOf('.');
        return lastDotIndex != -1 ? qualifiedClassName.substring(0, lastDotIndex) : "";
    }
}
