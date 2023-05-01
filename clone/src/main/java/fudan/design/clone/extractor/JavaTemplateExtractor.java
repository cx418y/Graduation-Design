package fudan.design.clone.extractor;

//import fudan.design.clone.extractor.cpp.CPP14Lexer;
import fudan.design.clone.common.TemplateConstants;
import fudan.design.clone.extractor.java.JavaLexer;
import fudan.design.clone.utils.code.CppCodeUtil;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class JavaTemplateExtractor {

	public static List<TemplateLine> extract(String code) {

		String formattedCode = CppCodeUtil.removeComment(code);
		formattedCode = CppCodeUtil.formatCode(formattedCode);

		JavaLexer lexer = new JavaLexer(CharStreams.fromString(formattedCode));
		List<? extends Token> tokens = lexer.getAllTokens();
		List<Token> tmp = new ArrayList<>();
		String[] lines = formattedCode.split("\n");
		int lineNum = 1;
		List<TemplateLine> templateLineList = new ArrayList<>();
		int lineOffset = 0;

		for (Token tk : tokens) {
//			System.out.println("type:" + JavaLexer.VOCABULARY.getSymbolicName(tk.getType()) +
//					"@pos" + tk.getLine() + "(" + tk.getStartIndex() + "," +
//					tk.getStopIndex() + ")" + ":" + tk.getText());
			int tkLineNum = tk.getLine();
			if(tkLineNum == lineNum) {
				if (isLiteral(tk) || isIdentifier(tk)) {
					tmp.add(tk);
				}
				if(tk!=tokens.get(tokens.size()-1)){
					continue;
				}
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
			if (isLiteral(tk) || isIdentifier(tk)) {
				tmp.add(tk);
			}
		}

		System.out.println(formattedCode);
		for(TemplateLine line : templateLineList){
			System.out.println(line.getFormattedLine());
		}
		return templateLineList;
	}



	private static boolean isLiteral(Token tk) {
		//return CPP14Lexer.VOCABULARY.getSymbolicName(tk.getType()).endsWith("Literal");
		return JavaLexer.VOCABULARY.getSymbolicName(tk.getType()).endsWith("_LITERAL");
	}

	private static boolean isIdentifier(Token tk) {
//		return tk.getType() == CPP14Lexer.Identifier;
		return tk.getType() == JavaLexer.IDENTIFIER && !tk.getText().equals("String");
	}

	// 提取通用模板
	public static  List<TemplateLineSet> extractTemplate(List<MultiSet> rawTemplate){
		int methodNum = rawTemplate.get(0).getOccurrence().length;
		List<TemplateLineSet> res = new ArrayList<>();

		int beginIndex = findNextNotCommonLine(rawTemplate,0,res);
		if(beginIndex==-1){
			return res;
		}
		MultiSet curLine = rawTemplate.get(beginIndex);
		boolean[] curOccurrence = new boolean[methodNum];
		int occurrenceCount = curLine.getOccurrenceCount();
		for(int i = 0; i < curOccurrence.length;i++){
			curOccurrence[i] = curLine.getOccurrence()[i];
		}

		for(int k = beginIndex+1; k < rawTemplate.size();k++){
			outerLoop:
			for (int i = k; i < rawTemplate.size(); i++) {
				MultiSet line = rawTemplate.get(i);
				if (line.isVisited() || (!curLine.getToken().canCompare(line.getToken()))) {
					continue;
				}

				for (int j = 0; j < curOccurrence.length; j++) {
					if (curOccurrence[j] && line.getOccurrence()[j]) {
						continue outerLoop;
					}
				}

				line.setVisited(true);
				for (int j = 0; j < curOccurrence.length; j++) {
					if (line.getOccurrence()[j]) {
						curOccurrence[j] = true;
						occurrenceCount++;
					}
				}
				if (line.getRate() >= TemplateConstants.MIN_MAIN_LINE_RATE && res.get(res.size() - 1).getMainLine() == null) {
					res.get(res.size() - 1).setMainLine(line);
				} else {
					res.get(res.size() - 1).addAlternative(line);
				}
				if (occurrenceCount == methodNum) {
					break;
				}
			}
			beginIndex = findNextNotCommonLine(rawTemplate, beginIndex + 1, res);
			if (beginIndex == -1) {
				return res;
			}
			curLine = rawTemplate.get(beginIndex);
			k = beginIndex - 1;
		}

		// 设置第一行方法声明一定有mainLine
		TemplateLineSet firstSet = res.get(0);
		if(firstSet.getMainLine() == null){
			firstSet.setMainLine(firstSet.getAlternateLines().get(0));
			firstSet.getAlternateLines().remove(0);
		}

		return res;
	}

	public static int findNextNotCommonLine(List<MultiSet> rawTemplate,int index,List<TemplateLineSet> res){
		for(;index<rawTemplate.size();index++){
			MultiSet line = rawTemplate.get(index);
			if(line.isVisited()){
				continue;
			}
			line.setVisited(true);
			if(line.getRate() ==1){
				res.add(new TemplateLineSet(line,new ArrayList<>(),res.size()));
				continue;
			}
			if(line.getRate()>= TemplateConstants.MIN_MAIN_LINE_RATE){
				res.add(new TemplateLineSet(line,new ArrayList<>(),res.size()));
				return index;
			}else{
				List<MultiSet> alternateLines = new ArrayList<>();
				alternateLines.add(line);
				res.add(new TemplateLineSet(null,alternateLines,res.size()));
				return index;
			}
		}
		return -1;
	}

}
