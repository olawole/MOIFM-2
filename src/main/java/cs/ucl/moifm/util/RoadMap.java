package cs.ucl.moifm.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	
//	public void writeDot(){
//		HashMap<Integer, String> rank = new HashMap<Integer, String>();
//		String all = "";
//		String lastNode = "";
//		String dotString = "digraph G { \n";
//		dotString += "\trankdir=LR\n";
//		dotString += "\troot[shape=point]\n";
//	//	List<String> nodes = new ArrayList<String>();
//		for (Plan p : optimal){
//			HashMap<Integer, String> solution = p.transformPlan();
//			String label = "";
//			if (solution.containsKey(0)){
//				solution.remove(0);
//			//	continue;
//			}
//			
//			System.out.println(solution.toString());
//			Iterator<String> it = solution.values().iterator();
//			if (it.hasNext()){
//			String object = it.next();
//			if (dotString.indexOf( "root -> \"" + object + "\"") < 0){
//				dotString += "\t\"" + object + "\"[shape = box]\n";
//				label += object;
//				dotString += "\troot -> \"" + object + "\"[label=\"" + label + "\"]\n";
//				Integer key = getKeyFromValue(solution, object);
//				if (!all.contains("\"" + object + "\"")){
//					if (rank.containsKey(key)){
//						String old = rank.get(key);
//						rank.put(key, old + " \"" + object + "\"");
//					
//					}
//					else {
//						rank.put(key, "\"" + object + "\"");
//					}
//					all += "\"" + object + "\",";
//				}
//			}
//			else {
//				label += object;
//			}
//			
//			while (it.hasNext()){
//				String current = it.next();
//				if (dotString.indexOf( "\"" + current + "\"[shape = box, style=rounded]\n") < 0){
//					dotString += "\t\"" + current + "\"[shape = box, style=rounded]\n";
//					Integer key = getKeyFromValue(solution, current);
//					if (!all.contains("\"" + current + "\"")){
//						if (rank.containsKey(key)){
//							String old = rank.get(key);
//							rank.put(key, old + " \"" + current + "\"");
//						}
//						else {
//							rank.put(key, "\"" + current + "\"");
//						}
//						all += "\"" + current + "\",";
//					}
//				}
//					
//				
//				 
//				String str = "\t\"" + object + "\"" + "->" + "\"" + current;
//				if (dotString.indexOf(str) < 0){
//					label += "|" + current;
//					dotString += str + "\"[label=\"" + label + "\"]\n";
//				}
//				else {
//					label += "|" + current;
//				}
//
//				object = current;
//				lastNode = object;
//			}
//			}
//			Double cost = p.getExpectedCost();
//			Double value = p.getExpectedNPV();
////			Double risk = p.getInvestmentRisk() * 100;
//			//lastNode.replaceAll("#", "");
//			dotString += "\t\"" + lastNode + "\"->\"" + label + "\"[label=\"Cost = " + cost + " | Value = " + value + "\"]\n";
//			
//		}
//		for (Integer k : rank.keySet()){
//			dotString += "\t{ rank=same " + rank.get(k) + " }\n";
//		}
//		dotString += "}";
////		for (Map.Entry<Integer, List<String>> entry : map.entrySet()){
////			
////		}
//	//	System.out.println(dotString);
//		try {
//			FileWriter output = new FileWriter("roadmap.dot");
//			output.write(dotString);
//			output.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	public Integer getKeyFromValue(HashMap<Integer, String> hm, String value) {
	    for (Integer key : hm.keySet()) {
	      if (hm.get(key).equals(value)) {
	        return key;
	      }
	    }
	    return null;
	}
//	
	public void writeDot1(String filename){
		HashMap<Integer, String> rank = new HashMap<Integer, String>();
		String all = "";
		String lastNode = "";
		String dotString = "digraph G { \n";
		dotString += "\trankdir=LR\n";
		dotString += "\troot[shape=point]\n";
		int num = 0;
	//	List<String> nodes = new ArrayList<String>();
		for (Plan p : optimal){
			if (++num > 20){
				break;
			}
			HashMap<Integer, String> solution = p.transformPlan();
			String label = "";
			if (solution.containsKey(0)){
				solution.remove(0);
			//	continue;
			}
			
			System.out.println(solution.toString());
			Iterator<String> it = solution.values().iterator();
			if (it.hasNext()){
			String object = it.next();
			label += object;
			if (dotString.indexOf( "root -> \"" + label + "\"") < 0){
				dotString += "\t\"" + label + "\"[shape = box]\n";
				dotString += "\troot -> \"" + label + "\"[label=\"" + object + "\"]\n";
				Integer key = getKeyFromValue(solution, object);
				if (!all.contains("\"" + label + "\"")){
					if (rank.containsKey(key)){
						String old = rank.get(key);
						rank.put(key, old + " \"" + label + "\"");
					
					}
					else {
						rank.put(key, "\"" + label + "\"");
					}
					all += "\"" + label + "\",";
				}
			}
			
			while (it.hasNext()){
				String current = it.next();
				label += "|" + current;
				if (dotString.indexOf( "\"" + label + "\"[shape = box, style=rounded]\n") < 0){
					dotString += "\t\"" + label + "\"[shape = box, style=rounded]\n";
					Integer key = getKeyFromValue(solution, current);
					if (!all.contains("\"" + label + "\"")){
						if (rank.containsKey(key)){
							String old = rank.get(key);
							rank.put(key, old + " \"" + label + "\"");
						}
						else {
							rank.put(key, "\"" + label + "\"");
						}
						all += "\"" + label + "\",";
					}
				}
					 
				String str = "\t\"" + object + "\"" + "->" + "\"" + label + "\"";
				if (dotString.indexOf(str) < 0){
					dotString += str + "[label=\"" + current + "\"]\n";
				}

				object = label;
				lastNode = label;
			}
			}
//			Double cost = p.getExpectedCost();
//			Double value = p.getExpectedNPV();
//			Double risk = p.getInvestmentRisk() * 100;
			//lastNode.replaceAll("#", "");
	//		dotString += "\t\"" + lastNode + "\"->\"" + label + "\"[label=\"Cost = " + cost + " | Value = " + value + "\"]\n";
			
		}
		for (Integer k : rank.keySet()){
			dotString += "\t{ rank=same " + rank.get(k) + " }\n";
		}
		dotString += "}";
		try {
			FileWriter output = new FileWriter(filename+".dot");
			output.write(dotString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeDot2(String filename){
		HashMap<Integer, String> rank = new HashMap<Integer, String>();
		String all = "";
		String lastNode = "";
		String dotString = "digraph G { \n";
		dotString += "\trankdir=LR\n";
		dotString += "\troot[shape=point]\n";
		int num = 0;
	//	List<String> nodes = new ArrayList<String>();
		for (Plan p : optimal){
			if (++num > 20){
				break;
			}
			HashMap<Integer, String> solution = p.transformPlan();
			String label = "";
			if (solution.containsKey(0)){
				solution.remove(0);
			//	continue;
			}
			
			System.out.println(solution.toString());
			Iterator<String> it = solution.values().iterator();
			if (it.hasNext()){
			String object = sort(it.next());
			label += object;
		//	label = sort(label);
			if (dotString.indexOf( "root -> \"" + label + "\"") < 0){
				dotString += "\t\"" + label + "\"[shape = box, style=rounded]\n";
				dotString += "\troot -> \"" + label + "\"[label=\"" + object + "\"]\n";
				Integer key = getKeyFromValue(solution, object);
				if (!all.contains("\"" + label + "\"")){
					if (rank.containsKey(key)){
						String old = rank.get(key);
						rank.put(key, old + " \"" + label + "\"");
					
					}
					else {
						rank.put(key, "\"" + label + "\"");
					}
					all += "\"" + label + "\",";
				}
			}
			
			while (it.hasNext()){
				String current = it.next();
				label += "|" + current;
				label = sort(label);
				if (dotString.indexOf( "\"" + label + "\"[shape = box, style=rounded]\n") < 0){
					dotString += "\t\"" + label + "\"[shape = box, style=rounded]\n";
					Integer key = getKeyFromValue(solution, current);
					if (!all.contains("\"" + label + "\"")){
						if (rank.containsKey(key)){
							String old = rank.get(key);
							rank.put(key, old + " \"" + label + "\"");
						}
						else {
							rank.put(key, "\"" + label + "\"");
						}
						all += "\"" + label + "\",";
					}
				}
					 
				String str = "\t\"" + object + "\"" + "->" + "\"" + label + "\"";
				if (dotString.indexOf(str) < 0){
					dotString += str + "[label=\"" + current + "\"]\n";
				}

				object = label;
				lastNode = label;
			}
			}
//			Double cost = p.getExpectedCost();
//			Double value = p.getExpectedNPV();
//			Double risk = p.getInvestmentRisk() * 100;
			//lastNode.replaceAll("#", "");
	//		dotString += "\t\"" + lastNode + "\"->\"" + label + "\"[label=\"Cost = " + cost + " | Value = " + value + "\"]\n";
			
		}
		for (Integer k : rank.keySet()){
			dotString += "\t{ rank=same " + rank.get(k) + " }\n";
		}
		dotString += "}";
		try {
			FileWriter output = new FileWriter(filename+".dot");
			output.write(dotString);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String sort(String s){
		String l = "";
		for (String c : s.split("")){
			if (c.equals(",") || c.equals("|")){
				continue;
			}
			else {
				l += c;
			}
		}
		char[] ar = l.toCharArray();
		Arrays.sort(ar);
		String sorted = String.valueOf(ar);
		return sorted;
	}
	
}
