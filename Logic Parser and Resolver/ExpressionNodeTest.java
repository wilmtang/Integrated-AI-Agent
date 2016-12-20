import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ExpressionNodeTest {
	
	@Test
	public void testCNFConversion() {
		ExpressionNode test = new ExpressionNode ("((A(x) & B(x)) | C(x))");
		
		test.build();
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		assertEquals("Preorder Traversal", "A(x)&B(x)|C(x)", display.toString());
		
		test.convertToCNF();
		display = new StringBuilder ();
		helper(test, display);
		assertEquals("CNF Conversion basic", "A(x)|C(x)&B(x)|C(x)",display.toString());
		
		List<List<String>> clauses = test.CNFToList();
		for (List<String> l : clauses) {
			for (String s : l) {
				System.out.print(" "+ s);
			}
			System.out.println("CNF list test 1 finished");
		}
		
	}
	
	@Test
	public void testCNFConversion2() {
		ExpressionNode test = new ExpressionNode ("((A(x) & B(x)) | (C(x) & D(x)))");
		
		test.build();
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		assertEquals("Preorder Traversal", "A(x)&B(x)|C(x)&D(x)", display.toString());
		
		test.convertToCNF();
		display = new StringBuilder ();
		helper(test, display);
		assertEquals("CNF Conversion basic", "A(x)|C(x)&A(x)|D(x)&B(x)|C(x)&B(x)|D(x)",display.toString());
		
		List<List<String>> clauses = test.CNFToList();
		for (List<String> l : clauses) {
			for (String s : l) {
				System.out.print(" "+ s);
			}
			System.out.println("CNF test 2 finished");
		}
		
	}
	
	@Test
	public void testCNFConversion3() {
		ExpressionNode test = new ExpressionNode ("(A(x) | (B(x) | (C(x) & D(x))))");
		
		test.build();
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		assertEquals("Preorder Traversal", "A(x)|B(x)|C(x)&D(x)", display.toString());
		
		test.convertToCNF();
		display = new StringBuilder ();
		helper(test, display);
		assertEquals("CNF Conversion basic", "A(x)|B(x)|C(x)&A(x)|B(x)|D(x)",display.toString());
		
		List<List<String>> clauses = test.CNFToList();
		for (List<String> l : clauses) {
			for (String s : l) {
				System.out.print(" "+ s);
			}
			System.out.println("CNF test3 finished");
		}
		
	}
	
	@Test
	public void testCNFConversion4() {
		ExpressionNode test = new ExpressionNode ("(((A(x,y) & B(y,z)) & C(x,z)) | ((D(x,x,x,x,x) & E(x,y,z,z,z)) & F(x,y,z,x)))");
		
		test.build();
		StringBuilder display = new StringBuilder ();
		helper(test, display);
//		assertEquals("Preorder Traversal", "A(x)|B(x)|C(x)&D(x)", display.toString());
		
		test.convertToCNF();
		display = new StringBuilder ();
		helper(test, display);
		/*
		 * assertion below is not valid: the result is from function itself, not calculated by hand. However, it can be verified on 
		 * http://www.wolframalpha.com/input/?i=(a+%26%26+b+%26%26+c)+%7C%7C+(d+%26%26+e+%26%26+f)
		 */
//		assertEquals("CNF Conversion basic", "A(x)|D(x)&A(x)|E(x)&A(x)|F(x)&B(x)|D(x)&B(x)|E(x)&B(x)|F(x)&C(x)|D(x)&C(x)|E(x)&C(x)|F(x)",display.toString());
		
		List<List<String>> clauses = test.CNFToList();
		for (List<String> l : clauses) {
			for (String s : l) {
				System.out.print(" "+ s);
			}
			System.out.println("CNF test 4 finished");
		}
		
	}

	@Test
	public void testExpressionNode() {
		ExpressionNode test = new ExpressionNode ("((A(x) & B(x))  =>  C(x))");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		

		assertEquals("Preorder Traversal", "~A(x)|~B(x)|C(x)", display.toString());
		
	}
	
	@Test
	public void testExpressionNode1() {
		ExpressionNode test = new ExpressionNode ("(A(x)  =>  C(x))");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "~A(x)|C(x)", display.toString());
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode2() {
		ExpressionNode test = new ExpressionNode ("(~ A(x))");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "~A(x)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode_LongPredictionNames() {
		ExpressionNode test = new ExpressionNode ("(~ Asprin(x))");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "~Asprin(x)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode_Complicated() {
		ExpressionNode test = new ExpressionNode ("( Asprin(x) | ( Cat(x) | (Dante(x) & Bob(x))))");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "Asprin(x)|Cat(x)|Dante(x)&Bob(x)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode_Complicated2() {
		ExpressionNode test = new ExpressionNode ("(( Asprin(x) | ( Cat(x) | (Dante(x) & Bob(x)))) => Ohio(x))");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "~Asprin(x)&~Cat(x)&~Dante(x)|~Bob(x)|Ohio(x)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode_ActualTest0() {
		ExpressionNode test = new ExpressionNode ("(~(~(~(~A(x))))) ");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "A(x)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode_ActualTest1() {
		ExpressionNode test = new ExpressionNode ("(D(x,y) => (~H(y))) ");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "~D(x,y)|~H(y)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode_ActualTest2() {
		ExpressionNode test = new ExpressionNode ("((B(x,y) & C(x,y)) => A(x)) ");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "~B(x,y)|~C(x,y)|A(x)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	@Test
	public void testExpressionNode_ActualTest3() {
		ExpressionNode test = new ExpressionNode ("((~(Parent(x,y) & Ancestor(y,z))) | Ancestor(x,z))");
		test.build();
		
		StringBuilder display = new StringBuilder ();
		helper(test, display);
		
		assertEquals("Testing UnitSplit", "~Parent(x,y)|~Ancestor(y,z)|Ancestor(x,z)", display.toString() );
		//assertEquals("Preorder Traversal", display.toString(), "~A(x)|~B(x)|C(x)");
		
	}
	
	void helper (ExpressionNode input, StringBuilder temp) {
		if (input != null) {
			helper (input.left,temp);
			temp.append(input.expression.toString());
			helper (input.right, temp);
		}
	}


}
