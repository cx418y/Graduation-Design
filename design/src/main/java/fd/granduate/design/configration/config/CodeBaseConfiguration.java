package fd.granduate.design.configration.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@PropertySource(value = {"classpath:construct.yml"}, encoding = "utf-8", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "code-base", ignoreUnknownFields = false)
public class CodeBaseConfiguration {

    private boolean process = false;
    private String repoPath = "";
    private int threadNum = 1;
    private int sepNum = 1000;
    private DetectConfiguration clone;

    @Data
    public static class DetectConfiguration {
        private boolean detect = false;
        private String sagaPath;
    }

}
