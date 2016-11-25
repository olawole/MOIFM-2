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
			Project project = new Project("Project 1", 16, 0.01,"cost1.csv", "value1.csv", "precedence3.csv");
			System.out.println("Parsing = " + (System.currentTimeMillis() - startTime));
	//		MOIFM.parseModel("cost1.csv", "precedence3.csv", "value1.csv");
	//		MOIFM.precedenceGraph(project);
			MOIFM.simulate_cf(project.getMmfs(), project.getPeriods(), project.getInterestRate());
			System.out.println("Simulation = " + (System.currentTimeMillis() - startTime));
			project.setSimSanpv(MOIFM.calculateSanpv(project.getMmfs(), project.getPeriods(), project.getInterestRate()));
			System.out.println("SANPV = " + (System.currentTimeMillis() - startTime));
			Population randomPop = MOIFM.generateRandomPlan(project, 100);
			System.out.println("Random = " + (System.currentTimeMillis() - startTime));
			Population finalPop = MOIFM.evolvePopulation(project, randomPop, 100);
			System.out.println("Evolution = " + (System.currentTimeMillis() - startTime));
			Front pareto = MOIFM.getParetoSolutions(finalPop);
			System.out.println(pareto.members.size());
			MOIFM.drawScatterPlot(pareto, Genetic.allSolution);
			MOIFM.writeSolutionsToFile(pareto, Genetic.allSolution);
	//		HashMap<String, Double[][]> Analysis = MOIFM.CashAnalysis(pareto.members);
	//		MOIFM.analyisCurve(Analysis, Project.getPeriods(),Project.getFeatures().size());
	//		RoadMap roadmap = new RoadMap(pareto.members);
	//		roadmap.writeDot1();
	//		roadmap.writeDot2();
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
