package fudan.design.clone.dto;

import lombok.Data;

import java.util.List;

@Data
public class TemplateLineDIffList {
	private int diffId;
	private List<TemplateLineItem> items;

}
