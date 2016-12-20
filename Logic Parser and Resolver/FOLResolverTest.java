import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class homeworkTest {

	@Test
	public void test() throws Exception {
		Set<String> set = new HashSet<>();
		String[] arr = { "rich", "rich", "poor", "poor", "loop"};
		List<String> list = new ArrayList<String>(Arrays.asList(arr));
		set.addAll(list);
		System.out.println(set.toString());
	}
	
//	@Test
//	public void qTest1 () {
//		assertTrue(KB.query("F(Bob)"));
//	}
//	
//	@Test
//	public void qTest2 () {
//		assertTrue(KB.query("H(John)"));
//	}
//	
//	@Test
//	public void qTest3 () {
//		assertTrue(KB.query("~H(Alice)"));
//	}
//	
//	@Test
//	public void qTest4 () {
//		assertFalse(KB.query("~H(John)"));
//	}
//	
//	@Test
//	public void qTest5 () {
//		assertFalse(KB.query("G(Bob)"));
//	}
//	
//	@Test
//	public void qTest6 () {
//		assertTrue(KB.query("G(Alice)"));	
//	}
	
	public void queryTest (String query, KnowledgeBase KB) throws Exception {
		System.out.println("\nQuerying" + query + "...");
		OriginalLiteral q = new OriginalLiteral (query);
		System.out.print("Standardized arglist:");
		printList (KB.standize (q.arglist));
		System.out.print("\nVariablized arglist:");
		printList (KB.variablize(q.arglist));
		
		System.out.println("\nMatching... merge the results into one list");
	}
	
	public void printList (List<String> list) {
		System.out.print("(");
		for (String s : list) {
			System.out.print(s + ",");
		}
		System.out.print(")");
	}

}
