package fudan.design.clone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TemplateLineItem {
	private String content;

	private int count;

	private double frequency;
}
