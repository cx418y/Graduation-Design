package fudan.design.clone.handler.thread;

//import cn.edu.fdu.clone.recommend.construct.common.CodeBaseConstants;
import fudan.design.clone.bean.MethodInfo;
import fudan.design.clone.bean.State;
import fudan.design.clone.common.SAGAConstants;
import fudan.design.clone.extractor.TemplateInfo;
import fudan.design.clone.extractor.TemplateLineSet;
import fudan.design.clone.handler.MethodResultHandler;
import fudan.design.clone.utils.code.CppCodeUtil;

import fudan.design.clone.common.CodeBaseConstants;


import com.opencsv.CSVWriter;
import fudan.design.clone.utils.sagaUtils.CodeBasePipeline;
import fudan.design.clone.utils.sagaUtils.FileUtil;
import fudan.design.clone.utils.sagaUtils.ProcessCounter;
import fudan.design.clone.utils.sagaUtils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class MethodGroupTask implements Runnable {

    private final List<MethodInfo> methodInfoList;
    private final CountDownLatch countDownLatch;
    private final int groupId;
    private final String sagaFolderPath;
    private final String granularity;
    private final ProcessCounter counter;

    public MethodGroupTask(String[] group, CountDownLatch countDownLatch, int groupId,
                           String sagaFolderPath, String granularity, ProcessCounter counter) {
        methodInfoList = new ArrayList<>();
        for (String methodId : group) {
            int mid = Integer.parseInt(methodId);
            methodInfoList.add(MethodInfo.methodInfoList.get(mid));
        }
        this.groupId = groupId;
        this.sagaFolderPath = sagaFolderPath;
        this.countDownLatch = countDownLatch;
        this.granularity = granularity;
        this.counter = counter;
    }

    @Override
    public void run() {
        // 创建相应目录
        int groupGroupId = groupId / CodeBaseConstants.SEP_NUM;

        String outputDirPath = CodeBaseConstants.METHOD_CODEBASE_FOLDER + File.separator + groupGroupId + File.separator + groupId;
        String[] csvHeader = MethodInfo.getMethodCsvHeader();

        if (granularity.equals("snippet")) {
            outputDirPath = CodeBaseConstants.SNIPPET_CODEBASE_FOLDER + File.separator + groupGroupId + File.separator + groupId;
            csvHeader = MethodInfo.getSnippetCsvHeader();
        }
        try {
            FileUtils.forceMkdir(new File(outputDirPath));
        } catch (IOException e) {
            log.error("create " + granularity + " group " + groupId + " folder fail!", e);
            counter.progress();
            countDownLatch.countDown();
            return;
        }

        List<String[]> csvLines = new ArrayList<>();
        csvLines.add(csvHeader);
        csvLines.addAll(divideGroup(outputDirPath));

        // 输出到csv
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(outputDirPath + File.separator + String.format(CodeBaseConstants.METHOD_GROUP_CSV, groupId)));
            writer.writeAll(csvLines);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error("output group " + groupId + " csv file error!", e);
        } finally {
            counter.progress();
            countDownLatch.countDown();
        }

        TemplateInfo templateInfo = new TemplateInfo(methodInfoList,groupId);
        System.out.println(templateInfo.getRawTemplate().size());
        System.out.println(templateInfo.getFinalTemplate().size());
        List<TemplateLineSet> finalTemplate = templateInfo.getFinalTemplate();
        writeTemplate(finalTemplate);

    }

    private List<String[]> divideGroup(String outputDirPath) {
        List<String[]> csvLines = new ArrayList<>();
        List<byte[]> tokensList = new ArrayList<>();

        int methodCount = methodInfoList.size();
        for (int i = 0; i < methodCount; i++) {
            MethodInfo right = methodInfoList.get(i);
            //处理方法体和方法签名
            String path = right.getFilePath();
            int startLine = right.getStartLine();
            int endLine = right.getEndLine();

            // 更新代码内容和签名
            right.setCG_T3(groupId);
            if (right.getBody() == null || right.getBody().length() == 0){
                right.setSignature(CppCodeUtil.parseMethodSignature(path, startLine));
                right.setBody(CppCodeUtil.parseMethodBody(path, startLine, endLine));
            }

            if (right.getBody() == null || right.getBody().length() == 0){
                log.error("parse body wrong," + groupId + " " + path + "," + startLine + "-" + endLine);
                right.setBody(CppCodeUtil.parseMethodBodySimple(path, startLine, endLine));
            }

            // 更新token起止位置
            int csvId = right.getTokenCsvId();
            String inputFileName = String.format(sagaFolderPath + File.separator + SAGAConstants.ALL_TOKEN_CSV, csvId);
            String outputFileName = outputDirPath + File.separator + CodeBaseConstants.TOKEN_CSV;
            long tokenStartIndex = State.tokenIndexList.get(csvId);

            // 读取tokens
            byte[] rightTokens = FileUtil.getBytes(inputFileName, right.getOriStartToken() - tokenStartIndex, right.getOriEndToken() - tokenStartIndex);
            tokensList.add(i, rightTokens);
            // 写入tokens
//            right.setStartToken(FileUtil.writeBytes(outputFileName, rightTokens));
//            right.setEndToken();

            // 片段级不用比较
            if (granularity.equals("snippet")) {
                csvLines.add(right.getCsvFormatForSnippet());
                continue;
            }

            // 方法级比较
            if (i == 0) {
                right.setCG_T1(MethodResultHandler.CG.getAndIncrement());
                right.setCG_T2(MethodResultHandler.CG.getAndIncrement());
                csvLines.add(right.getCsvFormatForMethod());
                continue;
            }
            if (rightTokens == null) {
                continue;
            }

            int g1 = -1, g2 = -1;
            for (int j = i - 1; j > -1; j--) {
                MethodInfo left = methodInfoList.get(j);
                byte[] leftTokens = tokensList.get(j);
                if (leftTokens == null)
                    continue;
                int cloneType = compareTokens(leftTokens, rightTokens);

                if (cloneType == 1) {
                    g1 = left.getCG_T1();
                    g2 = left.getCG_T2();
                    break;
                } else if (cloneType == 2) {
                    g2 = left.getCG_T2();
                }
            }
            g1 = g1 == -1 ? MethodResultHandler.CG.getAndIncrement() : g1;
            g2 = g2 == -1 ? MethodResultHandler.CG.getAndIncrement() : g2;

            right.setCG_T1(g1);
            right.setCG_T2(g2);
            csvLines.add(right.getCsvFormatForMethod());
        }

        return csvLines;
    }

    private void writeTemplate(List<TemplateLineSet> finalTemplate){

    }

    private int compareTokens(byte[] left, byte[] right) {
        return TokenUtil.judgeCloneType(left, right, CodeBasePipeline.lang);
    }
}
