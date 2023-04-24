package fudan.design.clone.extractor;

import fudan.design.clone.extractor.cpp.CPP14Lexer;
import fudan.design.clone.utils.code.CppCodeUtil;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class CppTemplateExtractor {

    public static List<TemplateLine> extract(String code) {
        String formattedCode = CppCodeUtil.removeComment(code);
        formattedCode = CppCodeUtil.formatCode(formattedCode);
        CPP14Lexer lexer = new CPP14Lexer(CharStreams.fromString(formattedCode));
        List<? extends Token> tokens = lexer.getAllTokens();
        List<Token> tmp = new ArrayList<>();
        String[] lines = code.split("\n");
        int lineNum = 1;
        List<TemplateLine> templateLineList = new ArrayList<>();
        int lineOffset = 0;

        for (Token tk : tokens) {
            if (!isLiteral(tk) && !isIdentifier(tk))
                continue;
            System.out.println("type:" + CPP14Lexer.VOCABULARY.getSymbolicName(tk.getType()) +
                    "@pos" + tk.getLine() + "(" + tk.getStartIndex() + "," +
                    tk.getStopIndex() + ")" + ":" + tk.getText());
            int tkLineNum = tk.getLine();
            if (tkLineNum == lineNum) {
                tmp.add(tk);
                continue;
            }
            do {
                String[] ids = new String[tmp.size()];
                StringBuilder line = new StringBuilder(lines[lineNum - 1]);
                int intraOffset = 0;
                for (int i = 0; i < tmp.size(); i++) {
                    Token tkk = tmp.get(i);
                    int len = tkk.getStopIndex() - tkk.getStartIndex() + 1;
                    int start = tkk.getStartIndex() - lineOffset - intraOffset;
                    String mark = "<LTR>";
                    if (isIdentifier(tkk))
                        mark = "<ID>";
                    line.delete(start, start + len).insert(start, mark);
                    intraOffset += len - mark.length();
                    ids[i] = tkk.getText();
                }
                templateLineList.add(new TemplateLine(line.toString(), ids));
                lineOffset += lines[lineNum - 1].length() + 1;
                lineNum++;
                tmp.clear();
            } while (tkLineNum > lineNum);
            tmp.add(tk);
        }

        return templateLineList;
    }

    private static boolean isLiteral(Token tk) {
        return CPP14Lexer.VOCABULARY.getSymbolicName(tk.getType()).endsWith("Literal");
    }

    private static boolean isIdentifier(Token tk) {
        return tk.getType() == CPP14Lexer.Identifier;
    }
}
