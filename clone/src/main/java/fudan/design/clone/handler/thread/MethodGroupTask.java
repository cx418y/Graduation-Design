package fudan.design.clone.handler.thread;

//import cn.edu.fdu.clone.recommend.construct.common.CodeBaseConstants;
import fudan.design.clone.bean.MethodInfo;
import fudan.design.clone.bean.State;
import fudan.design.clone.bean.TemplateInfoCSV;
import fudan.design.clone.bean.TemplateList;
import fudan.design.clone.common.SAGAConstants;
import fudan.design.clone.dto.Template;
import fudan.design.clone.dto.TemplateLineDIffList;
import fudan.design.clone.dto.TemplateLineItem;
import fudan.design.clone.extractor.MultiSet;
import fudan.design.clone.extractor.TemplateInfo;
import fudan.design.clone.extractor.TemplateLineSet;
import fudan.design.clone.handler.MethodResultHandler;
import fudan.design.clone.service.ElasticsearchService;
import fudan.design.clone.utils.code.CppCodeUtil;

import fudan.design.clone.common.CodeBaseConstants;


import com.opencsv.CSVWriter;
import fudan.design.clone.utils.sagaUtils.CodeBasePipeline;
import fudan.design.clone.utils.sagaUtils.FileUtil;
import fudan.design.clone.utils.sagaUtils.ProcessCounter;
import fudan.design.clone.utils.sagaUtils.TokenUtil;
import jakarta.annotation.Resource;
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

    @Resource
    private ElasticsearchService elasticsearchService;

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

            TemplateInfo templateInfo = new TemplateInfo(methodInfoList,groupId);
//            System.out.println(templateInfo.getRawTemplate().size());
//            System.out.println(templateInfo.getFinalTemplate().size());
            List<TemplateLineSet> finalTemplate = templateInfo.getFinalTemplate();
            Template template = saveTemplate(finalTemplate);

            CSVWriter templateWriter = new CSVWriter(new FileWriter(outputDirPath + File.separator + String.format(CodeBaseConstants.TEMPLATE_CSV, groupId)));
            List<String[]> templateCsvLine= new ArrayList<>();
            templateCsvLine.add(TemplateInfoCSV.getCsvHeader());
            templateCsvLine.add(getTemplateCsvLine(groupGroupId,template));
            templateWriter.writeAll(templateCsvLine);
            templateWriter.flush();
            templateWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("output group " + groupId + " csv file error!", e);
        } finally {
            counter.progress();
            countDownLatch.countDown();
        }


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
                String body = right.getSignature()+CppCodeUtil.parseMethodBody(path, startLine, endLine);
                System.out.println(body);
                right.setBody(body);
               // System.out.println(path+startLine+","+endLine);
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

    private String[] getTemplateCsvLine(int id,Template template){
        String[] result = new String[4];
        result[0] = id+"";
        result[1] = template.getMethodName();
        result[2] = template.getCommonString();
        result[3] = extractDiffString(template.getTemplateDiffs());
        return result;
    }

//    private void saveTemplateToElasticsearch( List<TemplateLineSet> finalTemplate){
//         StringBuilder methodNameSB = new StringBuilder();
//
//         StringBuilder templateCommonSB = new StringBuilder();
//
//        StringBuilder templateDetailsSB = new StringBuilder();
//        TemplateLineSet methodNameSet = finalTemplate.get(0);
//        methodNameSB.append(getMethodNameByFirstLine(methodNameSet.getMainLine().getToken().getOriginLineString()));
//        System.out.println("methodNameSB: "+methodNameSB);
//        for (TemplateLineSet set : finalTemplate) {
//            // System.out.print(set.getIndex()+": ");
//            if (set.getMainLine() != null) {
//                String origin = set.getMainLine().getToken().getOriginLineString();
//                if (set.getAlternateLines().size() == 0) {
//                    templateCommonSB.append(origin).append("\n");
//                } else {
//                    templateCommonSB.append("[*").append(set.getIndex()).append("*] ");
//                    templateCommonSB.append("[*").append(origin).append("*]").append("\n");
//                    // System.out.print("[*"+set.getIndex()+"*] ");
//                    //System.out.println("[*"+origin+"*]");
//                }
//                // System.out.println(set.getMainLine().getToken().getOriginLineString());
//            } else {
//                templateCommonSB.append("[*").append(set.getIndex()).append("*] ").append("\n");
//                // System.out.println("[*"+set.getIndex()+"*] ");
//            }
//
//            // System.out.println("[*"+set.getIndex()+"*] :");
//            if (set.getAlternateLines().size() > 0) {
//                templateDetailsSB.append("[*").append(set.getIndex()).append("*] :").append("\n");
//                for (MultiSet<TemplateLine> line : set.getAlternateLines()) {
//                    templateDetailsSB.append(line.getToken().getOriginLineString()).append("\n");
//                   // System.out.println(line.getToken().getOriginLineString());
//                }
//            }
//        }
//        System.out.println(methodNameSB);
//        System.out.println(templateCommonSB);
//        System.out.println(templateDetailsSB);
//
//        Template template = new Template();
//        template.setTemplateCommon(templateCommonSB.toString());
//        template.setTemplateDetails(templateDetailsSB.toString());
//        template.setMethodName(methodNameSB.toString());
//        TemplateList.addTemplate(template);
//    }
//
    private Template saveTemplate(List<TemplateLineSet> finalTemplate){
        StringBuilder methodNameSB = new StringBuilder();
        List<TemplateLineItem> templateCommon = new ArrayList<>();
        List<TemplateLineDIffList> templateDetails = new ArrayList<>();
        TemplateLineSet methodNameSet = finalTemplate.get(0);
        if(methodNameSet.getMainLine() != null){
            methodNameSB.append(getMethodNameByFirstLine(methodNameSet.getMainLine().getToken().getOriginLineString())).append(", ");
        }else{
            for(MultiSet set : methodNameSet.getAlternateLines()){
                methodNameSB.append(getMethodNameByFirstLine(set.getToken().getOriginLineString()));
            }
        }
       // methodNameSB.append(getMethodNameByFirstLine(methodNameSet.getMainLine().getToken().getOriginLineString()));
        System.out.println("methodNameSB: "+methodNameSB);
        for (TemplateLineSet set : finalTemplate) {
            if (set.getMainLine() != null) {
                MultiSet main = set.getMainLine();
                main.setFinalPosition(set.getIndex()+1);
                //TemplateLine line = set.getMainLine().getToken();
                TemplateLineItem item = new TemplateLineItem(main.getToken().getOriginLineString(),main.getOccurrenceCount(),main.getRate());
                templateCommon.add(item);
            }else{
                templateCommon.add(null);
            }
           // List<MultiSet> detailsLine = new ArrayList<>();
            if(set.getAlternateLines().size() != 0 || set.getMainLine().getRate()<1){
                TemplateLineDIffList diffList = new TemplateLineDIffList();
                diffList.setDiffId(templateDetails.size()+1);
                List<TemplateLineItem> list = new ArrayList<>();
                for(MultiSet line:set.getAlternateLines()){
                    TemplateLineItem item = new TemplateLineItem(line.getToken().getOriginLineString(),line.getOccurrenceCount(),line.getRate());
                    list.add(item);
                }
                diffList.setItems(list);
                templateDetails.add(diffList);
            }
           // templateDetails.add(detailsLine);
        }
        Template template = new Template();
        template.setMethodName(methodNameSB.toString());
        template.setTemplateCommon(templateCommon);
        template.setTemplateDiffs(templateDetails);
        template.setCommonString(extractCommonString(templateCommon));
//        System.out.println("final template:");
//        System.out.println("method name:" + methodNameSB);
//        System.out.println("common");
//        for(TemplateLineItem item:templateCommon){
//            System.out.println("item: "+item);
//        }
//        System.out.println("details");
//        for(TemplateLineDIffList list : templateDetails){
//            System.out.println("de: "+list);
//        }
        TemplateList.addTemplate(template);
        return template;

    }

    public String extractCommonString(List<TemplateLineItem> templateCommon){
        //List<TemplateLineItem> templateCommon = template.getTemplateCommon();
        StringBuilder commonSB = new StringBuilder();
        int missCount = 0;
        for(int i = 0;i < templateCommon.size();i++){
            TemplateLineItem item = templateCommon.get(i);
            if(item!=null){
                if(item.getFrequency() == 1) {
                    commonSB.append(item.getContent()).append("\n");
                }else{
                    commonSB.append(item.getContent()).append("[*").append(++missCount).append("*] ").append("\n");
                }
            }else{
                commonSB.append("[*").append(++missCount).append("*] ").append("\n");
            }
        }
        return commonSB.toString();
    }

    public String extractDiffString(List<TemplateLineDIffList> templateDiffs){
        StringBuilder diffSB = new StringBuilder();
        for(TemplateLineDIffList diffList: templateDiffs){
            diffSB.append("[*").append(diffList.getDiffId()).append("*]:").append("\n");
            for(TemplateLineItem item:diffList.getItems()){
                diffSB.append(item.getCount()).append(", ").append(item.getFrequency()).append(", ").append(item.getContent()).append("\n");
            }
        }
        return diffSB.toString();
    }

    private String getMethodNameByFirstLine(String firstLine){
        //System.out.println("first: "+firstLine);
        String front = firstLine.substring(0,firstLine.indexOf('('));
        String[] array = front.split(" ");
        return array[array.length-1];
    }
    private int compareTokens(byte[] left, byte[] right) {
        return TokenUtil.judgeCloneType(left, right, CodeBasePipeline.lang);
    }
}
