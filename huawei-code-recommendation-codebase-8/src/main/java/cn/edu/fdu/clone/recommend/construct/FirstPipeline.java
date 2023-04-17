package cn.edu.fdu.clone.recommend.construct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FirstPipeline {

    @Resource
    private CodeBasePipeline codeBasePipeline;

    @Resource
    private DatabasePipeline databasePipeline;

    @Value("${code-base.process}")
    private boolean processCodeBase;

    public void run() {
        if (processCodeBase) {
            log.info("start constructing codebase");
            codeBasePipeline.run();
            log.info("construct codebase finished");
        }
    }
}
