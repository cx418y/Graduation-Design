package cn.edu.fdu.clone.recommend.construct.config;

import cn.edu.fdu.clone.recommend.enums.Language;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

@Data
@Slf4j
@Component
@PropertySource(value = {"classpath:construct.yml"}, encoding = "utf-8", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "clone.detect.saga.config", ignoreUnknownFields = false)
public class SAGAConfiguration {

    private String language = "cpp";
    private String extensions = "c,cpp";
    private float threshold = 0.7f;
    private int mlc = 50;
    private int mlcc = 20;
    private int minLine = 2;
    private long sepNum = 100000000;
    private String exe = "";
    private String granularity = "snippet";
    private int openStringHash = 1;
    private int useLongType = 0;
    private int tokenize = 1;
    private int processTokenize = 1;
    private int processBuild = 1;
    private int processParse = 1;
    private int threadNum = 8;
    private Properties properties;

    public Language loadProperties() {
        properties = new Properties();
        properties.setProperty("language", language);
        properties.setProperty("extensions", extensions);
        properties.setProperty("threshold", String.valueOf(threshold));
        properties.setProperty("mlc", String.valueOf(mlc));
        properties.setProperty("mlcc", String.valueOf(mlcc));
        properties.setProperty("min-line", String.valueOf(minLine));
        properties.setProperty("sep-num", String.valueOf(sepNum));
        properties.setProperty("exe", exe);
        properties.setProperty("granularity", granularity);
        properties.setProperty("open-string-hash", String.valueOf(openStringHash));
        properties.setProperty("use-long-type", String.valueOf(useLongType));
        properties.setProperty("tokenize", String.valueOf(tokenize));
        properties.setProperty("process-tokenize", String.valueOf(processTokenize));
        properties.setProperty("process-build", String.valueOf(processBuild));
        properties.setProperty("process-parse", String.valueOf(processParse));
        properties.setProperty("thread-num", String.valueOf(threadNum));

        if (tokenize * openStringHash == 0)
            return null;
        if (language.equals("java"))
            return Language.JAVA;
        if (language.equals("cpp"))
            return Language.CPP;
        return null;
    }

    public void loadPropertiesForSnippetParse() {
        granularity = "method";
        processTokenize = 0;
        processBuild = 0;
        properties.setProperty("granularity", granularity);
        properties.setProperty("process-tokenize", String.valueOf(processTokenize));
        properties.setProperty("process-build", String.valueOf(processBuild));
    }

    public void writeConfigurationFile(String configFilePath) {
        try {
            properties.store(new FileWriter(configFilePath), "");
        } catch (IOException e) {
            log.error("print saga config file error", e);
        }
    }

}
