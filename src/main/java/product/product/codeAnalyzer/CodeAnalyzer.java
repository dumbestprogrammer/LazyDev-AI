package product.product.codeAnalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import product.product.configuration.Config;
import product.product.dto.MethodMetaData;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*

Role of this class :
- Analyze Java source code to extract detailed information about each method — like method name, parameters, return type, exceptions, annotations, etc.
- This metadata will later be used to generate unit tests automatically.

Library Used:
- Uses Spoon (a popular library for analyzing and transforming Java code).
 */
@Component
public class CodeAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeAnalyzer.class);
    private final Config config;


    public CodeAnalyzer(Config config) {
        this.config = config;
    }



    /*
     Analyzes Java source files and extracts detailed method metadata.

     sourceFolderPath is the path to the Java source directory.
     returns list of MethodMetaData for all extracted methods.
     */
    public List<MethodMetaData> analyzeSource(String sourceFolderPath) {
        List<MethodMetaData> analyzedMethods = new ArrayList<>();


        /*
        - Validate input path
        - Check if the provided folder path is valid.
        - If not, log error and return an empty list.
         */
        if (sourceFolderPath == null || sourceFolderPath.trim().isEmpty()) {
            LOGGER.error("Provided source folder path is null or empty.");
            return analyzedMethods;
        }

        File sourceFolder = new File(sourceFolderPath);  //Checks if the provided path actually exists and is a directory.
        if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
            LOGGER.error(" Invalid source folder provided: {}", sourceFolderPath);
            return analyzedMethods;
        }

        try {
            LOGGER.info(" Starting analysis of source folder: {}", sourceFolderPath);

            // Setup Spoon launcher
            Launcher launcher = new Launcher();
            launcher.addInputResource(sourceFolder.getAbsolutePath());
            launcher.getEnvironment().setNoClasspath(true);
            launcher.buildModel();

            /*
            - Load Java files from the directory.
            - setNoClasspath(true) helps analyze code without needing full dependencies (good for incomplete projects).
            */
            CtModel model = launcher.getModel();

            // Extract classes and methods
            for (CtType<?> type : model.getAllTypes()) {
                if (type instanceof CtClass<?> clazz) {
                    LOGGER.info(" Analyzing class: {}", clazz.getQualifiedName());

                    // Process each method
                    for (CtMethod<?> method : clazz.getMethods()) {
                        MethodMetaData metaData = extractMethodMetaData(clazz, method);
                        analyzedMethods.add(metaData);
                        LOGGER.debug(" Method found: {} in class {}", method.getSimpleName(), clazz.getQualifiedName());
                    }
                }
            }

            LOGGER.info("Analysis completed. Total methods found: {}", analyzedMethods.size());

        } catch (Exception e) {
            LOGGER.error(" Exception during code analysis: {}", e.getMessage(), e);
        }

        return analyzedMethods;
    }




    /*
     Extracts detailed metadata from a given method.

     clazz is for the  Class containing the method.
     method The method to analyze.
     returns MethodMetaData with detailed extracted data.
     */

    private MethodMetaData extractMethodMetaData(CtClass<?> clazz, CtMethod<?> method) {
        String methodName = method.getSimpleName();
        String signature = method.getSignature();
        String returnType = (method.getType() != null) ? method.getType().getQualifiedName() : "void";

        // Parameters
        List<String> parameterTypes = new ArrayList<>();
        method.getParameters().forEach(param -> parameterTypes.add(param.getType().getQualifiedName()));

        // Annotations
        List<String> annotations = new ArrayList<>();
        method.getAnnotations().forEach(annotation -> annotations.add(annotation.getAnnotationType().getQualifiedName()));

        // Thrown Exceptions
        List<String> thrownExceptions = new ArrayList<>();
        method.getThrownTypes().forEach(exception -> thrownExceptions.add(exception.getQualifiedName()));

        // Access Modifier
        String accessModifier = method.getModifiers().toString();

        return new MethodMetaData(
                clazz.getQualifiedName(),
                methodName,
                signature,
                returnType,
                parameterTypes,
                annotations,
                thrownExceptions,
                accessModifier
        );
    }
}
