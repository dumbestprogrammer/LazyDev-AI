## License
[![AGPL-3.0 License](https://img.shields.io/badge/License-AGPL%203.0-blue.svg)](https://www.gnu.org/licenses/agpl-3.0.html)

This project is licensed under the **GNU AGPL-3.0 License** © 2025-2027 Anukul Kumar. See [LICENSE](./LICENSE) for more information.

<hr>

# 🧠 LazyDev AI - Automated Test Case Generator for Java Projects
#### A next-gen CLI tool that analyzes Java source code, generates JUnit test cases using AI, and runs tests automatically — making test-driven development lazy but efficient.

## 🚀 Overview
- LazyDev AI is a *Spring Boot + AI-based*  **CLI tool** that automatically generates **JUnit 5 test classes** for your existing **Java** project. 
- It scans your code, understands method logic, and writes meaningful unit tests, reducing manual effort.
- It utilizes **Spoon** for *code parsing* and **OpenAI (GPT-4 Turbo)** for *writing test cases*. 
- After generating, it can automatically run and validate those tests using Maven.

## ✅ Why I Created This?
Writing unit tests is time-consuming, repetitive, and often skipped in fast-paced development. LazyDev AI allows you to: <br>

- Auto-generate meaningful test cases ( *full-fledged JUnit 5 tests not partial/skeleton* )
- Focus on feature development, not boilerplate testing.
- Catch bugs early by running tests instantly.
- Work fully offline ( *no code upload, ensuring security* )
- Think of LazyDev AI as your AI-powered testing assistant.

## 🔹 Benefits?
- Most devs hate writing unit tests – This saves time.
- Helps juniors & lazy devs by generating test cases automatically.
- AI-powered coverage analysis ensures important cases aren’t missed.
- Improves code quality without effort.


## ✨ Features
- AI-Powered **JUnit 5 Test** Generation.
- Analyzes method signatures, parameters, exceptions.
- **Command-line based** — no need to integrate in IDE.
- Auto-organizes generated test files in **/src/test/java**.
- Supports **Mockito mocking** where required.
- Runs mvn test automatically to validate generated tests.
- Fully customizable via **application.properties** or **CLI**.
- Detailed logs for easy debugging and traceability.
- Automatic cleanup of AI-generated Java code.
- Markdown artifacts removal: ` ```java ` blocks, stray backticks, and more.
- Whitespace normalization and line break adjustments.
- Automatic test class wrapping with essential imports and proper structure.


## 📦 Prerequisites
- Java 17+
- Maven 3.8+
- OpenAI API Key (Required to generate tests)


## ⚙️ Tech Stack

| Technology         | Purpose                                 |
|--------------------|-----------------------------------------|
| Java 17            | Core programming language               |
| Spring Boot 3      | Backend framework and CLI               |
| Spring AI          | OpenAI LLM integration (GPT-4 Turbo)    |
| Spoon              | Java static code analysis               |
| JUnit 5            | Test framework                          |
| Mockito            | Mocking framework                       |
| Maven              | Build & dependency management           |
| SLF4J + Logback    | Logging and tracing                     |



## 📚 Dependencies (from pom.xml)
- spring-boot-starter-web
- spring-ai-openai-spring-boot-starter
- spoon-core
- junit-jupiter
- mockito-core
- slf4j + logback
- spring-boot-starter-test


## 📂 Folder Structure

```
├── src/main/java
│   └── product/product
│       ├── ai/                    # Talks to AI models like OpenAI to generate test cases
│       │   └── AIService.java
│       │
│       ├── codeAnalyzer/          # Reads and analyzes your Java code to find methods
│       │   └── CodeAnalyzer.java
│       │
│       ├── testGenerator/         # Creates JUnit test files automatically from AI output
│       │   └── TestGenerator.java
│       │
│       ├── testValidator/         # Runs the generated tests and checks if they pass or fail
│       │   └── TestValidator.java
│       │
│       ├── utility/               # Helps clean AI-generated code (removes markdown, backticks, etc.)
│       │   └── CodeSanitizer.java
│       │
│       ├── configuration/         # Stores settings like file paths and API keys
│       │   └── Config.java
│       │
│       ├── dto/                   # Holds method details (like name, params) to share between classes
│       │   └── MethodMetaData.java
│       │
│       └── LazyDevApplication.java # Main class that runs everything from start to finish

```


## 🔎 Class Breakdown
| Class              | Role and Responsibility                               |
|--------------------|-------------------------------------------------------|
| LazyDevApplication | CLI entry point, command parsing, pipeline control    |
| AIService          | AI interaction and prompt generation for tests        |
| CodeAnalyzer       | Analyzes Java methods and gathers metadata            |
| TestGenerator      | Generates test files based on AI responses            |
| TestValidator      | Runs `mvn test` to validate tests                     |
| CodeSanitizer      | Cleans AI-generated code for compilation              |
| Config             | Loads application properties and paths                |
| MethodMetaData     | DTO for method-level metadata                         |


## 🚀 API Endpoints (Public Methods)

### 1. ai.AIService
- Handles interaction with OpenAI API for generating test cases.

#### Method Signature
`public String generateTestCases(String methodSignature)`

<br>

#### Description:
- Takes a Java method signature or block and sends it to OpenAI to generate JUnit test cases. Returns raw AI output as a String.

#### Example Usage
`String aiGeneratedTest = aiService.generateTestCases("public int add(int a, int b)");`



### 2. codeAnalyzer.CodeAnalyzer
- Scans Java files to extract methods that require tests.

#### Method Signature
`public List<MethodMetaData> analyzeSourceCode(String sourceCode)`
<br>


#### Description:
- Accepts Java source code as String, analyzes it using Spoon, and returns a list of MethodMetaData objects representing each method found.

#### Example Usage
`List<MethodMetaData> methods = codeAnalyzer.analyzeSourceCode(javaFileContent);`



### 3. testGenerator.TestGenerator
- Generates properly formatted JUnit test class files from AI outputs.

#### Method Signature
`public void generateTestFile(String packageName, String className, String testMethodsContent)`
<br>


#### Description:
- Takes package name, class name, and AI-generated test methods, and writes them into a JUnit test file under correct package directory.

#### Example Usage
`testGenerator.generateTestFile("com.example.test", "UserServiceTest", aiGeneratedMethods);`




### 4. testValidator.TestValidator
- Runs generated test files using Maven and reports pass/fail results.

#### Method Signature
`public void runTests(String testFilePath)`
<br>


#### Description:
- Takes the file path of the generated JUnit test and runs mvn test on it. Captures and logs the result of test execution.

#### Example Usage
`testValidator.runTests("src/test/java/com/example/UserServiceTest.java");`


### 5. utility.CodeSanitizer
- Cleans AI-generated code from unwanted formatting and prepares it for file writing.

#### Method Signatures

`public String cleanAICode(String aiGeneratedCode)`
`public String wrapInTestClass(String packageName, String className, String methodsBlock)`

<br>


#### Descriptions:

- **cleanAICode:** Removes markdown syntax (like ```java) and stray backticks from AI output.
- **wrapInTestClass:** Wraps cleaned test methods inside a valid Java class structure including necessary imports.


#### Example Usage

`String cleanedCode = codeSanitizer.cleanAICode(rawAiCode);`
`String testClass = codeSanitizer.wrapInTestClass("com.example", "MyServiceTest", cleanedCode);`



### 6. configuration.Config
- Manages system configurations like file paths and API keys.

#### Method Signatures
`public String getOpenAiApiKey()`
`public String getJavaSourceFolder()`
`public String getTestOutputFolder()`

<br>


#### Descriptions:

- **getOpenAiApiKey:** Returns stored OpenAI API key.
- **getJavaSourceFolder:** Returns path to Java source folder to be analyzed.
- **getTestOutputFolder:** Returns path where generated test files should be stored.


#### Example Usage

`String apiKey = config.getOpenAiApiKey();`



### 7. dto.MethodMetaData
- Data Transfer Object that stores method information extracted from Java files.

| Method Signature                          | Description                                               |
|-------------------------------------------|-----------------------------------------------------------|
| `public String getOpenAiApiKey()`          | Returns stored OpenAI API key.                           |
| `public String getJavaSourceFolder()`     | Returns path to Java source folder to be analyzed.        |
| `public String getTestOutputFolder()`     | Returns path where generated test files should be stored. |

<br>


### 8. LazyDevApplication
- Main CLI runner that orchestrates the complete workflow: analyze, generate, sanitize, create test class, validate.


| Method Signature                            | Description                                                                                                                                   |
|---------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| `public static void main(String[] args)`    | Entry point of the application. Processes user input via command-line, runs AI service, and triggers test generation and validation pipeline. |



#### Example Run:
`java -jar lazydev.jar /path/to/source/file.java`

<br>



## 🔗 How It Works and How Classes Interact (Workflow)


#### 1. Analyze Java Classes:
Scans all .java files in /src/main/java, extracts method signatures, parameters, and exceptions.

#### 2. Generate JUnit Test Cases (with AI):

Sends method data to GPT-4 Turbo.
Receives fully functional JUnit test methods, using Mockito if needed.


#### 3. Sanitize AI Responses:
Cleans markdown, backticks, formatting issues before saving as .java files.


#### 4. Save and Organize Test Classes:
Puts them under appropriate package in /src/test/java.


#### 5. Run Tests Automatically:
Runs mvn test and displays success/failure.



<br>
<br>

### Workflow

```
┌──────────────┐
│  Java Files  │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│ CodeAnalyzer     │  - Extracts methods
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ AIService        │  - Generates tests
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ CodeSanitizer    │  - Cleans AI output
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ TestGenerator    │  - Creates test files
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ TestValidator    │  - Runs and validates
└──────────────────┘

```


## 💻 CLI Commands Available

| Command                               | Description                                                 |
|---------------------------------------|-------------------------------------------------------------|
| `generate-tests <source_folder>`       | Analyze, generate tests, and validate them                 |
| `analyze-only <source_folder>`         | Only analyze methods and show metadata                     |
| `validate-only <project_root_path>`    | Only run `mvn test` in existing project                    | 

#### Example Command:
`java -jar lazydev.jar generate-tests C:/Users/user/MyProject/src/main/java`




## 🎥 Demo



https://github.com/user-attachments/assets/1fba2228-5a59-44fb-a310-5ce87ebf9a1b

<br>


https://github.com/user-attachments/assets/fd7e5d11-12ef-4c7c-8a41-9a5f07173a5a

<br>

#### Backend Logs , Test/Target folders and @Test cases :

![Screenshot 2025-03-17 104004](https://github.com/user-attachments/assets/b3402739-51ea-414c-a2e6-b5f0dde4b0fa)
![Screenshot 2025-03-17 102333](https://github.com/user-attachments/assets/7020754a-f758-48ce-ae89-c62ad96a68ce)
![Screenshot 2025-03-17 102324](https://github.com/user-attachments/assets/6d4070d6-3720-4db6-b7e2-66f00f07a58a)
![Screenshot 2025-03-17 102254](https://github.com/user-attachments/assets/ef07e6a2-1551-4f01-80c2-0bc6e91d871f)
![Screenshot 2025-03-17 102218](https://github.com/user-attachments/assets/ec0afafa-96e3-48dd-96a8-c59b7c9e2f38)
![Screenshot 2025-03-17 102111](https://github.com/user-attachments/assets/b9356c3b-31f0-4be9-8367-b29448224690)
![Screenshot 2025-03-17 102027](https://github.com/user-attachments/assets/9282574a-ca68-40b8-84bd-4c4444fea19e)



<br>


## ⚠️ Known Limitations (as of now)
- AI might miss edge cases in complex methods.
- Relies on OpenAI — requires internet.
- Generated tests may need manual tweaks.
- Only for Java.


## 🚧 Future Improvements
- Support for private method testing.
- Add CLI flags for API keys, models, and temperature instead of only application.properties.
- Support local AI models (like Llama, CodeGen) for offline test generation.
- Generate test coverage reports to highlight untested methods.
- Optional AI-generated comments inside tests to explain test logic.
- Summary report after test generation and validation, showing counts and results.
- Support to more languages.



## 📢 Contact
For any questions, feedback, or contributions, feel free to reach out: <br>
**Email:** *anukulmaurya18@gmail.com* <br>
**This README will be updated as the project progresses. Stay tuned!**





