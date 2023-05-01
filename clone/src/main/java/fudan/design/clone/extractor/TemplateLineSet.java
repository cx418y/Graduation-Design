package fudan.design.clone.extractor;

import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
@Data
public class TemplateLineSet {

	MultiSet mainLine;

	List<MultiSet> alternateLines;

	int index;

	public TemplateLineSet(MultiSet mainLine,List<MultiSet> alternateLines,int index){
		this.mainLine = mainLine;
		this.alternateLines = alternateLines;
		this.index = index;
	}

	public void addAlternative(MultiSet multiSet){
		if(alternateLines == null) {
			alternateLines = new ArrayList<>();
			alternateLines.add(multiSet);
			return;
		}
		for(int i = 0; i < alternateLines.size();i++) {
			if(alternateLines.get(i).getRate() < multiSet.getRate()){
				alternateLines.add(i,multiSet);
				return;
			}
		}
		alternateLines.add(multiSet);
	}
	//List<>

}
