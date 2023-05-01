package fudan.design.clone.dto;

import fudan.design.clone.extractor.MultiSet;
import fudan.design.clone.extractor.TemplateLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
//import org.springframework.data.elasticsearch.annotations.DocumentId；


import java.lang.reflect.Type;
import java.util.List;

@Data
@Document(indexName = "wiremock")
public class Template {

	@Id
	private String id;

	@Field
	private String methodName;

	@Field(type= FieldType.Object)
	private List<TemplateLineItem> templateCommon;


	@Field(type= FieldType.Object)
	private List<TemplateLineDIffList> templateDetails;

}

