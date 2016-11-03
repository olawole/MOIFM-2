package cs.ucl.moifm.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cs.ucl.moifm.model.Plan;

public class RoadMap {
	
	public RoadMap(List<Plan> optimal) {
		this.optimal = optimal;
//		this.periodLength = periodLength;
//		pareto = new ArrayList<HashMap<Integer, String>>();
//		for (int i = 0; i < optimal.size(); i++){
//			pareto.add(i, optimal.get(i).transformPlan());
//		}
	}
	

	List<Plan> optimal;
//	int periodLength;
//	List<HashMap<Integer, String>> pareto;
//	
//	public HashMap<Integer, List<String>> analyse(){
//		
//		HashMap<Integer, List<String>> map = new HashMap<Integer, List<String>>();
//		
//		for (int i = 1; i <= periodLength; i++){
//			List<String> str = new ArrayList<String>();
//			for (HashMap<Integer, String> entry : pareto){
//				if (entry.containsKey(i)){
//					String features = entry.get(i);
//					if (!str.contains(features)){
//						str.add(features);
//					}
//				}
//			}
//			if (!str.isEmpty()){
//				map.put(i, str);
//			}
//			
//		}
//		
//		return map;
//	}
	
	public void writeDot(){
		String dotString = "digraph G { \n";
		dotString += "root[shape=point]\n";
	//	List<String> nodes = new ArrayList<String>();
		for (Plan p : optimal){
			HashMap<Integer, String> solution = p.transformPlan();
			solution.remove(0);
			System.out.println(solution.toString());
			Iterator<String> it = solution.values().iterator();
			if (it.hasNext()){
			String object = it.next();
			if (dotString.indexOf( "root -> \"" + object + "\"\n") < 0){
				dotString += "\"" + object + "\"[shape = box]\n";  
				dotString += "root -> \"" + object + "\"\n"; 
			}
			
			while (it.hasNext()){
				String current = it.next();
				if (dotString.indexOf( "\"" + current + "\"[shape = box, style=rounded]\n") < 0){
					dotString += "\"" + current + "\"[shape = box, style=rounded]\n";
				}
				 
				String str = "\"" + object + "\"" + "->" + "\"" + current + "\"\n";
				if (dotString.indexOf(str) < 0){
					dotString += str;
				}

				object = current;
			}
			}
//			for (Map.Entry<Integer, String> entry : solution.entrySet()){
//				String object = entry.getValue();
//				if (!nodes.contains(object)){
//					nodes.add(object);
//					
//				}
//			}
		}
		dotString += "}";
//		for (Map.Entry<Integer, List<String>> entry : map.entrySet()){
//			
//		}
	//	System.out.println(dotString);
		try {
			FileWriter output = new FileWriter("roadmap.dot");
			output.write(dotString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
