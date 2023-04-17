package cn.edu.fdu.clone.recommend.construct.bean;

import cn.edu.fdu.clone.recommend.construct.common.SAGAConstants;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class State {
    //上一次处理过的文件索引
    public static int lastProcessedFileIndex;
    //allTokenCsv的数量
    public static int outputId;
    //(文件id/方法id)
    public static int measureId;
    //代码行数
    public static long line;
    //token数量
    public static long tokenNum;
    //每个allTokenCsv的第一个token在总token串中的索引
    public static List<Long> tokenIndexList;

    public static void load(String sagaFolderPath) {
        try {
            InputStream is = FileUtils.openInputStream(new File(sagaFolderPath + File.separator + SAGAConstants.STATE_FILENAME));
            JSONObject jObj = JSON.parseObject(is);
            lastProcessedFileIndex = jObj.getIntValue("lastProcessedFileIndex");
            line = jObj.getLongValue("line");
            outputId = jObj.getIntValue("outputId");
            tokenNum = jObj.getIntValue("tokenNum");
            measureId = jObj.getIntValue("measureId");
            tokenIndexList = new ArrayList<>();
            tokenIndexList = jObj.getList("tokenIndexList", Long.class);
        } catch (IOException e) {
            log.error("load state.json error");
            System.exit(-1);
        }
    }

}
