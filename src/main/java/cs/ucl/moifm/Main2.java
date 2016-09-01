package cs.ucl.moifm;

import java.io.IOException;

import cs.ucl.moifm.model.MMFException;
import cs.ucl.moifm.model.Project;
import cs.ucl.moifm.util.Front;
import cs.ucl.moifm.util.Genetic;
import cs.ucl.moifm.util.Population;

public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			long startTime = System.currentTimeMillis();
			Project project = MOIFM.parseModel("input3.csv", "precedence3.csv", 0.00241);
//			MOIFM.precedenceGraph(project);
			MOIFM.simulate_cf(project);
			Population randomPop = MOIFM.generateRandomPlan(20, project);
			Population finalPop = MOIFM.evolvePopulation(randomPop, 20);
			Front pareto = MOIFM.getParetoSolutions(finalPop);
			MOIFM.drawScatterPlot(pareto, Genetic.allSolution);
			MOIFM.writeSolutionsToFile(pareto, Genetic.allSolution);
			Double[][] cfa = MOIFM.planCashAnalysis(pareto.members.get(0), project);
			MOIFM.analyisCurve(cfa, project,pareto.members.get(0).transformPlan().toString());
			long runtime = System.currentTimeMillis() - startTime;
			System.out.println("Runtime = " + (runtime / 1000)/60 + " Minutes");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MMFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
