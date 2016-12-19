import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

@SuppressWarnings("unused")
public class PathFinder {
	
	static String searchMethod;
	static String start;
	static String destination;
	
	public static void main (String[] args) throws IOException {
		try {
			read();
		} catch (Exception e) {
			e.printStackTrace();
			File file = new File(".");
			for(String fileNames : file.list()) System.out.println(fileNames);
			return;
		}
		
		//Input Check
//		System.out.println(searchMethod);
//		System.out.println(start);
//		System.out.println(destination);
//		
//		for (Entry<String, LocationNode> node : LocationNode.nodeList.entrySet()) {
//			System.out.println(node.getKey() + "( eta: " + node.getValue().estimatedTime + ")");
//			for (Entry<LocationNode, Integer> sub : node.getValue().outgoingRoutes.entrySet()) {
//				System.out.print(sub.getKey().name + "(" +sub.getValue() + ") ");
//			}
//			System.out.println();
//		}
		
		List<List<String>> ret = null;
		if (searchMethod.equals("DFS")) ret = dfs (start, destination);
		else if (searchMethod.equals("BFS")) ret = bfs (start, destination);
		else if (searchMethod.equals("UCS")) ret = ucs (start, destination);
		else if (searchMethod.equals("A*")) ret = aStar (start, destination);
		
		//TO DO: add writer and write ret.s
		File output = new File("output.txt");
		output.createNewFile();
		PrintWriter writer = new PrintWriter(output);
		for (List<String> l : ret) {
			for (String s : l) {
				writer.print(s+" ");
			}
			writer.println();
		}
		writer.close();
		
	}
	
	/**
	 * Construct graph from input.txt
	 * @throws Exception
	 */
	static void read() throws Exception {
		
		Scanner myscanner = new Scanner(new File("input.txt"));
			searchMethod = myscanner.next();
			start = myscanner.next();
			destination = myscanner.next();
			
			int relations = myscanner.nextInt();
			for (int i = 0; i < relations; i++) {
				String name = myscanner.next();
				String sub = myscanner.next();
				int outgoTime = myscanner.nextInt();
				if (LocationNode.nodeList.containsKey(name)){
					LocationNode.nodeList.get(name).addSub(sub, outgoTime);
				} else {
					LocationNode.nodeList.put(name, new LocationNode (name, sub, outgoTime));
				}
			}
			
			int etaCount = myscanner.nextInt();
			for (int i = 0; i < etaCount; i++) {
				String name = myscanner.next();
				int eta = myscanner.nextInt();
				LocationNode.nodeList.get(name).estimatedTime = eta;
			}
			if (myscanner.hasNext()) {
				myscanner.close();
				throw new Exception ("File Error");
			}
		myscanner.close();

	}
	
	static List<List<String>> dfs (String start, String destination) {
		/*
		 * ret for return list. update ret when current accumulate cost is smaller than shorted.
		 * Initialize shortedPathCost as max integer so that first path would be added anyway.
		 */
		List<List<String>> ret = new ArrayList<>();
		
		int shortestPathCost = Integer.MAX_VALUE;
		
		//Depth-first search, avoid looping with visited
		Queue<LocationNode> q = new LinkedList<>();
		Set<LocationNode> visited = new HashSet<>();
			//TO TO...
		q.add(LocationNode.nodeList.get(start));
		visited.add(q.peek());
		while (!q.isEmpty()) {
			LocationNode top = q.poll();
			if (top.name.equals(destination) && top.accumulateTime < shortestPathCost) {
				ret = top.constructPath();
				shortestPathCost = top.accumulateTime;
				continue;
			}
			
			Queue<LocationNode> newQ = new LinkedList<>();
			for (Entry<LocationNode, Integer> entry : top.outgoingRoutes.entrySet()) {
				if (!visited.contains(entry.getKey())){
					newQ.add(entry.getKey());
					entry.getKey().accumulateTime = top.accumulateTime + 1;
					entry.getKey().pre = top;
					visited.add(entry.getKey());
				}
			}
			newQ.addAll(q);
			q = newQ;
		}
		return ret;		
	}
	
	static List<List<String>> bfs (String start, String destination) {
		List<List<String>> ret = new ArrayList<>();
		int shortestPathCost = Integer.MAX_VALUE;
		
		Queue<LocationNode> q = new LinkedList<>();
		//Depth-first search, avoid looping with visited
		Set<LocationNode> visited = new HashSet<>();
			//TO TO...
		q.add(LocationNode.nodeList.get(start));
		visited.add(q.peek());
		while (!q.isEmpty()) {
			LocationNode top = q.poll();
			if (top.name.equals(destination) && top.accumulateTime < shortestPathCost) {
				ret = top.constructPath();
				shortestPathCost = top.accumulateTime;
				continue;
			}
			
			for (Entry<LocationNode, Integer> entry : top.outgoingRoutes.entrySet()) {
				if (!visited.contains(entry.getKey())) {
					entry.getKey().pre = top;
					entry.getKey().accumulateTime = top.accumulateTime + 1;
					q.add(entry.getKey());
					visited.add(entry.getKey());
				}
			}
		}
		
		return ret;
	}
	
	static List<List<String>> ucs (String start, String destination) {
		List<List<String>> ret = new ArrayList<>();
		int shortedPathCost = Integer.MAX_VALUE;
		int count = 0;
		PriorityQueue<LocationNode> pq = new PriorityQueue<>(LocationNode.nodeList.size(), new Comparator<LocationNode>() {
			public int compare (LocationNode n1, LocationNode n2) {
				return n1.accumulateTime == n2.accumulateTime? n1.priority - n2.priority : n1.accumulateTime - n2.accumulateTime;
			}
		}); 
		Set<LocationNode> visited = new HashSet<>();
		
		pq.add(LocationNode.nodeList.get(start));
		visited.add(pq.peek());
		while (!pq.isEmpty()) {
			LocationNode top = pq.poll();
			if (top.name.equals(destination) && top.accumulateTime < shortedPathCost) {
				ret = top.constructPath();
				shortedPathCost = top.accumulateTime;
				return ret;
			}
			
			for (LocationNode cur : top.outgoingRoutes.keySet()) {
				int curCost = top.accumulateTime + top.outgoingRoutes.get(cur);
				if (!pq.contains(cur) && !visited.contains(cur)) {
					cur.priority = ++count;
					cur.accumulateTime = curCost;
					pq.add(cur);
					cur.pre = top;
					visited.add(cur);
				} else if (pq.contains(cur)) {
					if (curCost < cur.accumulateTime) {
						pq.remove(cur);
						cur.pre = top;
						cur.priority = ++count;
						cur.accumulateTime = curCost;
						pq.offer(cur);
					}
				} else if (visited.contains(cur)) {
					if (curCost < cur.accumulateTime) {
						visited.remove(cur); // used to be pq.remove TEST CASES?
						cur.pre = top;
						cur.priority = ++count;
						cur.accumulateTime = curCost;
						pq.offer(cur);
					}
				}
			}
		}
		
		return ret;
	}
	
	static List<List<String>> aStar (String start, String destination) {
		List<List<String>> ret = new ArrayList<>();
		//int shortedPathCost = Integer.MAX_VALUE;
		int count = 0;
		PriorityQueue<LocationNode> pq = new PriorityQueue<>( new Comparator<LocationNode>() {
			public int compare (LocationNode n1, LocationNode n2) {
				return (n1.accumulateTime + n1.estimatedTime) == (n2.accumulateTime + n2.estimatedTime) ? n1.priority - n2.priority : (n1.accumulateTime + n1.estimatedTime) - (n2.accumulateTime + n2.estimatedTime);
			}
		});
		
		Set<LocationNode> visited = new HashSet<>();
		
		pq.add(LocationNode.nodeList.get(start));
		visited.add(pq.peek());
		
		while (!pq.isEmpty()) {
			LocationNode top = pq.poll();
			if (top.name.equals(destination)) {
				ret = top.constructPath();
				return ret;
			}
			
			for (LocationNode k : top.outgoingRoutes.keySet()) {
				int curCost = top.accumulateTime + top.outgoingRoutes.get(k);
				 if (!pq.contains(k) && ! visited.contains(k)) {
					 k.accumulateTime = curCost;
					 k.priority = ++count;
					 pq.add(k);
					 k.pre = top;
					 visited.add(k);
				 } else if (pq.contains(k)) {
					 if (curCost < k.accumulateTime) {
						 pq.remove(k);
							k.pre = top;
							k.priority = ++count;
							k.accumulateTime = curCost;
						 pq.offer(k);
					 }
				 } else if (visited.contains(k)) {
					 if (curCost < k.accumulateTime) {
						 visited.remove(k);
							k.pre = top;
							k.priority = ++count;
							k.accumulateTime = curCost;
						 pq.offer(k);
					 }
				 }
			}
		}
		return ret;
	}
	
	
}
