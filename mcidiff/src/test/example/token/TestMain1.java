package example.token;

import java.util.ArrayList;

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
		
		CloneInstance instance1 = new CloneInstance(path1, 18, 23);
		CloneInstance instance2 = new CloneInstance(path2, 18, 23);
		CloneInstance instance3 = new CloneInstance(path3, 18, 23);
		
		CloneSet set = new CloneSet("0");
		set.addInstance(instance1);
		set.addInstance(instance2);
		set.addInstance(instance3);
		
		try {
			ArrayList<TokenMultiset> multisets = new TokenMCIDiff().diff(set, null);
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
				//System.out.println(tokens.getTokens().get(0).getNode());
			}


			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
