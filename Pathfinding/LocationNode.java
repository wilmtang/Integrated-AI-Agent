import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LocationNode {
	public static Map<String, LocationNode> nodeList = new LinkedHashMap<>();
	
	
	public final String name;
	Map<LocationNode, Integer> outgoingRoutes = new LinkedHashMap<>();
	LocationNode pre;
	int accumulateTime;
	int estimatedTime;
	int priority;
	
	public LocationNode (String name, String sub, int outgoTime) {
		this.name = name;
		addSub (sub, outgoTime);
	}
	
	public LocationNode (String name) {
		this.name = name;
	}
	
	public void addSub (String sub, int outgoTime) {
		if (!nodeList.containsKey(sub)) {
			nodeList.put (sub, new LocationNode(sub));
		}
		this.outgoingRoutes.put(nodeList.get(sub), outgoTime);
	}
	
	public List<List<String>> constructPath () {
		List<List<String>> ret = new LinkedList<>();
		List<String> temp = new ArrayList<>();
		LocationNode runner = this;
		
		temp.add(this.name);
		temp.add("" + this.accumulateTime);
		ret.add(new ArrayList<>(temp));
		temp.clear();
		
		while (runner.pre != null) {
			temp.add(runner.pre.name);
			temp.add("" + runner.pre.accumulateTime);
			ret.add(0, new ArrayList<>(temp));
			temp.clear();
			runner = runner.pre;		
		}
		
		return ret;
		
	}
}
