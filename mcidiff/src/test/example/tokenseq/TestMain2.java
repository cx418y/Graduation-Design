package example.tokenseq;

import java.util.ArrayList;

import mcidiff.main.SeqMCIDiff;
import mcidiff.model.*;

public class TestMain2 {
	public static void main(String[] ars) {

		String path1 = "test/MemberDeclarationVisitor1.java";
		String path2 = "test/MemberDeclarationVisitor2.java";

		CloneInstance instance1 = new CloneInstance(path1, 1, 459);
		CloneInstance instance2 = new CloneInstance(path2, 1, 505);
		
//		CloneInstance instance1 = new CloneInstance(path1, 1, 103);
//		CloneInstance instance2 = new CloneInstance(path2, 1, 113);

		CloneSet set = new CloneSet("0");
		set.addInstance(instance1);
		set.addInstance(instance2);

		try {
			ArrayList<SeqMultiset> multisets = new SeqMCIDiff().diff(set, null);
//			System.out.println(multisets);
//			for(SeqMultiset seq:multisets){
//				ArrayList<TokenSeq> sequences=seq.getSequences();
//				//System.out.println("sequence");
//				for(TokenSeq tokenSeq:sequences){
//					//System.out.println("tokenseq"){
//					for(Token token :tokenSeq.getTokens() ){
//						System.out.print(token.getTokenName()+"     ");
//					}
//					System.out.println();
//				}
//				System.out.println();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
