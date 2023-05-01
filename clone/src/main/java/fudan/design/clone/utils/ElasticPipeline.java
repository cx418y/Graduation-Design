package fudan.design.clone.utils;

import fudan.design.clone.bean.TemplateList;
import fudan.design.clone.dto.Template;
import fudan.design.clone.service.ElasticsearchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ElasticPipeline {
	@Resource
	ElasticsearchService service;

	public void run() {
		List<Template> templateList =TemplateList.templateList;
		System.out.println("size:"+templateList.size());
		for(Template template:templateList){
			service.save(template);
		}
	}
}
