import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FOLResolver {
	public static void main (String[] args) throws Exception {
		KnowledgeBase KB = new KnowledgeBase();
		List<String> answerList = read(KB, "input.txt");
		write (answerList, "output.txt");
	}
	
	private static List<String> read(KnowledgeBase KB, String filepath) throws Exception {
		
		List<String> clist = new ArrayList<>();
		List<String> qlist = new ArrayList<>();
		List<String> alist = new ArrayList<>();
		
		Scanner myscanner = new Scanner (new File (filepath));
		int queryN = Integer.parseInt(myscanner.nextLine());
		for (int i = 0; i < queryN; ++i) {
			qlist.add(myscanner.nextLine());
			System.out.println();
		}
		
		int clauseN = Integer.parseInt(myscanner.nextLine());
		for (int i = 0; i < clauseN; ++i) {
			clist.add(myscanner.nextLine());
		}
		
		for (String clause : clist) {
			KB.addOneClause(clause);
		}
//		KB.print();
//		KB.selfInfer();
//		System.out.println("--------------------After Self Infer-----------------------");
//		KB.print();
		@SuppressWarnings("unused")
		int index = 0;
		for (String query : qlist) {
			//System.out.println("\nQuerying " + index++ + ": " + query);
			//KB.print();
			alist.add(KB.query(query)? "TRUE" : "FALSE");
		}
		myscanner.close();
		
		return alist;
	}
	
	private static void write (List<String> aList, String filepath) throws Exception {
		File output = new File(filepath);
		output.createNewFile();
		PrintWriter writer = new PrintWriter(output);
		
		for (String answer : aList) {
			writer.println(answer);
		}
		
		writer.close();
		
	}
}
