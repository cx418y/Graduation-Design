package fudan.design.clone.extractor;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
@Data
public class TemplateLineSet {

	MultiSet<TemplateLine> mainLine;

	List<MultiSet<TemplateLine>> alternateLines;

	int index;

	public TemplateLineSet(MultiSet<TemplateLine> mainLine,List<MultiSet<TemplateLine>> alternateLines,int index){
		this.mainLine = mainLine;
		this.alternateLines = alternateLines;
		this.index = index;
	}

	//List<>

}
