package cn.edu.fdu.clone.recommend.construct.handler.codebase.thread;

import cn.edu.fdu.clone.recommend.construct.bean.SnippetInfo;
import cn.edu.fdu.clone.recommend.construct.common.CodeBaseConstants;
import cn.edu.fdu.clone.recommend.utils.ProcessCounter;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class SnippetGroupTask implements Runnable {

    private final int groupId;
    private final CountDownLatch countDownLatch;
    private final List<SnippetInfo> snippetInfoList;
    private final List<GroupLockPair> groupLockPairs;
    private final ProcessCounter counter;

    public SnippetGroupTask(int groupId, CountDownLatch countDownLatch, List<SnippetInfo> snippetInfoList,
                            List<GroupLockPair> groupLockPairs, ProcessCounter counter) {
        this.groupId = groupId;
        this.countDownLatch = countDownLatch;
        this.snippetInfoList = snippetInfoList;
        this.groupLockPairs = groupLockPairs;
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            for (GroupLockPair groupLockPair: groupLockPairs){
                processGatherGroup(groupLockPair);
            }
        }
        catch (IOException e){
            log.error("output snippet group " + groupId + " csv file error!", e);
        }
        finally {
            counter.progress();
            countDownLatch.countDown();
        }
    }

    private void processGatherGroup(GroupLockPair groupLockPair) throws IOException {
        int gatherId = groupLockPair.gatherId;
        int groupGroupId = gatherId / CodeBaseConstants.SEP_NUM;
        String outputFilePath = CodeBaseConstants.SNIPPET_CODEBASE_FOLDER + File.separator +
                groupGroupId + File.separator + gatherId + File.separator +
                String.format(CodeBaseConstants.SNIPPET_GROUP_CSV, gatherId);
        File outputFile = new File(outputFilePath);

        ReentrantReadWriteLock rwLock = groupLockPair.lock;
        rwLock.writeLock().lock();

        boolean writeHeader = !outputFile.exists();

        FileWriter fw = new FileWriter(outputFile, true);
        CSVWriter writer = new CSVWriter(fw);

        if (writeHeader)
            writer.writeNext(SnippetInfo.getHeader());

        for (SnippetInfo info : snippetInfoList) {
            writer.writeNext(info.getCsvFormat());
        }

        writer.close();
        rwLock.writeLock().unlock();
    }
}
