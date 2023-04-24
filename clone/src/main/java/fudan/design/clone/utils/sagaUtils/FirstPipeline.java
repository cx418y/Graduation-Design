package fudan.design.clone.utils.sagaUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//import javax.annotation.Resource;
import jakarta.annotation.Resource;

@Slf4j
@Component
public class FirstPipeline {

    @Resource
    private CodeBasePipeline codeBasePipeline;

    @Resource
   //private DatabasePipeline databasePipeline;

    @Value("${code-base.process}")
    private boolean processCodeBase;
    public void run() {
        //System.out.println(processCodeBase);
        if (processCodeBase) {
            log.info("start constructing codebase");
            codeBasePipeline.run();
            log.info("construct codebase finished");
        }
    }
}
