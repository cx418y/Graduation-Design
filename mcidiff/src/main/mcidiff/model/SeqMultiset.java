package mcidiff.model;

import java.util.ArrayList;
import java.util.List;

public class SeqMultiset extends Multiset{
	private ArrayList<TokenSeq> sequences = new ArrayList<>();

	@Override
	public String toString(){
		return this.sequences.toString();
	}
	
	public TokenSeq findTokenSeqByCloneInstance(CloneInstance cloneInstance){
		return findTokenSeqByCloneInstance(cloneInstance.getFileName(), 
				cloneInstance.getStartLine(), cloneInstance.getEndLine());
	}
	
	public TokenSeq findTokenSeqByCloneInstance(String fileName, int startLine, int endLine){
		for(TokenSeq seq: getSequences()){
			CloneInstance ins = seq.getCloneInstance();
			if(ins.getFileName().equals(fileName) && ins.getStartLine()==startLine
					&& ins.getEndLine()==endLine){
				return seq;
			}
		}
		return null;
	}
	
	/**
	 * @return the sequences
	 */
	public ArrayList<TokenSeq> getSequences() {
		return sequences;
	}

	/**
	 * @param sequences the sequences to set
	 */
	public void setSequences(ArrayList<TokenSeq> sequences) {
		this.sequences = sequences;
	}
	
	public void addTokenSeq(TokenSeq seq){
		this.sequences.add(seq);
	}
	
	public int getSize(){
		return getSequences().size();
	}

	public boolean isAllEmpty() {
		for(TokenSeq seq: this.sequences){
			if(!seq.isEpisolonTokenSeq()){
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isGapped(){
		for(TokenSeq seq: this.sequences){
			if(seq.isEpisolonTokenSeq()){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean containsDuplicate(){
		for(int i=0; i<sequences.size(); i++){
			for(int j=i+1; j<sequences.size(); j++){
				if(sequences.get(i).equals(sequences.get(j))){
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public List<? extends DiffElement> getDiffElements() {
		return sequences;
	}
}
