import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ExpressionNode {
	public boolean isOperand;
	public boolean isOperator;
	public boolean isLiteral;
	
	public StringBuilder expression;
	
	ExpressionNode left;
	ExpressionNode right;
	ExpressionNode parent;
	
	public ExpressionNode (boolean isOperand, boolean isOperator, boolean isLiteral, StringBuilder exp, ExpressionNode parent) {
		this.isOperand = isOperand;
		this.isOperator = isOperator;
		this.isLiteral = isLiteral;
		this.expression = exp;
		this.parent = parent;
	}
	
	public ExpressionNode (String initialString) {
		this.expression = new StringBuilder (initialString.replaceAll("\\s+",""));
		if (initialString.charAt(0) == '(') {
			this.isOperand = true;
		} else if (Character.isUpperCase(initialString.charAt(0))) {
			this.isLiteral = true;
		//} else throw new Exception ("Invalid input String: Expected Uppercase or '(' but found " + initialString);
		} else {
			System.out.println("Initialization failed: Expected Uppercase or '(' but found " + initialString);
		}
	}
	
	/**
	 * build a subtree from an Expression Node
	 * @return
	 */
	public boolean build () {
		if (this.isOperator || this.isLiteral) {
			return true;
		}
		
		if (this.expression.charAt(0) == '~') {
			try {
				negaUnitSplit (this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				unitSplit (this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.print(e.getMessage());
				e.printStackTrace();
				return false;
			}
		} 
		
		if (this.left != null) {
			if (!this.left.build()) return false;
		}
		
		if (this.right != null) {
			if (!this.right.build()) return false;
		}
		
		return true;
	}
	
	public void convertToCNF() {
		convertToCNF(this);
	}
	
	public List<List<String>> CNFToList() {
		List<List<String>> ret = null;
		try {
			ret = CNFToList(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * Utilization function: Build a subtree based on the input starting with a negation
	 * @param input
	 * @throws Exception
	 */
	private static void negaUnitSplit (ExpressionNode input) throws Exception {
		if (input.expression.charAt(2) != '~') {
			int cursor = 2;
			boolean leftIsLiteral = false;
			boolean leftIsOperand = false;
			boolean rightIsLiteral = false;
			boolean rightIsOperand = false;
			
			StringBuilder leftBlock;
			if (input.expression.charAt(cursor) == '(') {
				leftIsOperand = true;
				leftBlock = findNextBlock (input.expression, cursor);
			} else if (Character.isUpperCase(input.expression.charAt(cursor))) {
				leftIsLiteral = true;
				leftBlock = findNextLiteral (input.expression, cursor);
			} else throw new Exception ("Split Error: expecting ( or upper Alphabetics but found " + input.expression.charAt(cursor));
			cursor += leftBlock.length();
			
			StringBuilder operator = findOperator (input.expression, cursor);
			cursor += operator.length();
			
			StringBuilder rightBlock;
			if (input.expression.charAt(cursor) == '(') {
				rightIsOperand = true;
				rightBlock = findNextBlock (input.expression, cursor);
			} else if (Character.isUpperCase(input.expression.charAt(cursor))) {
				rightIsLiteral = true;
				rightBlock = findNextLiteral (input.expression, cursor);
			} else throw new Exception ("Split Error: expecting ( or upper Alphabetics but found " + input.expression.charAt(cursor));
			
			if (operator.charAt(0) == '=') {
				if (leftBlock.charAt(0) != '~') {
					leftBlock.insert(0, '~');
				} else {
					leftBlock.deleteCharAt(0);
				}
				operator = new StringBuilder("|");
			}
			
			/*
			 * negative transform
			 */
			operator = operator.charAt(0) == '|'? new StringBuilder("&") : new StringBuilder("|");
			
			if (leftBlock.charAt(0) != '~') {
				leftBlock.insert(0, '~');
			} else {
				leftBlock.deleteCharAt(0);
			}
			
			if (rightBlock.charAt(0) != '~') {
				rightBlock.insert(0, '~');
			} else {
				rightBlock.deleteCharAt(0);
			}
			
			/*
			 * set the node and its children
			 */
			input.isOperator = true;
			input.isOperand = false;
			input.isLiteral = false;
			input.expression = operator;
			
			input.left = new ExpressionNode (leftIsOperand,!(leftIsOperand||leftIsLiteral) , leftIsLiteral, leftBlock, input);
			input.right = new ExpressionNode (rightIsOperand,!(leftIsOperand||leftIsLiteral) ,rightIsLiteral, rightBlock, input);
		} else if (input.expression.charAt(2) == '~') {
			if (input.expression.charAt(3) == '(') {
				StringBuilder temp = findNextBlock (input.expression, 3);
				input.expression = temp;
				unitSplit (input);
			} else if (Character.isUpperCase(input.expression.charAt(3))) {
				StringBuilder temp = findNextLiteral (input.expression, 3);
				input.isLiteral = true;
				input.isOperand = false;
				input.isOperator = false;
				input.expression = temp;
			}
		} else throw new Exception ("Split Error: invalid starting char");
	}
	
	/**
	 * Utilization function:
	 * build a subtree based on the node input
	 * @param input
	 * @throws Exception
	 */
	private static void unitSplit (ExpressionNode input) throws Exception {
		
		
		if (input.expression.charAt(1) != '~') {
			/*first case : start with an embraced block
			 *case 1: block op block
			 *case 2: block op literal
			 */
			int cursor = 1;
			boolean rightIsLiteral = false;
			boolean rightIsOperand = false;
			boolean leftIsLiteral = false;
			boolean leftIsOperand = false;
			
			StringBuilder leftBlock;
			if (input.expression.charAt(cursor) == '(') {
				leftIsOperand = true;
				leftBlock = findNextBlock (input.expression, cursor);
			} else if (Character.isUpperCase(input.expression.charAt(cursor))) {
				leftIsLiteral = true;
				leftBlock = findNextLiteral (input.expression, cursor);
			} else throw new Exception ("Split Error:  invalid starting char while inside splitting. Expected ( or Uppercase alphabet but found " + input.expression.toString());
			cursor += leftBlock.length();
			
			StringBuilder operator = findOperator (input.expression, cursor);
			cursor += operator.length();
			
			StringBuilder rightBlock;
			if (input.expression.charAt(cursor) == '(') {
				rightIsOperand = true;
				rightBlock = findNextBlock (input.expression, cursor);
			} else if (Character.isUpperCase(input.expression.charAt(cursor))) {
				rightIsLiteral = true;
				rightBlock = findNextLiteral (input.expression, cursor);
			} else throw new Exception ("Split Error:  invalid starting char while splitting :" + "\"" + input.expression + "\"");
			
			if (operator.charAt(0) == '=') {
				leftBlock.insert(0, '~'); //may cause weird bug. leftBlock might be ~(xxxx) or ~A(x). I assume not. 
				operator = new StringBuilder("|");
			}
			
			input.isOperator = true;
			input.isOperand = false;
			input.isLiteral = false;
			input.expression = operator;
			
			input.left = new ExpressionNode (leftIsOperand,!(leftIsLiteral || leftIsOperand ) ,leftIsLiteral, leftBlock, input);
			input.right = new ExpressionNode (rightIsOperand, !(leftIsLiteral || leftIsOperand) ,rightIsLiteral, rightBlock, input);
			
			
			
		} else if (input.expression.charAt(1) == '~') {
			/*
			 * third case: start with negation
			 * case1: negation block
			 * case2: negation literal
			 */
			if (input.expression.charAt(2) == '(') {
				input.expression = new StringBuilder (input.expression.substring(1, input.expression.length() - 1));
				negaUnitSplit (input);
			} else if (Character.isUpperCase(input.expression.charAt(2))) {
				StringBuilder temp = findNextLiteral (input.expression, 2);
				input.expression = temp.insert(0, '~');
				input.isLiteral = true;
				input.isOperand = false;
				input.isOperator = false;
			}
			
			
		} else throw new Exception ("Split Error: invalid starting char");
	}
	
	/**
	 * Utilization function: transform the FOL to Conjunction Normal Form
	 * @param input
	 */
	private void convertToCNF (ExpressionNode input) {
		Queue<ExpressionNode> checkQ = new LinkedList<>();
		checkQ.offer(input);
		
		while (!checkQ.isEmpty()) {
			ExpressionNode top = checkQ.poll();
			// only reconstruct when | has a child or children as "&"
			if (top.isOperator) {
				if (top.expression.toString().equals("|")) {
					if (top.left != null && top.left.expression.toString().equals("&")) {
						ExpressionNode newLeft = new ExpressionNode (false, true, false, new StringBuilder("|"), top);
						ExpressionNode newRight = new ExpressionNode (false, true, false, new StringBuilder("|"), top);
						newLeft.right = newRight.right = top.right;
						newLeft.left = top.left.left;
						newRight.left = top.left.right;
						
						top.expression = new StringBuilder ("&");
						top.left = newLeft;
						top.right = newRight;
						
						if (top.parent != null) {
							checkQ.offer(top.parent);
						} else {
							checkQ.offer(top);
						}
						continue; // !! Don't check top.right which is already modified.
					}
					
					if (top.right != null && top.right.expression.toString().equals("&")) {
						ExpressionNode newLeft = new ExpressionNode (false, true, false, new StringBuilder("|"), top);
						ExpressionNode newRight = new ExpressionNode (false, true, false, new StringBuilder("|"), top);
						newLeft.left = newRight.left = top.left;
						newLeft.right = top.right.left;
						newRight.right = top.right.right;
						
						top.expression = new StringBuilder("&");
						top.left = newLeft;
						top.right = newRight;
						
						if (top.parent != null) {
							checkQ.offer(top.parent);
						} else {
							checkQ.offer(top);
						}
						continue;
					}
					
					// if | has no children as & , offer its children
				
						checkQ.offer(top.left);
						checkQ.offer(top.right);
					
				} else {
					/*
					 * if top operator is &, enqueue its children to check further. NOT NULL 
					 */
					if (top.left != null) {
						checkQ.offer(top.left);
					}
					
					if (top.right != null) {
						checkQ.offer(top.right);
					}
				}
			}
			//if top is not an operator, it wont have any children. no need to enqueue. just check next item in Q
		}
	}
	
	/**
	 * Utilization function: input node is the root of the whole generated logic expression tree.
	 * this root is already negation distributed, cnf converted. Now need to separate the clauses between
	 * '&' to get a pure disjoint sentence as element of Knowledge Base.
	 * @param input
	 * @return a small KB where each List<String> is the separated pure "|" sentences. In string form. Might need to be
	 * converted into IndexedLiteral object.
	 * @throws Exception exceptions are threw if an invalid char is found.
	 */
	private List<List<String>> CNFToList (ExpressionNode input) throws Exception {
		List<List<String>> ret = new ArrayList<>();
		CNFToList_Helper (input, ret);
		return ret;
	}
	
	/**
	 * Preorder traversal helper for CNFToList
	 * @param root
	 * @param ret
	 * @throws Exception
	 */
	private void CNFToList_Helper (ExpressionNode root, List<List<String>> ret) throws Exception {
		/*
		 * root is '&' : go check left and right.
		 * root is '|' : call nodeToList to build a list of string from this subtree. All literals on this subtree are disjointed.
		 * root is Literal: a single Literal as a clause with no disjointed literals. build a new list of string. 
		 * 					add this literal to list. add the new generated list to the result list.
		 */
		if (root.isOperator) {
			if (root.expression.toString().equals("&")) {
				CNFToList_Helper (root.left, ret);
				CNFToList_Helper (root.right, ret);
			} else {
				if (!root.expression.toString().equals("|")) {
					throw new Exception ("CNF Helper Error : operator not valid. Expecting | but found " + root.expression.toString());
				}
				ret.add(nodeToList (root));
			}
		} else {
			if (root.isOperand) {
				throw new Exception ("CNF Helper Error : expecting Literal but found" + root.expression.toString());
			}
			
			if (!root.isLiteral) {
				throw new Exception ("CNF Helper Error: not an Literal");
			}
			
			List<String> temp = new ArrayList<>();
			temp.add(root.expression.toString());
			ret.add(temp);
 		}
	}
	/**
	 * Convert this root and its subtree into a list containing all the literals in this scope
	 * @param root :the root holding one CNF'ed clause. must be "|" or a literal
	 * @param ret :the clause list to add into
	 * @throws Exception 
	 */
	private List<String> nodeToList (ExpressionNode root) throws Exception {
		List<String> temp = new ArrayList<>();
		nodeToListHelper (root, temp);
		return temp;
	}
	
	/**
	 * Preorder helper for nodeToList
	 * @param root
	 * @param temp
	 * @throws Exception
	 */
	private void nodeToListHelper  (ExpressionNode root, List<String> temp) throws Exception {
		if (root.expression.toString().equals("|")) {
			nodeToListHelper (root.left, temp);
			nodeToListHelper (root.right, temp);
		} else {
			if (!root.isLiteral) {
				throw new Exception ("nodeToList Helper Error : expecting a literal but found " + root.expression.toString());
			}
			
			temp.add(root.expression.toString());
		}
	}
	
	/* PRIVATE METHODS */
	
	/**
	 * find next embraced block
	 * @param input
	 * @param start
	 * @return the block found in a StringBuilder
	 * @throws Exception 
	 */
	
	private static StringBuilder findNextBlock (StringBuilder input, int start) throws Exception {
		StringBuilder ret = null;
		/*
		 * CODE
		 */
		int leftCount = 1;
		int idx = start + 1;
		while (leftCount > 0 && idx < input.length()) {
			if (input.charAt(idx) == '(') {
				leftCount ++;
			} else if (input.charAt(idx) == ')') {
				leftCount --;
			}
			
			idx++;
		}
		
		if (leftCount != 0) {
			throw new Exception ("Invalid input: embraces not match");
		}
		
		ret = new StringBuilder (input.substring(start, idx));
		
		return ret;
	}
	
	/**
	 * find next literal
	 * @param input
	 * @param start
	 * @return
	 * @throws Exception
	 */
	private static StringBuilder findNextLiteral (StringBuilder input, int start) throws Exception {
		StringBuilder ret = null;
		/*
		 * CODE
		 */
		int idx = start;
		
		while (input.charAt(idx++) != ')') {
			if (idx > input.length()) {
				throw new Exception ("Invalid Literal");
			}
		}
		
		ret = new StringBuilder (input.substring(start, idx));
		
		return ret;
	}
	
	/**
	 * find the operator
	 * @param input
	 * @param start
	 * @return
	 * @throws Exception
	 */
	private static StringBuilder findOperator (StringBuilder input, int start) throws Exception {

		
		StringBuilder ret = null;
		/*
		 * CODE
		 */
		if (input.charAt(start) == '|') {
			ret = new StringBuilder("|");
		} else if (input.charAt(start) == '&') {
			ret = new StringBuilder("&");
		} else if (input.charAt(start) == '=') {
			ret = new StringBuilder ("=>");
		} else {
			throw new Exception ("Invalid Operator: found " + input.charAt(start) + "at index " + start);
		}
		
		return ret;
	}

}
