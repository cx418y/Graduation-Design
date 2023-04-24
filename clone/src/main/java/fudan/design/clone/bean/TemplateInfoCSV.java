package fudan.design.clone.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
public class TemplateInfoCSV {
	private String templateId;

	private String methodName;

	private String templateCommon;

	private String diffDetails;

	private List<TemplateInfoCSV> templateList;

	public static String[] getCsvHeader() {
		String[] result = new String[4];
		result[0] = "templateId";
		result[1] = "methodName";
		result[2] = "templateCommon";
		result[3] = "diffDetails";
		return result;
	}







}
