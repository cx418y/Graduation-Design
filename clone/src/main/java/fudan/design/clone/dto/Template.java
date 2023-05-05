package fudan.design.clone.dto;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
//import org.springframework.data.elasticsearch.annotations.DocumentId；


import java.util.List;

@Data
@Document(indexName = "8w")
public class Template {

	@Id
	private String id;

	@Field
	private String methodName;

	@Field
	private String commonString;

	@Field(type= FieldType.Object)
	private List<TemplateLineItem> templateCommon;

	@Field(type= FieldType.Object)
	private List<TemplateLineDIffList> templateDiffs;

}

