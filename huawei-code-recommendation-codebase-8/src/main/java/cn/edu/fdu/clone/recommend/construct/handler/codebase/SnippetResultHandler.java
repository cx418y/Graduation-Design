package cn.edu.fdu.clone.recommend.construct.handler.codebase;

import cn.edu.fdu.clone.recommend.construct.bean.SnippetInfo;
import cn.edu.fdu.clone.recommend.construct.common.CodeBaseConstants;
import cn.edu.fdu.clone.recommend.construct.common.SAGAConstants;
import cn.edu.fdu.clone.recommend.construct.handler.codebase.thread.GroupLockPair;
import cn.edu.fdu.clone.recommend.construct.handler.codebase.thread.MethodGroupTask;
import cn.edu.fdu.clone.recommend.construct.handler.codebase.thread.SnippetGroupTask;
import cn.edu.fdu.clone.recommend.utils.ProcessCounter;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class SnippetResultHandler extends BaseResultHandler {

    private final Map<Integer, Integer> method2gather = new HashMap<>();
    private static final Map<Integer, GroupLockPair> gatherLockMap = new ConcurrentHashMap<>();
    private int snippetId = 0;

    public SnippetResultHandler(String sagaFolderPath) {
        super(sagaFolderPath);
    }

    public void handle() {
        File snippetResultFolder = new File(CodeBaseConstants.SNIPPET_CODEBASE_FOLDER);
        if (!snippetResultFolder.mkdirs()) {
            log.error("mkdir " + snippetResultFolder.getAbsolutePath() + "error");
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

        // 逐行读取克隆组分发方法级任务
        try {
            CSVReader reader = new CSVReader(new FileReader(sagaFolderPath + File.separator + SAGAConstants.TYPE3_SNIPPET_CLONE_GROUP_GATHER_FILENAME));
            int groupId = 0;
            List<String[]> groups = reader.readAll();
            int n = groups.size();
            log.info("total snippet groups:" + n);
            ProcessCounter counter = new ProcessCounter(n, "snippet divide");

            final CountDownLatch countDownLatch = new CountDownLatch(n);
            for (String[] group : groups) {
                for (String mid : group) {
                    method2gather.put(Integer.parseInt(mid), groupId);
                }
                MethodGroupTask methodGroupTask = new MethodGroupTask(group,
                        countDownLatch, groupId, sagaFolderPath, "snippet", counter);
                executor.execute(methodGroupTask);
                groupId++;
            }
            log.info("process snippet gather results...");
            countDownLatch.await();
        } catch (IOException | CsvException | InterruptedException e) {
            log.error("parse clone group error:", e);
        }

        // 逐行读取克隆组分发片段级任务
        processSnippetResult(12, executor);
        processSnippetResult(3, executor);
    }

    private void processSnippetResult(int type, ThreadPoolExecutor executor) {

        try {

            FileReader fr = new FileReader(sagaFolderPath + File.separator +
                    String.format(SAGAConstants.SAGA_CLONE_SNIPPET_TYPE_GROUP_FILE, type));
            CSVParser csvParser = new CSVParserBuilder().withEscapeChar(' ').build();
            CSVReader reader = new CSVReaderBuilder(fr).withCSVParser(csvParser).build();

            int groupId = -1;
            Set<Integer> gatherIds = new HashSet<>();
            int n = 0;
            String[] csvLine;

            while ((csvLine = reader.readNext()) != null) {
                if (csvLine.length > 1) {
                    int curGroupId = Integer.parseInt(csvLine[0]);
                    if (groupId != curGroupId) {
                        groupId = curGroupId;
                        n++;
                    }
                }
            }

            reader.close();
            final CountDownLatch countDownLatch = new CountDownLatch(n);
            log.info("total snippet type" + type + " groups:" + n);
            ProcessCounter counter = new ProcessCounter(n, "parse snippet type" + type);

            fr = new FileReader(sagaFolderPath + File.separator +
                    String.format(SAGAConstants.SAGA_CLONE_SNIPPET_TYPE_GROUP_FILE, type));
            reader = new CSVReaderBuilder(fr).withCSVParser(csvParser).build();
            groupId = -1;
            List<SnippetInfo> tmpList = new ArrayList<>();

            while ((csvLine = reader.readNext()) != null) {
                if (csvLine.length <= 1) {
                    if (snippetId > 0 && gatherIds.size() > 0) {
                        List<GroupLockPair> groupLockPairs = new ArrayList<>();
                        for (int gatherId: gatherIds) {
                            if (gatherId == -1)
                                continue;
                            GroupLockPair groupLockPair = gatherLockMap.getOrDefault(gatherId, new GroupLockPair(gatherId));
                            gatherLockMap.put(gatherId, groupLockPair);
                            groupLockPairs.add(groupLockPair);
                        }
                        SnippetGroupTask snippetGroupTask = new SnippetGroupTask(groupId, countDownLatch, tmpList, groupLockPairs, counter);
                        executor.execute(snippetGroupTask);
                    }
                    gatherIds.clear();
                    tmpList = new ArrayList<>();
                } else {
                    groupId = Integer.parseInt(csvLine[0]);
                    int methodId = Integer.parseInt(csvLine[1]);
                    gatherIds.add(method2gather.getOrDefault(methodId, -1));
                    SnippetInfo snippetInfo = new SnippetInfo(
                            snippetId,
                            methodId,
                            groupId,
                            type,
                            Integer.parseInt(csvLine[7]),
                            Integer.parseInt(csvLine[8]),
                            Long.parseLong(csvLine[5]),
                            Long.parseLong(csvLine[6]), ""
                    );
                    tmpList.add(snippetInfo);
                    snippetId++;
                }
            }

            log.info("process snippet results...");
            countDownLatch.await();
            reader.close();
        } catch (IOException | CsvException | InterruptedException e) {
            log.error("parse type" + type + " snippet clone group error:", e);
        }
    }
}
