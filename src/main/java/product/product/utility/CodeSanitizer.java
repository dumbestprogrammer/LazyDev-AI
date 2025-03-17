package product.product.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/*

 - This utility class cleans the AI generated Java code.
 - Take cares of the code that typically contains markdown artifacts like ```java or ``` and wraps cleaned methods into a valid JUnit test class.
 - Handles formatting, cleaning, and wrapping in a single pipeline.

SO Basically :
 - It sanitizes the raw AI-generated code and make it syntactically valid Java.
 - Also generates a fully structured JUnit test class with necessary imports and given methods.

*/


@Component
public class CodeSanitizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeSanitizer.class);

    /*
    - Removes markdown formatting, unnecessary backticks, line breaks, whitespace.
    - Makes code ready to compile, especially test methods.
     */
    public String cleanAICode(String aiGeneratedCode) {
        if (aiGeneratedCode == null || aiGeneratedCode.trim().isEmpty()) {
            LOGGER.warn(" Received empty AI-generated code for cleaning."); //alerts when AI output is empty.
            return "";
        }

        LOGGER.info(" Cleaning AI-generated code...");

        String cleanedCode = aiGeneratedCode;

        //removes ```java or ``` JAVA (case-insensitive)
        cleanedCode = cleanedCode.replaceAll("(?i)```java\\s*", ""); // Case-insensitive

        //removes closing ```
        cleanedCode = cleanedCode.replaceAll("```", "");

        //removes stray single backticks `
        cleanedCode = cleanedCode.replaceAll("`", "");

        //removes normalize line breaks
        cleanedCode = cleanedCode.replaceAll("\\r\\n?", "\n"); // Windows to Unix linebreaks

        //trims unnecessary whitespace
        cleanedCode = cleanedCode.strip();

        LOGGER.info("✅ AI code cleaned and ready for wrapping.");
        return cleanedCode;
    }

    // Wrap sanitized test methods into a full test class with proper imports.
    public String wrapInTestClass(String packageName, String className, String methodsBlock) {
        StringBuilder builder = new StringBuilder();

        LOGGER.info("📦 Wrapping test methods in class: {}.{}", packageName, className);

        //Adds package declaration
        if (packageName != null && !packageName.isEmpty()) {
            builder.append("package ").append(packageName).append(";\n\n");
        }

        //Adding necessary imports for testing and mocking
        builder.append("""
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.*;
                import org.mockito.Mockito;
                import static org.mockito.Mockito.*;

                """);

        //adding class definition
        builder.append("public class ").append(className).append(" {\n\n");

        //adding test methods
        builder.append(methodsBlock).append("\n");

        //adding close class
        builder.append("}\n");

        LOGGER.info("✅ Test class wrapped and ready.");
        return builder.toString();
    }
}
