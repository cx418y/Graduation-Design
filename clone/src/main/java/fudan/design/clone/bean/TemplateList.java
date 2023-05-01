package fudan.design.clone.bean;

import fudan.design.clone.dto.Template;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Data
@Slf4j
public class TemplateList {

	public static List<Template> templateList;

	public static void addTemplate(Template template){
		if(templateList == null){
			templateList = new ArrayList<>();
		}
		templateList.add(template);
	}


}
