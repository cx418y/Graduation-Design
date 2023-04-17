package cn.edu.fdu.clone.recommend.construct.handler.codebase;

import cn.edu.fdu.clone.recommend.construct.common.CodeBaseConstants;
import cn.edu.fdu.clone.recommend.construct.common.SAGAConstants;
import cn.edu.fdu.clone.recommend.construct.handler.codebase.thread.MethodGroupTask;
import cn.edu.fdu.clone.recommend.utils.ProcessCounter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MethodResultHandler extends BaseResultHandler {

    // 记录type12克隆组
    public static AtomicInteger CG;

    public MethodResultHandler(String sagaFolderPath) {
        super(sagaFolderPath);
        CG = new AtomicInteger(0);
    }

    public void handle() {
        // 创建文件夹
        File methodResultFolder = new File(CodeBaseConstants.METHOD_CODEBASE_FOLDER);
        if (!methodResultFolder.mkdirs()) {
            log.error("mkdir " + methodResultFolder.getAbsolutePath() + "error");
            return;
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CodeBaseConstants.CORE_POOL_SIZE,
                CodeBaseConstants.MAX_POOL_SIZE,
                CodeBaseConstants.KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 逐行读取克隆组分发任务
        try {
            CSVReader reader = new CSVReader(new FileReader(sagaFolderPath + File.separator + SAGAConstants.SAGA_CLONE_GROUP_FILE));
            int groupId = 0;
            List<String[]> groups = reader.readAll();

            int n = groups.size();
            log.info("total method groups:" + n);
            ProcessCounter counter = new ProcessCounter(n, "method divide");

            final CountDownLatch countDownLatch = new CountDownLatch(n);
            for (String[] group : groups) {
                MethodGroupTask methodGroupTask = new MethodGroupTask(group,
                        countDownLatch, groupId, sagaFolderPath, "method", counter);
                executor.execute(methodGroupTask);
                groupId++;
            }
            log.info("process method results...");
            countDownLatch.await();
        } catch (IOException | CsvException | InterruptedException e) {
            log.error("parse clone group error:", e);
        }
    }
}
