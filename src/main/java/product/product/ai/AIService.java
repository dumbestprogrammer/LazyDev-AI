package product.product.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import product.product.dto.MethodMetaData;
import product.product.utility.CodeSanitizer;

import java.util.List;

//AI Service for generating JUnit test methods using OpenAI.
//It is a core business logic class that communicates with an AI model (via Spring AI's ChatClient) to generate JUnit test methods automatically for a given piece of code (methods).
@Service
public class AIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AIService.class);

    /*
    - Part of Spring AI.
    - Handles communication with AI models (like OpenAI, Azure AI, etc.).
    - Used to send prompts and receive generated test methods.
     */
    private final ChatClient chatClient;


    /*
     - This CodeSanitizer is a class I built that cleans the AI's raw response.
     - It Removes extra explanations/comments/markdown — keeps only pure Java code. (without proper sanitization you will get backticks ```  or ```java in the code)
     - Ensures the output is ready to be written into .java test files.
     */
    private final CodeSanitizer codeSanitizer;


    public AIService(ChatClient chatClient, CodeSanitizer codeSanitizer) {
        this.chatClient = chatClient;
        this.codeSanitizer = codeSanitizer;
    }



    //Generates JUnit test code using AI for a specific method.
    public String generateTestForMethod(MethodMetaData methodMetaData) {
        try {
            String prompt = buildPrompt(methodMetaData);

            LOGGER.info("Sending method to AI for test generation: {}", methodMetaData.getMethodName());

            // Send prompt to AI
            Prompt aiPrompt = new Prompt(List.of(new UserMessage(prompt)));
            Message response = chatClient.call(aiPrompt).getResult().getOutput();

            // Clean AI response using CodeSanitizer
            String cleanedResponse = codeSanitizer.cleanAICode(response.getContent());

            // Return sanitized response
            return cleanedResponse;

        } catch (Exception e) {
            LOGGER.error(" Error generating test with AI for method {}: {}", methodMetaData.getMethodName(), e.getMessage(), e);
            return "// Failed to generate test for method: " + methodMetaData.getMethodName();
        }
    }

    //A natural language prompt for the AI to generate JUnit test.
    private String buildPrompt(MethodMetaData metaData) {
        return """
                You are a senior Java developer. Write a FULL JUnit 5 test method for this method. 
                - Handle all important and edge cases.
                - Cover parameters and exceptions.
                - Use proper assertions.
                - NO placeholders like 'TODO'. Write FULL code.
                - Annotate with @Test and make the test method name descriptive.
                - Use Mockito if mocking is needed.

                Class: %s
                Method Name: %s
                Signature: %s
                Return Type: %s
                Parameters: %s
                Annotations: %s
                Exceptions Thrown: %s
                Access Modifier: %s

                Generate only the method (NO class header or imports).
                """.formatted(
                metaData.getClassName(),
                metaData.getMethodName(),
                metaData.getSignature(),
                metaData.getReturnType(),
                metaData.getParameterTypes(),
                metaData.getAnnotations(),
                metaData.getThrownExceptions(),
                metaData.getAccessModifier()
        );
    }
}

/*
Step-by-Step Working of this Class :

Step 1: Build a detailed prompt.

Step 2: Send prompt to AI via ChatClient.

Step 3: Receive AI-generated test code as response.

Step 4: Sanitize and clean the AI's raw output using CodeSanitizer.

Step 5: Return the cleaned test code for writing to a file.
 */