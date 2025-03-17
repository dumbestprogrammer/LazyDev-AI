package product.product.dto;

import java.util.List;

/**
 * DTO for storing extracted method metadata from Java source code.
 */
public class MethodMetaData {
    private final String className;
    private final String methodName;
    private final String signature;
    private final String returnType;
    private final List<String> parameterTypes;
    private final List<String> annotations;
    private final List<String> thrownExceptions;
    private final String accessModifier;

    public MethodMetaData(String className, String methodName, String signature, String returnType,
                          List<String> parameterTypes, List<String> annotations,
                          List<String> thrownExceptions, String accessModifier) {
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.annotations = annotations;
        this.thrownExceptions = thrownExceptions;
        this.accessModifier = accessModifier;
    }

    // ✅ Proper getters
    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getSignature() {
        return signature;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public List<String> getThrownExceptions() {
        return thrownExceptions;
    }

    public String getAccessModifier() {
        return accessModifier;
    }

    @Override
    public String toString() {
        return "Class: " + className + ", Method: " + methodName +
                ", Signature: " + signature + ", Return: " + returnType +
                ", Params: " + parameterTypes + ", Annotations: " + annotations +
                ", Throws: " + thrownExceptions + ", Modifier: " + accessModifier;
    }
}
