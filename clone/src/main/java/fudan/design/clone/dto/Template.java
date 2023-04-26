package fudan.design.clone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "template")
public class Template {
	@Id
	@Field
	private int id;
	@Field
	private String methodName;

	@Field
	private String templateCommon;
	@Field
	private String templateDetails;






}

