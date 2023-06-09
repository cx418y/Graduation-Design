package fudan.design.clone.service;

import fudan.design.clone.dto.*;
import jakarta.annotation.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticsearchService {

	@Resource
	ElasticsearchOperations elasticsearchOperations;

	public List<RelatedTemplatesDTO> searchTemplatesByName(String name) {
		//elasticsearchOperations.indexOps("test").delete();
		Criteria criteria = new Criteria("commonString").contains(name);
		Query query = new CriteriaQuery(criteria);

		//List<MultiGetItem<Template>> result = elasticsearchOperations.multiGet(query, Template.class);
//		List<Template> templates= new ArrayList<>();
//		for(MultiGetItem<Template> item : result){
//			templates.add(item.getItem());
//		}
		SearchHits<Template> searchHits = elasticsearchOperations.search(query, Template.class);
		List<Template> templates = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
		List<RelatedTemplatesDTO> result = new ArrayList<>();

		for(Template template :templates){
//			List<TemplateLineItem> templateCommon = template.getTemplateCommon();
//			StringBuilder commonSB = new StringBuilder();
//			int missCount = 0;
//			for(int i = 0;i < templateCommon.size();i++){
//				TemplateLineItem item = templateCommon.get(i);
//				if(item!=null){
//					if(item.getFrequency() == 1) {
//						commonSB.append(item.getContent()).append("\n");
//					}else{
//						commonSB.append(item.getContent()).append("[*").append(++missCount).append("*] ").append("\n");
//					}
//				}else{
//					commonSB.append("[*").append(++missCount).append("*] ").append("\n");
//				}
//			}
			//String commonString = extractCommonString(template.getTemplateCommon());
			RelatedTemplatesDTO item = new RelatedTemplatesDTO(template.getId(),template.getCommonString());
			result.add(item);
		}
		return result;
	}

//	public String extractCommonString(List<TemplateLineItem> templateCommon){
//		//List<TemplateLineItem> templateCommon = template.getTemplateCommon();
//		StringBuilder commonSB = new StringBuilder();
//		int missCount = 0;
//		for(int i = 0;i < templateCommon.size();i++){
//			TemplateLineItem item = templateCommon.get(i);
//			if(item!=null){
//				if(item.getFrequency() == 1) {
//					commonSB.append(item.getContent()).append("\n");
//				}else{
//					commonSB.append(item.getContent()).append("[*").append(++missCount).append("*] ").append("\n");
//				}
//			}else{
//				commonSB.append("[*").append(++missCount).append("*] ").append("\n");
//			}
//		}
//		return commonSB.toString();
//	}

	public TemplateDetailsDTO findById(String id) {
		Template template = elasticsearchOperations.get(id, Template.class);
		TemplateDetailsDTO details = new TemplateDetailsDTO();
		details.setId(template.getId());
		//String commonString = extractCommonString(template.getTemplateCommon());
//		StringBuilder commonSB = new StringBuilder();
//		int missCount = 0;
//		List<TemplateLineItem> templateCommon = template.getTemplateCommon();
//		for(int i = 0;i < templateCommon.size();i++){
//			TemplateLineItem item = templateCommon.get(i);
//			if(item!=null){
//				if(item.getFrequency() == 1) {
//					commonSB.append(item.getContent()).append("\n");
//				}else{
//					commonSB.append(item.getContent()).append("[*").append(++missCount).append("*] ").append("\n");
//				}
//			}else{
//				commonSB.append("[*").append(++missCount).append("*] ").append("\n");
//			}
//		}
		details.setCommonContent(template.getCommonString());
		details.setDiffs(template.getTemplateDiffs());
		return details;
		//return template;
	}

	public void save(Template template){
		elasticsearchOperations.save(template);
		//System.out.println(template.getId());
		List<Template> templates = getAll();
		System.out.println("after insert,size is: "+templates.size());
	}

	public List<Template> getAll() {
		SearchHits<Template> searchHits = elasticsearchOperations.search(new CriteriaQuery(new Criteria()), Template.class, IndexCoordinates.of("test"));
		List<Template> templates = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
		return templates;
	}

	public void delete(String id){
		elasticsearchOperations.delete(id,Template.class);
	}

}
