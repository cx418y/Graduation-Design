package fudan.design.clone.extractor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class MultiSet {

    private boolean[] occurrence;
    private TemplateLine token;

    private boolean isVisited = false;

    private int finalPosition;

    public MultiSet(TemplateLine token, int index, int size) {
        occurrence = new boolean[size];
        occurrence[index] = true;
        this.token = token;
    }

    public void append(int index, boolean occur) {
        occurrence[index] = occur;
    }

    public double getRate() {
        int count = 0, total = occurrence.length;
        for (boolean b : occurrence) {
            if (b)
                count++;
        }
        return count * 1.0 / total;
    }

    public int getOccurrenceCount(){
        int count = 0;
        for (boolean b : occurrence) {
            if (b)
                count++;
        }
        return count;
    }

    public String toString() {
        return "token:" + token + ", occurrence:" + Arrays.toString(occurrence);
    }

    public static  List<MultiSet> generateMultiset(List<List<TemplateLine>> candidates) {
        if (candidates == null || candidates.size() == 0) {
            return null;
        }
        List<MultiSet> res = new ArrayList<>();
        for (TemplateLine tk : candidates.get(0)) {
            res.add(new MultiSet(tk, 0, candidates.size()));
        }
        int total = candidates.size();
        for (int i = 1; i < total; i++) {
            computeLcs(res, candidates.get(i), i, total);
        }

        return res;
    }

    private static <T> void computeLcs(List<MultiSet> rawTemplate, List<TemplateLine> candidate, int index, int size) {
        List<TemplateLine> ts = extractSequenceFromTemplate(rawTemplate);
        int m = ts.size(), n = candidate.size();
        int[][] table = new int[m + 1][n + 1];
        for (int i = 0; i < m + 1; i++)
            table[i][0] = 0;
        for (int j = 0; j < n + 1; j++)
            table[0][j] = 0;

        // Fill in the table using dynamic programming
        for (int i = 1; i < m + 1; i++) {
            for (int j = 1; j < n + 1; j++) {
                if (ts.get(i - 1).equals(candidate.get(j - 1)))
                    table[i][j] = table[i - 1][j - 1] + 1;
                else
                    table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]);
            }
        }

        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (ts.get(i - 1).equals(candidate.get(j - 1))) {
                rawTemplate.get(i - 1).append(index, true);
                i--;
                j--;
            } else if (table[i - 1][j] > table[i][j - 1]) {
                rawTemplate.get(i - 1).append(index, false);
                i--;
            } else {
                rawTemplate.add(i, new MultiSet(candidate.get(j - 1), index, size));
                j--;
            }
        }

//        System.out.println("tempalte");
//        for(MultiSet token:rawTemplate){
//            System.out.println(token.toString());
//        }
    }

    private static  List<TemplateLine> extractSequenceFromTemplate(List<MultiSet> rawTemplate) {
        List<TemplateLine> res = new ArrayList<>();
        for (MultiSet ms : rawTemplate) {
            res.add(ms.getToken());
        }
        return res;
    }
}
