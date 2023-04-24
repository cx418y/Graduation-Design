package fudan.design.clone.construct;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CloneInstance {

	private String filePath;
	private int methodId;
	private int startLine;
	private int endLine;
	private String methodDetails;
	private String[] methodDetailsList;

	public CloneInstance(String[] methodLine){
		this.methodId = Integer.parseInt(methodLine[0]);
		this.filePath = methodLine[5];
		this.startLine = Integer.parseInt(methodLine[6]);
		this.endLine = Integer.parseInt(methodLine[7]);
		this.methodDetails = methodLine[13];
		this.methodDetailsList = this.methodDetails.split("\n");
	}




}
