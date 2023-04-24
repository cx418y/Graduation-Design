package fudan.design.clone.extractor;

import fudan.design.clone.utils.DiffUtil;
import lombok.Data;
import lombok.NonNull;

import fudan.design.clone.common.TemplateConstants;

@Data
public class TemplateLine {

    @NonNull
    private String formattedLine;
    @NonNull
    private String[] ids;


    public boolean strictEquals(TemplateLine that) {
        if (that.ids.length != ids.length)
            return false;
        if (!formattedLine.equals(that.formattedLine))
            return false;
        for (int i = 0; i < ids.length; i++) {
            if (!ids[i].equals(that.ids[i]))
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TemplateLine){
            String thisLine = formattedLine.replaceAll("\\s+", "");
            String thatLine  = ((TemplateLine) obj).formattedLine.replaceAll("\\s+", "");
            return thisLine.equals(thatLine);
        }
        return super.equals(obj);
    }

    public boolean canCompare(TemplateLine line){
        String thisLine = formattedLine.replaceAll("\\s+", "");
        String thatLine  = line.formattedLine.replaceAll("\\s+", "");
        Double similarity = DiffUtil.compareStringSimilarity(thisLine,thatLine);
        return similarity >= TemplateConstants.MIN_CAN_COMPARE_SIM;
    }

    public String getOriginLineString(){
        StringBuilder line = new StringBuilder(this.formattedLine.replaceAll("<LTR>","<ID>"));
        int index = 0;
        for(int i = 0; i < ids.length; i++){
            int idIndex = line.indexOf("<ID>",index);
            if(idIndex != -1){
                line.delete(idIndex,idIndex+4).insert(idIndex,ids[i]);
            }
        }
        return line.toString();
    }

//    public double simDegree(TemplateLine that){
//
//    }

}
