package fudan.design.clone.extractor;

import  fudan.design.clone.bean.MethodInfo;
import fudan.design.clone.utils.code.CppCodeUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemplateInfo {

    List<MethodInfo> methods;
    List<MultiSet> rawTemplate;

    List<TemplateLineSet> finalTemplate;

    int templateId;

    // methods是一个克隆组的method list
    public TemplateInfo(List<MethodInfo> methods,int templateId) {
        this.templateId = templateId;
        this.methods = methods;
        List<List<TemplateLine>> candidates = new ArrayList<>();
        for (MethodInfo methodInfo : methods) {
            String code = methodInfo.getBody();
            code = CppCodeUtil.removeComment(code);
            //code = CppCodeUtil.formatCode(code);
            candidates.add(JavaTemplateExtractor.extract(code));
        }
        this.rawTemplate = MultiSet.generateMultiset(candidates);
        System.out.println("rawraw");
        for(MultiSet set : rawTemplate){
            System.out.println(""+set);
        }

        this.finalTemplate = JavaTemplateExtractor.extractTemplate(rawTemplate);

        TemplateLineSet firstSet = finalTemplate.get(0);
       // System.out.println("mainLIne: "+finalTemplate.get(0)+"");
        if(firstSet.getMainLine() == null){
            System.out.println("null main");
            firstSet.setMainLine(firstSet.getAlternateLines().get(0));
            firstSet.getAlternateLines().remove(0);
        }
        for(TemplateLineSet set:finalTemplate){
            System.out.print(set.getIndex()+": ");
            if(set.getMainLine() != null){
                System.out.println(set.getMainLine().toString());
            }else{
                System.out.println("null");
            }
            System.out.println("alter: "+set.getAlternateLines());
        }
//        for(TemplateLineSet set:finalTemplate){
//            // System.out.print(set.getIndex()+": ");
//            if(set.getMainLine() != null){
//                String origin = set.getMainLine().getToken().getOriginLineString();
//                if(set.getAlternateLines().size()==0){
//                   System.out.println(origin);
//                }else{
//                    System.out.print("[*"+set.getIndex()+"*] ");
//                    System.out.println("[*"+origin+"*]");
//                }
//               // System.out.println(set.getMainLine().getToken().getOriginLineString());
//            }else{
//                System.out.println("[*"+set.getIndex()+"*] ");
//            }
//        }
//        for(TemplateLineSet set:finalTemplate){
//            // System.out.print(set.getIndex()+": ");
//            System.out.println("[*"+set.getIndex()+"*] :");
//            for(MultiSet line:set.getAlternateLines()){
//                System.out.println(line.getToken().getOriginLineString());
//            }
//        }



    }



    public int size() {
        return methods.size();
    }
}
