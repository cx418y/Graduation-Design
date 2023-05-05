package fudan.design.clone.dto;

import lombok.Data;

import java.util.List;

@Data
public class TemplateDetailsDTO {
	private String id;
	private String commonContent;
	private List<TemplateLineDIffList> diffs;

}
