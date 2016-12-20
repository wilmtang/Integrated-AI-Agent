import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class homeworkTestSuit {
	@SuppressWarnings("unused")
	public static void main (String[] args) throws Exception {
		KnowledgeBase KB = null;
		
		int passed = 0;
		int failed = 0;
		
		Integer[] arr = {
			27, 28, 29, 30, 37, 38, 39, 41,25
		};
		Set<Integer> skipped = new HashSet<> ();
		List<Integer> sl = new ArrayList<Integer>(Arrays.asList(arr));
		skipped.addAll(sl);
		
		for (int i = 1; i <= 41; ++i) {
			KB = new KnowledgeBase();
			if (skipped.contains(i) ) {
				continue;
			}
			
			System.out.print("Test case " + i + "...");

			List<String> answerList = read(KB, "testcase/input" + i + ".txt");
			List<String> outputList = readAnswer("testcase/output" + i + ".txt");
			
			if (answerList.equals(outputList)) {
				System.out.println("passed");
				passed ++;
			} else {
				System.out.println("failed");
				System.out.println("	Correct:" + outputList.toString());
				System.out.println("    Your input: " + answerList.toString());
				failed ++;
			}
		}
		
		System.out.println("----------------------------Test Finished-----------------------------");
		System.out.println(passed + "/41 passed, " + sl.size() + "skipped");
	}
	
	private static List<String> read(KnowledgeBase KB, String filepath) throws Exception {
		
		List<String> clist = new ArrayList<>();
		List<String> qlist = new ArrayList<>();
		List<String> alist = new ArrayList<>();
		
		Scanner myscanner = new Scanner (new File (filepath));
		int queryN = Integer.parseInt(myscanner.nextLine());
		for (int i = 0; i < queryN; ++i) {
			qlist.add(myscanner.nextLine());
		}
		
		int clauseN = Integer.parseInt(myscanner.nextLine());
		for (int i = 0; i < clauseN; ++i) {
			clist.add(myscanner.nextLine());
		}
		
		for (String clause : clist) {
			KB.addOneClause(clause);
		}
//		KB.print();
//		
//		KB.selfInfer();
//		
//		System.out.println("-------------------------After Self Infer---------------------------------");
//		KB.print();

		@SuppressWarnings("unused")
		int index = 0;
		for (String query : qlist) {
			//System.out.println("\nQuerying " + index++ + ": " + query);
			
			alist.add(KB.query(query)? "TRUE" : "FALSE");
		}
		myscanner.close();
		
		return alist;
	}
	
	private static List<String> readAnswer (String filepath) throws Exception {
		List<String> alist = new ArrayList<>();
		Scanner myscanner = new Scanner (new File (filepath));
		while (myscanner.hasNextLine()) {
			alist.add(myscanner.nextLine());
		}
		myscanner.close();
		return alist;	
	}
}
