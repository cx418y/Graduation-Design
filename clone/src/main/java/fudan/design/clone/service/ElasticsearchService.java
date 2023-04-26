package fudan.design.clone.service;

import fudan.design.clone.dto.Template;
import jakarta.annotation.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.MultiGetItem;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {

	@Resource
	ElasticsearchOperations elasticsearchOperations;

	public List<Template> searchTemplatesByName(String name) {
		Criteria criteria = new Criteria("methodName").contains(name);
		Query query = new CriteriaQuery(criteria);
		List<MultiGetItem<Template>> result = elasticsearchOperations.multiGet(query, Template.class);
		List<Template> templates= new ArrayList<>();
		for(MultiGetItem<Template> item : result){
			templates.add(item.getItem());
		}
		return templates;

	}

	public Template findById(int id) {
		Template template = elasticsearchOperations.get(id+"", Template.class);
		return template;
	}

	public void save(Template template){
		elasticsearchOperations.save(template);
	}

}
