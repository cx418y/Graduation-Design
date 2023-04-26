package fudan.design.clone.controller;


import fudan.design.clone.dto.Template;
import fudan.design.clone.service.ElasticsearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CloneController {
	@Resource
	private ElasticsearchService elasticsearchService;

	@GetMapping("/getDtails")
	public Template getDetails(@RequestParam int id) {
		return elasticsearchService.findById(id);
	}
	@GetMapping("/getCloneModule")
	public List<Template> searchData(@RequestParam String key){
		return elasticsearchService.searchTemplatesByName(key);
	}
}
