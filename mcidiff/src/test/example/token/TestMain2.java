package example.token;

import java.util.ArrayList;

import mcidiff.main.TokenMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.TokenMultiset;

public class TestMain2 {
	public static void main(String[] ars) {

		String path1 = "test/MemberDeclarationVisitor1.java";
		String path2 = "test/MemberDeclarationVisitor2.java";

		CloneInstance instance1 = new CloneInstance(path1, 1, 432);
		CloneInstance instance2 = new CloneInstance(path2, 1, 475);
		
//		CloneInstance instance1 = new CloneInstance(path1, 1, 103);
//		CloneInstance instance2 = new CloneInstance(path2, 1, 113);

		CloneSet set = new CloneSet("0");
		set.addInstance(instance1);
		set.addInstance(instance2);

		try {
			ArrayList<TokenMultiset> multisets = new TokenMCIDiff().diff(set, null);
			//System.out.println(multisets);
			for(TokenMultiset tokens : multisets){
				System.out.println(tokens.getTokens() +"   "+ tokens.isCommon());
			}
			for(TokenMultiset tokens : multisets){
				if(tokens.isCommon()){
					System.out.print(tokens.getTokens().get(0).getTokenName()+" ");
					if(tokens.getTokens().get(0).getTokenName().equals(";") || tokens.getTokens().get(0).getTokenName().equals("{") || tokens.getTokens().get(0).getTokenName().equals("}")){
						System.out.println();
					}
				}
				//System.out.println(tokens.getTokens() +"   "+ tokens.isCommon());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
