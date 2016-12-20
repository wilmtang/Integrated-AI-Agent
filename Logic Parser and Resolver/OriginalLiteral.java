import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OriginalLiteral {
	String prediction;
	List<String> arglist;
	int index;
	
	OriginalLiteral (String literal) {
		List<String> arglist = null;
		String p = null;
		
		literal = literal.replaceAll("\\s+", "");
		
		int cursor = 0;
		while (literal.charAt(cursor) != '(') {
			cursor++;
		}
		p = literal.substring(0, cursor);
		String[] rawarglist = literal.substring(cursor + 1, literal.length()-1).split("\\,");
		arglist = new ArrayList<>(Arrays.asList(rawarglist));
		
		this.prediction = p;
		this.arglist = arglist;
	}
	
	OriginalLiteral (String p, List<String> list) {
		this.prediction = p;
		this.arglist = list;
	}
	
	OriginalLiteral (OriginalLiteral input) {
		this.prediction = new String (input.prediction);
		this.arglist = new ArrayList<> (input.arglist);
	}
	
	public void addUniqueIndex (int index) {
		this.index = index;
		for (int i = 0; i < arglist.size(); ++i) {
			String arg = arglist.get(i);
			if (Character.isLowerCase(arg.charAt(0))) {
				StringBuilder temp = new StringBuilder(arg);
				temp.append(index);
				
				arglist.remove(i);
				arglist.add(i, temp.toString());
				
				//System.err.println("Modified arg: " + temp.toString());
			}
		}
	}
	
	public void removeIndex () {
		for (int i = 0; i < arglist.size(); ++i) {
			String arg = arglist.get(i);
			if (Character.isLowerCase(arg.charAt(0))) {
				String temp = arg.replaceAll("\\d+", "");
				
				arglist.remove(i);
				arglist.add(i, temp);
			}
		}
	}
	
	/**
	 * WARNING: May damage  query data. Make sure you want to change the query itself rather than generating reversed prediction name.
	 * Use StringBuilder to obtain reversed query. 
	 * Reverse the Literal
	 * @return true if successfully modified. false otherwise
	 */
	boolean reverse () {
		boolean ret = false;
		StringBuilder temp = new StringBuilder (this.prediction);
		if (temp.charAt(0) == '~') {
			temp.deleteCharAt(0);
			ret = true;
		} else {
			if (Character.isUpperCase(temp.charAt(0))) {
				temp.insert(0, '~');
				ret = true;
			} else {
				ret = false;
			}
		}
		
		this.prediction = temp.toString();
		return ret;
		
	}
	
	public String toString() {
		StringBuilder temp = new StringBuilder();
		temp.append(prediction);
		temp.append("(");
		for (String s : arglist) {
			temp.append("," + s);
		}
		temp.append(")");
		return temp.toString();
	}
	
	@Override
	public boolean equals (Object l2) {
		OriginalLiteral o2 = (OriginalLiteral) l2;
		List<String> standardlist = standize (this.arglist);
		return this.prediction.equals(o2.prediction) && standardlist.equals(standize(o2.arglist));
	}
	
	private List<String> standize (List<String> list)  {
		Map<Character, Integer> indexTable = new HashMap<>();
		List<String> standardList = new ArrayList<>();
		for (String arg : list) {
			//debug
			if (arg == null) {
				//throw new Exception ("");
				System.err.println("Null Pointer inside arglist");
				return null;
			}

			//end of debug	
			if (arg.length() > 0 && Character.isLowerCase(arg.charAt(0))) { // BUG: NOT if (arg.length() == 1)
				if (!indexTable.containsKey(arg.charAt(0))) {
					indexTable.put(arg.charAt(0), indexTable.size());
				}
			}
		}

		for (String arg : list) {
			if (arg.length() > 0 && Character.isLowerCase(arg.charAt(0))) { //// BUG: NOT if (arg.length() == 1)
				char var = (char) (indexTable.get(arg.charAt(0)) + 'a');
				standardList.add("" + var);
			} else {
				standardList.add(arg);
			}
		}

		return standardList;
	}
	

}
