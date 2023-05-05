package fudan.design.clone.controller;


import fudan.design.clone.dto.RelatedTemplatesDTO;
import fudan.design.clone.dto.Template;
import fudan.design.clone.dto.TemplateDetailsDTO;
import fudan.design.clone.dto.TemplateLineDIffList;
import fudan.design.clone.service.ElasticsearchService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CloneController {
	@Resource
	private ElasticsearchService elasticsearchService;

	@GetMapping("/getDtails")
	public TemplateDetailsDTO getDetails(@RequestParam String id) {
		return elasticsearchService.findById(id);
	}
	@GetMapping("/getCloneTemplate")
	public List<RelatedTemplatesDTO> searchData(@RequestParam String key){
		return elasticsearchService.searchTemplatesByName(key);
	}

	@PostMapping("/delete")
	public void deleteData(@RequestParam String key){
		 elasticsearchService.delete(key);
	}

}
