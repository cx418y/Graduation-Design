package fudan.design.clone.bean;

//import cn.edu.fdu.clone.recommend.construct.common.SAGAConstants;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import fudan.design.clone.common.SAGAConstants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class MethodInfo {

    private int methodId;
    private int CG_T3;
    private int CG_T2;
    private int CG_T1;
    //private String oriMethodId;
    private int tokenCsvId;
    private String filePath;
    private int startLine;
    private int endLine;
    private String signature;
    private String body;
    private long oriStartToken;
    private long oriEndToken;
    private long startToken;
    private long endToken;

    public void setEndToken() {
        endToken = startToken + oriEndToken - oriStartToken;
    }

    public String[] getCsvFormatForSnippet() {
        String[] result = new String[12];
        result[0] = methodId + "";
        result[1] = CG_T3 + "";
        result[2] = tokenCsvId + "";
        result[3] = filePath;
        result[4] = startLine + "";
        result[5] = endLine + "";
        result[6] = oriStartToken + "";
        result[7] = oriEndToken + "";
        result[8] = startToken + "";
        result[9] = endToken + "";
        result[10] = signature;
        result[11] = body;

        return result;
    }

    public String[] getCsvFormatForMethod() {
        String[] result = new String[14];
        result[0] = methodId + "";
        result[1] = CG_T3 + "";
        result[2] = CG_T2 + "";
        result[3] = CG_T1 + "";
        result[4] = tokenCsvId + "";
        result[5] = filePath;
        result[6] = startLine + "";
        result[7] = endLine + "";
        result[8] = oriStartToken + "";
        result[9] = oriEndToken + "";
        result[10] = startToken + "";
        result[11] = endToken + "";
        result[12] = signature;
        result[13] = body;

        return result;
    }

    public static String[] getSnippetCsvHeader() {
        String[] result = new String[12];
        result[0] = "methodId";
        result[1] = "groupId";
        result[2] = "tokenCsvId";
        result[3] = "filePath";
        result[4] = "startLine";
        result[5] = "endLine";
        result[6] = "oriStartToken";
        result[7] = "oriEndToken";
        result[8] = "startToken";
        result[9] = "endToken";
        result[10] = "signature";
        result[11] = "body";

        return result;
    }

    public static String[] getMethodCsvHeader() {
        String[] result = new String[14];
        result[0] = "methodId";
        result[1] = "CG_T3";
        result[2] = "CG_T2";
        result[3] = "CG_T1";
        result[4] = "tokenCsvId";
        result[5] = "filePath";
        result[6] = "startLine";
        result[7] = "endLine";
        result[8] = "oriStartToken";
        result[9] = "oriEndToken";
        result[10] = "startToken";
        result[11] = "endToken";
        result[12] = "signature";
        result[13] = "body";

        return result;
    }

    public static List<MethodInfo> methodInfoList;

    public static void loadMethodList(String sagaFolderPath) {
        methodInfoList = new ArrayList<>();

        try (FileReader fr = new FileReader(sagaFolderPath + File.separator +
                SAGAConstants.SAGA_MEASURE_INDEX_FILE)) {
            // 读 MeasureList
            CSVParser csvParser = new CSVParserBuilder().withEscapeChar(' ').build();
            CSVReader reader = new CSVReaderBuilder(fr).withCSVParser(csvParser).build();

            String[] tmp;
            while ((tmp = reader.readNext()) != null) {
                MethodInfo methodInfo = new MethodInfo();
                methodInfo.setMethodId(Integer.parseInt(tmp[0]));
                methodInfo.setFilePath(tmp[1].replace("\\", "\\\\"));
                methodInfo.setStartLine(Integer.parseInt(tmp[2]));
                methodInfo.setEndLine(Integer.parseInt(tmp[3]));
                methodInfoList.add(methodInfo);
            }
            // 读 measureList%d.csv
            for (int i = 0; i < State.outputId; i++) {
                reader = new CSVReader(new FileReader(String.format(sagaFolderPath + File.separator + SAGAConstants.MEASURE_LIST, i)));
                while ((tmp = reader.readNext()) != null) {
                    int mid = Integer.parseInt(tmp[5]);
                    MethodInfo methodInfo = methodInfoList.get(mid);
                    methodInfo.setOriStartToken(Long.parseLong(tmp[2]));
                    methodInfo.setOriEndToken(Long.parseLong(tmp[3]));
                    methodInfo.setTokenCsvId(i);
                }
            }
            reader.close();
        } catch (IOException | CsvValidationException e) {
            log.error("load method list error!", e);
            System.exit(-1);
        }
    }

    public static void destroyMethodList() {
        methodInfoList = null;
    }
}
