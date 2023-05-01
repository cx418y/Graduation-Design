package fudan.design.clone.dto;

import lombok.Data;

import java.util.List;

@Data
public class TemplateDetailsDTO {
	private String id;
	private List<TemplateLineDIffList> details;

}
