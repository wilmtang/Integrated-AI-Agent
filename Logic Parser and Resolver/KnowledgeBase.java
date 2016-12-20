import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class KnowledgeBase {
	/**
	 * line 14 ---- Fields and Constructors
	 * line 111 ---- Clause (list of Literals) processing methords
	 * line 437 ---- argument (string) processing methods
	 * line 549 ---- Debugging method
	 */
	
	
	/*
	 * Fields and Constructors
	 */
	public final static boolean TRACE = false;
	public final static int CLAUSELIMIT = 100;
	
	public Map<String, Map<List<String>, List<Integer>>> Literals;
	public List<List<OriginalLiteral>> Clauses;

	KnowledgeBase () {
		this.Literals = new HashMap<>();
		this.Clauses = new ArrayList<>();
	}

	/*
	 * KB methods
	 */

	/**
	 * Query the reversed prediction to find a contradiction. If contradiction exists, return true. If all clauses inside Clauses are no longer available (empty), return false.
	 * @param prediction
	 * @param argList
	 * @return
	 * @throws Exception 
	 */
	public boolean query (String inputQuery) throws Exception {
		/*
		 * backtracking to search and combine
		 */
		//1: reverse the input Literal query. Reverse the reversed query as target merging literal. find this literal, either matching all constants or matching variables.

		/*2: traverse the initial seaching list.
		 * function queryOneLiteral (Literal v): return true or false
		 * 		 list = getClauseList (v)
		 * 		 foreach (clause : list):
		 *          if (clause.isNotValid) continue;
		 * 			newClause = resolveAndUnify (~query, clause)
		 * 			if (newClause.isEmpty) : return true; //found contradiction
		 * 			
		 * 			KB.remove(clause);
		 * 			foreach (literal : newClause):
		 * 				queryOneLiteral (literal);
		 * 		 	KB.recover(clause);
		 *		 return false;
		 * 			
		 * 
		 */

		OriginalLiteral q = new OriginalLiteral (inputQuery);
		q.reverse();
		List<OriginalLiteral> queryclause = new ArrayList<>();
		queryclause.add(q);
//		int queryindex = Clauses.size();
		addOneClause(queryclause);

		boolean ret = false;
		try {
			ret = queryOneClause (queryclause);
//			if (ret == false) {
//				selfInfer();
//				ret = queryOneClause (queryclause);
//			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		queryclause.remove(0);
		
		return ret;
	}
	
	/**
	 * Transform input FOL clause into CNF. Update Literal list and the corresponding 
	 * clause index list of each argument searching list.
	 * @param wrappedClause a raw String from input file which is an FOL clause
	 * @throws Exception 
	 */
	public void addOneClause (String wrappedClause) throws Exception {

		List<List<OriginalLiteral>> newClauses = unwrap (wrappedClause);

		for (List<OriginalLiteral> newClause : newClauses) {
			addOneClause(factor(newClause));
		}
	}
	
	/**
	 * Helper function for addOneClause. add one clause into KB
	 * @param newClause
	 * @throws Exception
	 */
	private void addOneClause (List<OriginalLiteral> newClause) throws Exception {
		int nextIndex = this.Clauses.size();
		/* find duplicate clauses in KB*/
		boolean duplicate = false;
		List<OriginalLiteral> newClauseCopy= new ArrayList<> (newClause);
		for (OriginalLiteral literal : newClauseCopy) {
			literal.removeIndex();
		}
		
		for (List<OriginalLiteral> clause : this.Clauses) {
			List<OriginalLiteral> temp = new ArrayList<>();
			
			for (OriginalLiteral literal : clause) {
				temp.add(new OriginalLiteral(literal));
			}
			
			for (OriginalLiteral literal : temp) {
				literal.removeIndex();
			}
			if (temp.equals(newClauseCopy)) {
				duplicate = true;
				break;
			}
		}
		
		if (duplicate) return;
		
		for (OriginalLiteral literal : newClause) {
			if (!Literals.containsKey(literal.prediction)) { // if literal base has not this prediction function set, build this set.
				Literals.put(literal.prediction, new HashMap<List<String>, List<Integer>>());
			}

			List<String> standardizedArgList = standize (literal.arglist);
			if (!Literals.get(literal.prediction).containsKey(standardizedArgList)) {
				List<Integer> temp = new ArrayList<Integer>();
				temp.add(nextIndex);
				Literals.get(literal.prediction).put(standardizedArgList, temp);
			} else {
				Literals.get(literal.prediction).get(standardizedArgList).add(nextIndex);
			}

			literal.addUniqueIndex(nextIndex);
		}
		
		

		this.Clauses.add(newClause);

	}
	
	/**
	 * May cause infinite loop. ABORT before a solution is found
	 * @throws Exception
	 */
	public void selfInfer () throws Exception {
		
		/* Every clause in KB resolves with the entire KB only once
		 * after converge one clause with KB, add this clause to visited list
		 */
		Set<Integer> visited = new HashSet<>();
		for (int i = 0; i < this.Clauses.size(); ++i) {
			if (this.Clauses.size() > CLAUSELIMIT) {
				break;
			}

			List<OriginalLiteral> clause = this.Clauses.get(i);
			for (int j = 0; j < clause.size(); ++j) {
				/*Resolve on this literal*/
				OriginalLiteral query = clause.get(j);
				/*Construct rest list.*/
				List<OriginalLiteral> rest = new ArrayList<>(clause);
				rest.remove(j);
				/* get literallist containing the reversed query*/
				List<Integer> list = getClauseList(query);
			
				
				for (int e : list) {
					
					if (visited.contains(e)) {
						continue;
					}
					
					List<OriginalLiteral> queryclause = this.Clauses.get(e);
					
					if (TRACE) {
						System.out.println("\n    " + e +"- Resolving on " + query.toString() + ":");
						printList(queryclause); System.out.println();
						printList(clause);System.out.println();
						List<OriginalLiteral> newClause = resolveAndUnify (query, rest, queryclause);
						System.out.println("---------------------------------------------------------");
						printList(newClause);System.out.println();
					}
					
					List<OriginalLiteral> newClause = resolveAndUnify (query, rest, queryclause);
					for (OriginalLiteral literal : newClause) {
						literal.removeIndex();
					}
					addOneClause(newClause);
				}
			}
			
			visited.add(i);
		}
	}
	
	/*
	 * Clause (list of literals) Processing Methods
	 */

	/**
	 * Parse a raw String clause into a miniKB which has all CNF'ed clauses.
	 * @param input
	 * @return
	 */
	List<List<OriginalLiteral>> unwrap (String input) {
		ExpressionNode root = new ExpressionNode (input);
		root.build();
		root.convertToCNF();

		/*
		 * All the Strings are literal 
		 * a list of clauses
		 * where each clause is a list of literals (disjointed)
		 */
		List<List<String>> cnfStringClauses = root.CNFToList();

		List<List<OriginalLiteral>> oLiterals = new ArrayList<>();
		for (List<String> clause : cnfStringClauses) {
			List<OriginalLiteral> olist = new ArrayList<>();

			for (String literal : clause) {	
				olist.add(new OriginalLiteral(literal));
			}

			oLiterals.add(olist);
		}


		return oLiterals;
	}


	/**
	 * set the clause at input index to be empty to prevent duplicated visiting.
	 * @param index 
	 * @return the removed list 
	 */
	List<OriginalLiteral> removeOneClause (int index) {
		List<OriginalLiteral> temp = this.Clauses.get(index);
		this.Clauses.remove(index);
		this.Clauses.add(index, new ArrayList<OriginalLiteral>());
		return temp;
	}
	

	/**
	 * queryclause is the new clause after each resolution, waiting to be merged with clauses from KB,  containing one counter part
	 * of one part of queryclause.
	 * @param queryclause
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private boolean queryOneClause (List<OriginalLiteral> queryclause) throws Exception {


		/*
		 * trace resolution process
		 */
		if(TRACE) {
			System.out.print ("\n    Querying ");
			printList(queryclause);
		}


		for (int i = 0; i < queryclause.size(); ++i) {
			OriginalLiteral query = queryclause.get(i);
			List<OriginalLiteral> rest = new ArrayList<> (queryclause);
			rest.remove(i);

			/*
			 * clause list is the possible list which contains a counter part of the literal we choose from the input queryclause.
			 */
			List<Integer> clauselist = getClauseList (query);

			/*
			 * trace resolution process
			 */
			if (TRACE && (!clauselist.isEmpty())) {
				System.out.println("Found" +clauselist.toString());
			}

			if (clauselist.size() == 0) continue; 
//			???? if use this, once any literal cannot be resolved, the entire clause would be judged as "cannot resolve" wihch is wrong when resolvable literal is in the last of the list

			for (int e : clauselist) {
				List<OriginalLiteral> clause = this.Clauses.get(e);
				if (clause.isEmpty()) {
					//System.out.println("\n    Fetching clause" + e + "but size is" + clause.size());
					//the clause is used
					continue;
				}
				List<OriginalLiteral> newClause = resolveAndUnify (query, rest, clause); // need to add the rest of the list

				/*
				 * trace resolution process
				 */
				if (TRACE) {
					System.out.println("\n    " + e +"- Resolving on " + query.toString() + ":");
					printList(queryclause); System.out.println();
					printList(clause);System.out.println();
					System.out.println("---------------------------------------------------------");
					printList(newClause);System.out.println();
				}

				/*
				 * if contradiction found, return true;
				 */
				if (newClause.isEmpty()) {
					if (TRACE) {
						System.out.println("Contradiction found, return true");
					}

					return true;
				}

				/*
				 * backtracking
				 */
				if (clause.size() > 1) {
					this.removeOneClause(e);
				}
	//			if (Clauses.contains(newClause)) continue;

				if (queryOneClause (newClause)) {
					this.Clauses.remove(e);
					this.Clauses.add(e, clause);
					return true;
				}

				this.Clauses.remove(e);
				this.Clauses.add(e, clause);
			}
		}

		return false;
	}

	/**
	 * find clauses that contains the reversed query
	 * @param query
	 * @return
	 * @throws Exception 
	 */
	private List<Integer> getClauseList (OriginalLiteral query) throws Exception {
		List<Integer> ret = new ArrayList<>();
		Set<Integer> set = new HashSet<>();

		//if (!isAllConstant(query.arglist)) return ret;

		StringBuilder targetPredictionBuilder = new StringBuilder(query.prediction);
		if (targetPredictionBuilder.charAt(0) == '~') {
			targetPredictionBuilder.deleteCharAt(0);
		} else {
			targetPredictionBuilder.insert(0, '~');
		}
		String targetPrediction = targetPredictionBuilder.toString();

		if (this.Literals.containsKey(targetPrediction)) {
			//			List<String> standardlist = standize (query.arglist);
			//			List<String> variablizedlist = variablize (query.arglist);
			//			if (this.Literals.get(targetPrediction).containsKey(standardlist)) {
			//				set.addAll(this.Literals.get(targetPrediction).get(standardlist));
			//			}
			//			
			//			if (this.Literals.get(targetPrediction).containsKey(variablizedlist)) {
			//				set.addAll (this.Literals.get(targetPrediction).get(variablizedlist));
			//			}
			//			
			//			for (List<String> arglist : this.Literals.get(targetPrediction).keySet()) {
			//				if (variablize (arglist).equals(variablizedlist)) {
			//					set.addAll(this.Literals.get(targetPrediction).get(arglist));
			//				}
			//			}

			for (List<String> kblist : this.Literals.get(targetPrediction).keySet()) {
				if (isReplacible (kblist, query.arglist)) {
					ret.addAll(this.Literals.get(targetPrediction).get(kblist));
				}
			}
		}

		ret.addAll(set);
		return ret;
	}

	private List<OriginalLiteral> resolveAndUnify (OriginalLiteral query,List<OriginalLiteral> rest,  List<OriginalLiteral> clause) throws Exception {
		List<OriginalLiteral> ret = new ArrayList<>();
		// assume clause only contains one. might need factoring and duplicate check 
		/*
		 * get the reversed prediction name
		 */
		StringBuilder targetPredictionBuilder = new StringBuilder(query.prediction);
		if (targetPredictionBuilder.charAt(0) == '~') {
			targetPredictionBuilder.deleteCharAt(0);
		} else {
			targetPredictionBuilder.insert(0, '~');
		}
		String targetPrediction = targetPredictionBuilder.toString();

		/*
		 * Resolve: search in the list from KB. remove the target found
		 */
		OriginalLiteral target = null;
		int targetIndex = 0;
		for (int i = 0; i < clause.size(); ++i) {
			OriginalLiteral literal = clause.get(i);
			if (literal.prediction.equals(targetPrediction)) {
				if (!isReplacible (query.arglist, literal.arglist)) {
					continue;
				}
				target = literal;
				targetIndex = i; //DO NOT clause.remove (target)!!! Clause will be damaged!
				break;
			}
		}
		if (target == null) {
			//System.err.print("\nResolve Exception: target literal prediction name not found: " + targetPrediction + " from ");
			//printList(clause);
			//throw new Exception ();
			return ret;
		}

		/*
		 * Unify. Build a table with target and query. Then for each literal remaining in the clause, replace all the variables.
		 */
		Map<String, String> unificationTable = new HashMap<>();
		//assume no duplicate variables.
		for (int i = 0; i < target.arglist.size(); ++i) {
			/*
			 * case 1: both constants
			 * case 2: query constant, target variable.
			 * case 3: target constant, query variable
			 * case4 : target variable, query variable
			 */
			String argQ = query.arglist.get(i);
			String argT = target.arglist.get(i);

			//case 1
			if (isConstant(argQ) && isConstant(argT)) {
				if (!argQ.equals(argT)) {
					print();
					throw new Exception ("Unify Matching Exception: Constants don't match: " + argQ + " and " + argT);
				}
			}

			//case 2 : set argQ (v) as key. argT (c) as value
			if (isVariable(argQ) && isConstant(argT)) {
				unificationTable.put(argQ, argT);
				continue;
			}

			//case 3: set argT (v) as key, argQ (c) as value
			if (isConstant(argQ) && isVariable(argT)) {
				unificationTable.put(argT, argQ);
				continue;
			}

			//case 4: set argQ(v) as key, argT (v) as value
			if (isVariable(argQ) && isVariable(argT)) {
				unificationTable.put(argQ, argT);
				continue;
			}
		}

		/*
		 * the result list contains : clause without target + rest, where rest is queryclause without query.
		 */

		/*
		 * update clause without target
		 */
		updatedAdd (clause, ret, targetIndex, unificationTable);
		/*
		 * update rest
		 */
		updatedAdd (rest, ret, -1, unificationTable); //DO NOT use targetIndex: may lose data in 'rest' list!

		return factor(ret);
	}

	/**
	 * Update the arglist for all literals inside clause list according to unificationTable, skipping index at targetIndex.
	 * @param clause
	 * @param ret
	 * @param targetIndex
	 * @param unificationTable
	 */
	void updatedAdd (List<OriginalLiteral> clause, List<OriginalLiteral> ret, int targetIndex, Map<String, String> unificationTable) {
		for (int i = 0; i < clause.size(); ++i) {
			if (i == targetIndex) continue; //"resolution": Not adding target into the new list
			/*
			 * create a new literal with args replaced according to the table. 
			 * then add it to the ret list.
			 */
			OriginalLiteral literal = clause.get(i); //one literal inside the resolved clause, with original variables waiting to be replaced
			/*
			 * same prediction name
			 */
			String p = literal.prediction;

			/*
			 * unify the arguments.
			 */
			List<String> newArglist = new ArrayList<>();
			for (String arg : literal.arglist) {
				//if the table has not the arg from the literal inside clause, then this literal contains variables which the target has not. we dont unify
				//in this case.
				if (!unificationTable.containsKey(arg)) {
					newArglist.add(arg);
				} else {
					newArglist.add(unificationTable.get(arg));
				}
			}

			/*
			 * Construct a new literal with prediction name and unified argument list.
			 */
			ret.add(new OriginalLiteral (p, newArglist));
		}
	}

	/**
	 * Remove duplicated literals on full-identity
	 * @param clause
	 * @return
	 * @throws Exception 
	 */
	List<OriginalLiteral> factor (List<OriginalLiteral> clause) throws Exception {
		List<OriginalLiteral> ret = new ArrayList<>();
		/*
		 * 1. same prediction, same arglist (standardized)
		 */
		for (OriginalLiteral literal : clause) {
			//equals() was modified.
//			boolean dup = false;
//			
//			List<String> standardL = standize (literal.arglist);
//			for (OriginalLiteral added : ret) {			
//				if (literal.prediction.equals(added.prediction)) {				
//					List<String> standardA = standize (added.arglist);
//					if (standardL.equals(standardA)) {
//						dup = true;
//						break;
//					}
//				}
//			}
//			
//			if (!dup) {
//				ret.add(literal);
//			}
			if (!ret.contains(literal)) {
				ret.add(literal);
			}
		}
		return ret;

	}


	/*
	 * Argument list processing methods
	 */
	
	/**
	 * replace the variables in the literal to universal standard (a,b,c,d).
	 * Constants won't he replaced
	 * @param list
	 * @return
	 * @throws Exception 
	 */
	List<String> standize (List<String> list) throws Exception {
		Map<Character, Integer> indexTable = new HashMap<>();
		List<String> standardList = new ArrayList<>();
		for (String arg : list) {
			//debug
			if (arg == null) {
				throw new Exception ("Null Pointer inside arglist");
			}

			//end of debug	
			if (isVariable (arg)) { // BUG: NOT if (arg.length() == 1)
				if (!indexTable.containsKey(arg.charAt(0))) {
					indexTable.put(arg.charAt(0), indexTable.size());
				}
			}
		}

		for (String arg : list) {
			if (isVariable (arg)) { //// BUG: NOT if (arg.length() == 1)
				char var = (char) (indexTable.get(arg.charAt(0)) + 'a');
				standardList.add("" + var);
			} else {
				standardList.add(arg);
			}
		}

		return standardList;
	}

	List<String> variablize (List<String> list) {
		List<String> variablelist = new ArrayList<>();
		for (int i = 0; i < list.size(); ++i) {
			char arg = (char) ('a' + i);
			variablelist.add("" + arg);
		}
		return variablelist;
	}

	/**
	 * Find if s is a constant. Constant starts with upper case.
	 * @param s
	 * @return
	 */
	boolean isConstant (String s) {
		if (s == null || s.length() < 1) {
			return false;
		}

		return Character.isUpperCase(s.charAt(0));
	}

	/**
	 * Find if s is a variable. Variable starts with lower case.
	 * @param s
	 * @return
	 */
	boolean isVariable (String s) {
		if (s == null || s.length() < 1) {
			return false;
		}

		return Character.isLowerCase(s.charAt(0));
	}

	/**
	 * Find if the argument list is a full-variable list. if so, may be applicable to fast index searching. (First element of each list in Literal)
	 * @param argList
	 * @return
	 */
	boolean isAllVariable (List<String> argList) {
		for (String s : argList) {
			if (isConstant(s)) {
				return false;
			}
		}

		return true;
	}

	boolean isAllConstant (List<String> argList) {
		for (String s : argList) {
			if (isVariable (s)) {
				return false;
			}
		}

		return true;
	}

	boolean isReplacible (List<String> kblist, List<String> querylist) throws Exception {
		
		if(kblist.size() != querylist.size()) {
			//throw new Exception ("Not unifiable: "+ kblist.toString() + " and " + querylist.toString());
			return false;
		}
		
		for (int i = 0; i < kblist.size(); ++i) {
			if (isConstant(kblist.get(i)) && isConstant (querylist.get(i))){
				if (!kblist.get(i).equals(querylist.get(i))) {
					return false;
				}
			}
		}

		return true;
	}
	
	/*
	 * Debugging methods
	 */

	/**
	 * Print the KB in a human friendly pattern
	 */
	void print() {
		System.out.println("Printing Clause Library...");
		int index = 0;
		for (List<OriginalLiteral> clause : Clauses) {
			System.out.print(index++ + ": ");

			for (OriginalLiteral literal : clause) {
				System.out.print(literal.prediction + "(");
				for (String arg : literal.arglist) {
					System.out.print(arg + ",");
				}
				System.out.print(") ");
			}
			System.out.println();
		}

		System.out.println("\nPrinting literal indice...");
		for (String p : Literals.keySet()) {
			System.out.print(p + ": ");
			for (List<String> arglist : Literals.get(p).keySet()) {
				System.out.print("(");
				for (String s : arglist) {
					System.out.print(s + ",");
				}
				System.out.print(") : (");
				for (int e : Literals.get(p).get(arglist)) {
					System.out.print(e + ", ");
				}
				System.out.print(")");
			}

			System.out.println();
		}
	}

	public void printList (List<OriginalLiteral> list) {
		System.out.print("(");
		for (OriginalLiteral s : list) {
			System.out.print(s.toString() + " | ");
		}
		System.out.print(")");
	}

}