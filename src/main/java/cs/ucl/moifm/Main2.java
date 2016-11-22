package cs.ucl.moifm;

import java.io.IOException;
import java.util.HashMap;

import cs.ucl.moifm.model.FeatureException;
import cs.ucl.moifm.model.Project;
import cs.ucl.moifm.util.Front;
import cs.ucl.moifm.util.Genetic;
import cs.ucl.moifm.util.Population;
import cs.ucl.moifm.util.RoadMap;

public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			long startTime = System.currentTimeMillis();
			Project project = MOIFM.parseModel("input3.csv", "precedence3.csv", 0.0241);
	//		MOIFM.precedenceGraph(project);
			MOIFM.simulate_cf();
			Population randomPop = MOIFM.generateRandomPlan(100);
			Population finalPop = MOIFM.evolvePopulation(randomPop, 100);
			Front pareto = MOIFM.getParetoSolutions(finalPop);
			System.out.println(pareto.members.size());
			MOIFM.drawScatterPlot(pareto, Genetic.allSolution);
			MOIFM.writeSolutionsToFile(pareto, Genetic.allSolution);
			HashMap<String, Double[][]> Analysis = MOIFM.CashAnalysis(pareto.members);
			MOIFM.analyisCurve(Analysis, Project.getPeriods(),Project.getFeatures().size());
			RoadMap roadmap = new RoadMap(pareto.members);
			roadmap.writeDot1();
			roadmap.writeDot2();
	//		Double[][] cfa = MOIFM.planCashAnalysis(pareto.members.get(0), project);
	//		MOIFM.analyisCurve(cfa, project,pareto.members.get(0).transformPlan().toString());
			long runtime = System.currentTimeMillis() - startTime;
			System.out.println("Runtime = " + (runtime / 1000) + " Seconds");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
