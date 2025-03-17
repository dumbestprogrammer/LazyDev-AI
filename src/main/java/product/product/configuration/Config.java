package product.product.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//Configuration class to load user-defined properties from application.properties.
@Component
@ConfigurationProperties(prefix = "lazydev")
public class Config {

    private String sourcePath;
    private String testPath;
    private String projectRootPath; //this is for the test validation location


    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getTestPath() {
        return testPath;
    }

    public void setTestPath(String testPath) {
        this.testPath = testPath;
    }

    public String getProjectRootPath() {
        return projectRootPath;
    }

    public void setProjectRootPath(String projectRootPath) {
        this.projectRootPath = projectRootPath;
    }

    @Override
    public String toString() {
        return "Config{" +
                "sourcePath='" + sourcePath + '\'' +
                ", testPath='" + testPath + '\'' +
                ", projectRootPath='" + projectRootPath + '\'' +
                '}';
    }
}
