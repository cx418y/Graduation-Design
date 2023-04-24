package fudan.design.clone.utils.sagaUtils;

//import cn.edu.fdu.clone.recommend.construct.bean.MethodInfo;
//import cn.edu.fdu.clone.recommend.construct.bean.State;
//import cn.edu.fdu.clone.recommend.construct.common.CodeBaseConstants;
//import cn.edu.fdu.clone.recommend.construct.config.CodeBaseConfiguration;
//import cn.edu.fdu.clone.recommend.construct.config.SAGAConfiguration;
//import cn.edu.fdu.clone.recommend.construct.handler.codebase.BaseResultHandler;
//import cn.edu.fdu.clone.recommend.construct.handler.codebase.MethodResultHandler;
//import cn.edu.fdu.clone.recommend.construct.handler.codebase.SnippetResultHandler;
//import cn.edu.fdu.clone.recommend.enums.Language;
//import cn.edu.fdu.clone.recommend.utils.FileUtil;
//import cn.edu.fdu.clone.recommend.utils.ScriptUtil;
import fudan.design.clone.bean.MethodInfo;
import fudan.design.clone.bean.State;
import fudan.design.clone.handler.BaseResultHandler;
import fudan.design.clone.handler.MethodResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import fudan.design.clone.configuration.CodeBaseConfiguration;
import fudan.design.clone.configuration.SAGAConfiguration;
import fudan.design.clone.common.CodeBaseConstants;


//import javax.annotation.Resource;
import jakarta.annotation.Resource;
import java.io.File;

//import static cn.edu.fdu.clone.recommend.construct.common.CodeBaseConstants.CODEBASE_FOLDER;
//import static cn.edu.fdu.clone.recommend.construct.common.SAGAConstants.SAGA_CONFIG_FILE;
import static fudan.design.clone.common.CodeBaseConstants.CODEBASE_FOLDER;
import static fudan.design.clone.common.SAGAConstants.SAGA_CONFIG_FILE;

@Slf4j
@Component
public class CodeBasePipeline {

    @Resource
    private CodeBaseConfiguration codeBaseConfiguration;
    @Resource
    private SAGAConfiguration sagaConfiguration;
    public static Language lang;

    public void run() {
        setupConstants();

        File cloneDetectTool = new File(codeBaseConfiguration.getClone().getSagaPath());
        File cloneDetectToolDir = cloneDetectTool.getParentFile();

        boolean processDetect = codeBaseConfiguration.getClone().isDetect();
        if (processDetect) {
            String repoPath = codeBaseConfiguration.getRepoPath();

            String configFilePath = cloneDetectToolDir.getAbsolutePath() + File.separator + SAGA_CONFIG_FILE;
            File configFile = new File(configFilePath);

            FileUtil.deleteFileIfExists(configFile);

            // 加载克隆配置
            log.info("load saga configuration");
            lang = sagaConfiguration.loadProperties();
            sagaConfiguration.writeConfigurationFile(configFilePath);

            //进行方法级克隆检测
            log.info("tokenize & build & parse method result");
            ScriptUtil.executeJarFile(cloneDetectToolDir, cloneDetectTool, repoPath);

            //重置配置文件
//            log.info("reset saga configuration");
//            sagaConfiguration.loadPropertiesForSnippetParse();
//            sagaConfiguration.writeConfigurationFile(configFilePath);
//
//           // 进行片段级克隆检测
//            log.info("parse snippet result");
//            ScriptUtil.executeJarFile(cloneDetectToolDir, cloneDetectTool, repoPath);
        }

        //创建文件夹
        File codeBaseFolder = new File(CODEBASE_FOLDER);
        FileUtil.deleteFileIfExists(codeBaseFolder);
        if (!codeBaseFolder.mkdirs()) {
            log.error("mkdir " + codeBaseFolder.getAbsolutePath() + " error");
            return;
        }

        State.load(cloneDetectToolDir.getAbsolutePath());
        MethodInfo.loadMethodList(cloneDetectToolDir.getAbsolutePath());

        //聚集方法级克隆组
        log.info("gather method result");
        BaseResultHandler handler = new MethodResultHandler(cloneDetectToolDir.getAbsolutePath());
        handler.handle();

        //聚集片段级克隆组
//        log.info("gather snippet result");
//        handler = new SnippetResultHandler(cloneDetectToolDir.getAbsolutePath());
//        handler.handle();

        MethodInfo.destroyMethodList();
    }

    private void setupConstants() {
        // 设置线程数和分割量
        CodeBaseConstants.SEP_NUM = codeBaseConfiguration.getSepNum();
        CodeBaseConstants.CORE_POOL_SIZE = codeBaseConfiguration.getThreadNum();
        CodeBaseConstants.MAX_POOL_SIZE = codeBaseConfiguration.getThreadNum();
    }
}
