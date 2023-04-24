package fudan.design.clone.utils;

public class DiffUtil {


	public static double compareStringSimilarity(String str1, String str2){

		if(str1 == null || str2 == null){
			return 0;
		}

		String[] words1 = splitCamelString(str1);
		String[] words2 = splitCamelString(str2);

		Object[] commonWords = generateCommonNodeList(words1, words2, new StringComparator(false));
		double sim = 2d*commonWords.length/(words1.length+words2.length);

		return sim;
	}

	public static String[] splitCamelString(String s) {
		return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|(?<!^)(?=(\\*)+)");
	}

	public static Object[] generateCommonNodeList(Object[] nodeList1, Object[] nodeList2, StringComparator comparator) {
		int[][] commonLengthTable = buildLeveshteinTable(nodeList1, nodeList2, comparator);

		int commonLength = commonLengthTable[nodeList1.length][nodeList2.length];
		Object[] commonList = new Object[commonLength];

		for (int k = commonLength - 1, i = nodeList1.length, j = nodeList2.length; (i > 0 && j > 0);) {
			if (comparator.isEquals(nodeList1[i - 1], nodeList2[j - 1])) {
				commonList[k] = nodeList1[i - 1];
				k--;
				i--;
				j--;
			} else {
				if (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1]){
					i--;
				}
				else{
					j--;
				}
			}
		}

		return commonList;
	}

	public static int[][] buildLeveshteinTable(Object[] nodeList1, Object[] nodeList2, StringComparator comparator){
		int[][] commonLengthTable = new int[nodeList1.length + 1][nodeList2.length + 1];
		for (int i = 0; i < nodeList1.length + 1; i++)
			commonLengthTable[i][0] = 0;
		for (int j = 0; j < nodeList2.length + 1; j++)
			commonLengthTable[0][j] = 0;

		for (int i = 1; i < nodeList1.length + 1; i++){
			for (int j = 1; j < nodeList2.length + 1; j++) {
				if (comparator.isEquals(nodeList1[i - 1], nodeList2[j - 1])){
					commonLengthTable[i][j] = commonLengthTable[i - 1][j - 1] + 1;
				}
				else {
					commonLengthTable[i][j] = (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1]) ?
							commonLengthTable[i - 1][j] : commonLengthTable[i][j - 1];
				}
			}
		}

		return commonLengthTable;
	}
}
