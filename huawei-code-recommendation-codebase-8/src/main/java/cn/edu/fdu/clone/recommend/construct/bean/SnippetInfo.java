package cn.edu.fdu.clone.recommend.construct.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SnippetInfo {
    private int snippetId;
    private int methodId;
    private int groupId;
    private int type;
    private int startLine;
    private int endLine;
    private long startToken;
    private long endToken;
    private String body;

    public static String[] getHeader() {
        String[] result = new String[9];
        result[0] = "snippetId";
        result[1] = "methodId";
        result[2] = "groupId";
        result[3] = "type";
        result[4] = "startLine";
        result[5] = "endLine";
        result[6] = "startToken";
        result[7] = "endToken";
        result[8] = "body";

        return result;
    }

    public String[] getCsvFormat() {
        String[] result = new String[9];
        result[0] = snippetId + "";
        result[1] = methodId + "";
        result[2] = groupId + "";
        result[3] = type + "";
        result[4] = startLine + "";
        result[5] = endLine + "";
        result[6] = startToken + "";
        result[7] = endToken + "";
        result[8] = body;

        return result;
    }
}
