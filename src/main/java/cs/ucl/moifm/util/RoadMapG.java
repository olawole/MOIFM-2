//package cs.ucl.moifm.util;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//
//import org.jgraph.graph.DefaultEdge;
//import org.jgrapht.DirectedGraph;
//import org.jgrapht.graph.DefaultDirectedGraph;
//
//import cs.ucl.moifm.model.Plan;
//import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
//import edu.uci.ics.jung.graph.Graph;
//
//public class RoadMapG {
//	
//	private static Graph<String, String> graph;
//	private static List<Plan> solutions;
//	private static HashMap<Integer, String> nodeRank;
//	
//	public RoadMapG(List<Plan> optimal){
//		setSolutions(optimal);
//		graph = new DirectedSparseMultigraph<String, String>();
//	}
//	
//	public static void generateGraph(){
//		graph.addVertex("root");
//		for (Plan p : solutions){
//			HashMap<Integer, String> solution = p.transformPlan();
//			String label = "";
//			if (solution.containsKey(0)){
//				solution.remove(0);
//			//	continue;
//			}
//			Iterator<String> it = solution.values().iterator();
//			if (it.hasNext()){
//			String object = it.next();
//			label += object;
//			if (!graph..containsEdge("root", label)){
//				graph.addVertex(label);
//				graph.
//				dotString += "\troot -> \"" + label + "\"[label=\"" + object + "\"]\n";
//				Integer key = getKeyFromValue(solution, object);
//				if (!all.contains("\"" + label + "\"")){
//					if (rank.containsKey(key)){
//						String old = rank.get(key);
//						rank.put(key, old + " \"" + label + "\"");
//					
//					}
//					else {
//						rank.put(key, "\"" + label + "\"");
//					}
//					all += "\"" + label + "\",";
//				}
//			}
//			
//			while (it.hasNext()){
//				String current = it.next();
//				label += "|" + current;
//				if (dotString.indexOf( "\"" + label + "\"[shape = box, style=rounded]\n") < 0){
//					dotString += "\t\"" + label + "\"[shape = box, style=rounded]\n";
//					Integer key = getKeyFromValue(solution, current);
//					if (!all.contains("\"" + label + "\"")){
//						if (rank.containsKey(key)){
//							String old = rank.get(key);
//							rank.put(key, old + " \"" + label + "\"");
//						}
//						else {
//							rank.put(key, "\"" + label + "\"");
//						}
//						all += "\"" + label + "\",";
//					}
//				}
//					 
//				String str = "\t\"" + object + "\"" + "->" + "\"" + label + "\"";
//				if (dotString.indexOf(str) < 0){
//					dotString += str + "[label=\"" + current + "\"]\n";
//				}
//
//				object = label;
//				lastNode = label;
//			}
//			}
//			Double cost = p.getExpectedCost();
//			Double value = p.getExpectedNPV();
////			Double risk = p.getInvestmentRisk() * 100;
//			//lastNode.replaceAll("#", "");
//	//		dotString += "\t\"" + lastNode + "\"->\"" + label + "\"[label=\"Cost = " + cost + " | Value = " + value + "\"]\n";
//			
//		}
//	}
//
//	public Graph<String, String> getGraph() {
//		return graph;
//	}
//
//	public void setGraph(Graph<String, String> graph) {
//		RoadMapG.graph = graph;
//	}
//
//	public List<Plan> getSolutions() {
//		return solutions;
//	}
//
//	public void setSolutions(List<Plan> solutions) {
//		RoadMapG.solutions = solutions;
//	}
//}
