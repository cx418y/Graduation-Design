package fudan.design.clone.utils.sagaUtils;

//import com.company.lexer.CPPLexer;
//import com.company.lexer.JavaLexer;
//import com.company.lexer.Lexer;
//import com.company.lexer2.CPPLexer2;
//import com.company.lexer2.JavaLexer2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenUtil {

    private static byte ID;
    private static byte KEYWORD_LOWER;
    private static byte KEYWORD_UPPER;

    //不openStringHash,不tokenize

    public static int judgeCloneType(byte[] left, byte[] right) {
        if (left.length != right.length)
            return 3;

        int len = left.length;
        for (int i = 0; i < len; i++) {
            if (left[i] != right[i])
                return 3;
        }
        return 1;
    }

    public static int judgeCloneType(byte[] left, byte[] right, Language lang) {
        if (lang == null)
            return judgeCloneType(left, right);

        initConstants(lang);
        if (left.length != right.length)
            return 3;

        boolean allSame = true;
        int len = left.length;

        for (int i = 0; i < len; i++) {
            byte l = left[i];
            byte r = right[i];
            if (l != r) {
                if (tokenType(l) * tokenType(r) > 0)
                    return 3;
                else
                    allSame = false;
            }
        }

        return allSame ? 1 : 2;

    }

    // 0-id, 1-keyword, 2-other
    private static int tokenType(byte token) {
        if (token == ID || token <= -3 || token >= 125)
            return 0;
        if (token >= KEYWORD_LOWER && token <= KEYWORD_UPPER)
            return 1;
        return 2;
    }

    private static void initConstants(Language lang) {
        if (lang.equals(Language.JAVA)) {
            ID = 0;
            KEYWORD_LOWER = 1;
            KEYWORD_UPPER = 52;
        } else if (lang.equals(Language.CPP)) {
            ID = 90;
            KEYWORD_LOWER = 0;
            KEYWORD_UPPER = 94;
        }
    }

//    public static String restoreSnippetByTokenIndexes(
//            String methodBody, long start, long end,
//            boolean tokenize, Language language
//    ) {
//        Lexer lexer = initLexer(tokenize, language);
//        if (lexer == null)
//            return null;
//        String result = "";
//
//        return null;
//    }

//    private static Lexer initLexer(boolean tokenize, Language language) {
//        try {
//            if (language.equals(Language.JAVA)) {
//                if (tokenize)
//                    return new JavaLexer("");
//                else
//                    return new JavaLexer2("");
//            } else if (language.equals(Language.CPP)) {
//                if (tokenize)
//                    return new CPPLexer("");
//                else
//                    return new CPPLexer2("");
//            }
//        } catch (IOException e) {
//            log.error("init lexer failed!\nlanguage:" +
//                    language + ";tokenize:" + tokenize, e);
//        }
//        return null;
//    }

}
