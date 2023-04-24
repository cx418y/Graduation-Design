package example.token;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;

import mcidiff.main.TokenMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.Token;
import mcidiff.model.TokenMultiset;

public class TestMain1 {
	
	public static void main(String[] ars){
		
		String path1 = "test/test1.java";
		String path2 = "test/test2.java";
		String path3 = "test/test3.java";
		System.out.println();
		float b;
		CloneInstance instance1 = new CloneInstance(path1, 42, 48);
		CloneInstance instance2 = new CloneInstance(path2, 34, 40);
		//CloneInstance instance3 = new CloneInstance(path3, 26, 32);
		
		CloneSet set = new CloneSet("0");
		set.addInstance(instance1);
		set.addInstance(instance2);
		//set.addInstance(instance3);
		
		try {
			ArrayList<TokenMultiset> multisets = new TokenMCIDiff().diff(set, null);
			HashMap<TextAttribute, Object> hm = new HashMap<TextAttribute, Object>();
			hm.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON); // 定义是否有下划线
			hm.put(TextAttribute.SIZE, 12); // 定义字号
			hm.put(TextAttribute.FAMILY, "Simsun"); // 定义字体名
			Font font = new Font(hm);
			fo
			System.out.println(font);

			//System.out.println("multisets: "+multisets);
//			for(TokenMultiset tokens : multisets){
//				for(Token token :tokens.getTokens() ){
//					System.out.println(token.getNode()+"6666666");
//				}
//				//System.out.println(tokens.getTokens().get(0).getNode() +"   "+ tokens.isCommon());
//				//System.out.println(tokens.getTokens().get);
//				System.out.println();
//			}
			//StringBuilder blank = new StringBuilder("");
			//System.out.println(multisets.get(0).getTokens().get(0).getNode());
			for(TokenMultiset tokens : multisets){
				//if(tokens.isCommon())
//					System.out.print(tokens.getTokens().get(0).getTokenName()+" ");
//					if(tokens.getTokens().get(0).getTokenName().equals("}")){
//						System.out.print(blank);
//						System.out.print(tokens.getTokens().get(0).getTokenName()+" ");
//						blank.delete(blank.length()-1, blank.length()-1);
//					}
//					if(tokens.getTokens().get(0).getTokenName().equals("{")){
//						blank.append("	");
//					}
//					if(tokens.getTokens().get(0).getTokenName().equals(";") || tokens.getTokens().get(0).getTokenName().equals("{") || tokens.getTokens().get(0).getTokenName().equals("}")){
//						System.out.println();
//						System.out.print(blank);
//					}
//
//				}
//				System.out.println(tokens.getTokens().get(0).getTokenName());
//				System.out.println(tokens.getTokens().get(0).getNode().getClass());
//				System.out.println(tokens.getTokens().get(0).getNode().getStartPosition());
//				System.out.println(tokens.getTokens().get(0).getNode().getLength());
//
//				System.out.println(tokens.getTokens().get(0).getStartPosition());
//				System.out.println(tokens.getTokens().get(0).getEndPosition());
//				System.out.println();

			}


			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
