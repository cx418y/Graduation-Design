package fudan.design.clone.utils.code;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Xiaochen Wang
 * @date 2022/1/4 16:57
 */

@Slf4j
public class CppCodeUtil {
    private static final String CPP_SINGLE_LINE_COMMENT_PATTERN = "//[\\s\\S]*?\n";
    private static final String CPP_MULTI_LINE_COMMENT_PATTERN = "/\\*[\\s\\S]*?\\*/";
    private static final String CPP_STRING_PATTERN = "\"[\\s\\S]*?\"";
    //private static final String CONST_KEYWORD = "const";
    private static final String INLINE_KEYWORD = "__inline";

    /**
     * 解析函数签名
     *
     * @param path      方法所在文件路径
     * @param startLine 方法起始行
     * @return 方法签名
     */
    public static String parseMethodSignature(String path, int startLine) {
        String signature = parseMethodSignature(path, startLine, "default");
        if (signature == null || signature.length() == 0)
            signature = parseMethodSignature(path, startLine, "UTF-8");
        if (signature == null || signature.length() == 0)
            signature = parseMethodSignature(path, startLine, "GB2312");
        if (signature == null || signature.length() == 0)
            signature = parseMethodSignature(path, startLine, "latin1");
       // System.out.println("sign: "+signature);
        return signature;
    }

    public static String parseMethodSignature(String path, int startLine, String charset) {

        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        try {
            //读取代码
            Scanner scanner = new Scanner(file, charset);
            List<String> lines = new ArrayList<>();
            lines.add("");
            int lineCount = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (++lineCount > startLine) {
                    break;
                }

                lines.add(line);
            }
            scanner.close();

            //以左大括号为锚点，向前扫描
            String code = String.join("\n", lines);
           // System.out.println("sign code: "+code);
            int index = code.lastIndexOf("{");
            if (code.lastIndexOf("}") > index)
                return null;
            if (index == -1) {
                return null;
            }
            char c = code.charAt(index);

            //扫描左大括号到右小括号之间是否存在const
            StringBuilder builder = new StringBuilder();
            while (c != ')') {
                index--;
                if (index < 0) {
                    return null;
                }
                c = code.charAt(index);
                if (c != ')') {
                    builder.append(c);
                }
            }
            String str;// = builder.reverse().toString().trim();
//            if (!str.isEmpty()) {
//                if (!str.equals(CONST_KEYWORD)) {
//                    return null;
//                }
//            }

            //从右小括号扫描到左小括号，提取参数列表
            builder.delete(0, builder.length()).append(')');
            int rightParenNum = 1;
            while (rightParenNum != 0) {
                index--;
                if (index < 0) {
                    return null;
                }
                c = code.charAt(index);
                if (c == '(') {
                    rightParenNum--;
                } else if (c == ')') {
                    rightParenNum++;
                }
                builder.append(c);
            }
            String params = builder.reverse().toString().trim();

            //提取方函数名
            builder.delete(0, builder.length());
            index = skipWhitespace(code, --index);
            c = code.charAt(index);
            while (!Character.isWhitespace(c)) {
                builder.append(c);
                index--;
                if (index < 0) {
                    return null;
                }
                c = code.charAt(index);
            }
            String methodName = builder.reverse().toString().trim();

            //提取返回类型
            builder.delete(0, builder.length());
            index = skipWhitespace(code, index);
            c = code.charAt(index);
            while (!Character.isWhitespace(c)) {
                builder.append(c);
                index--;
                if (index < 0) {
                    return null;
                }
                c = code.charAt(index);
            }
            str = builder.reverse().toString().trim();
            if (str.equals(INLINE_KEYWORD)) {
                builder.delete(0, builder.length());
                index = skipWhitespace(code, index);
                c = code.charAt(index);
                while (!Character.isWhitespace(c)) {
                    builder.append(c);
                    index--;
                    if (index < 0) {
                        return null;
                    }
                    c = code.charAt(index);
                }
                str = builder.reverse().toString().trim();
            }

            return str + " " + methodName + params;
        } catch (Exception e) {
            log.error("parse method signature error: " + path + " " + startLine, e);
        }
        return null;
    }

    public static String parseMethodBody(String path, int startLine, int endLine) {
        String code = parseMethodBody(path, startLine, endLine, false, "default");
        if (code == null || code.length() == 0)
            code = parseMethodBody(path, startLine, endLine, false, "UTF-8");
        if (code == null || code.length() == 0)
            code = parseMethodBody(path, startLine, endLine, false, "GB2312");
        if (code == null || code.length() == 0)
            code = parseMethodBody(path, startLine, endLine, false, "latin1");
        return code;
    }

    /**
     * 解析方法体
     *
     * @param path      方法所在文件路径
     * @param startLine 方法起始行
     * @param endLine   方法结束行
     * @return 方法体对象
     */
    public static String parseMethodBody(String path, int startLine, int endLine, boolean formatString, String charset) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            Scanner scanner = new Scanner(file, charset);
            List<String> lines = new ArrayList<>();
            lines.add("");
            int cnt = 0;
            while (scanner.hasNextLine()) {
                cnt++;
                String line = scanner.nextLine();
                if (cnt >= startLine && cnt <= endLine) {
                    lines.add(line);
                } else if (cnt > endLine) {
                    break;
                }
            }
            scanner.close();

            String code = String.join("\n", lines);
            int leftBracketIndex = code.indexOf("{");
            int rightBracketIndex = code.lastIndexOf("}");
            if (leftBracketIndex < 0 || rightBracketIndex < 0 || leftBracketIndex >= rightBracketIndex) {
                return null;
            }
            code = code.substring(leftBracketIndex, rightBracketIndex+1);
           // System.out.println("body: "+code);
            code = removeComment(code);
            if (formatString) {
                code = code.replaceAll(CPP_STRING_PATTERN, "");
                code = formatCode(code);
            }

            return code;

        } catch (IOException e) {
            log.error("parse method body error: " + path + " " + startLine, e);
        }
        return null;
    }


    public static String parseMethodBodySimple(String path, int startLine, int endLine) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            Scanner scanner = new Scanner(file);
            List<String> lines = new ArrayList<>();
            int cnt = 0;
            while (scanner.hasNextLine()) {
                cnt++;
                String line = scanner.nextLine();
                if (cnt >= startLine && cnt <= endLine) {
                    lines.add(line);
                } else if (cnt > endLine) {
                    break;
                }
            }
            scanner.close();

            return String.join("\n", lines);
        } catch (IOException e) {
            log.error("parse method body error: " + path + " " + startLine, e);
        }
        return null;
    }

    private static int skipWhitespace(String code, int index) {
        int res = index;
        char c = code.charAt(res);
        while (Character.isWhitespace(c)) {
            res--;
            if (res < 0) {
                return 0;
            }
            c = code.charAt(res);
        }
        return res;
    }

    public static String formatCode(String codeText) {
        String code;
        code = codeText.replaceAll("\\s+", " ");
        code = code.replaceAll(";", ";\n");
        code = code.replaceAll("\\{", "{\n");
        code = code.replaceAll("}", "}\n");
        return code;
    }

    public static String removeComment(String codeText) {
        String code = codeText.replaceAll(CPP_SINGLE_LINE_COMMENT_PATTERN, "");
        code = code.replaceAll(CPP_MULTI_LINE_COMMENT_PATTERN, "");
        return code;
    }

}
