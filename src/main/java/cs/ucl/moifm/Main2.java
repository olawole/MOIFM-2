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
			
			String[] output = {"council","fin","travel","synthetic"};
			String[] featureCost = {"councilC.csv", "finc.csv", "travelc.csv", "syntheticc.csv"};
			String[] featureValue = {"councilV.csv", "finv.csv", "travelv.csv", "syntheticv.csv"};
			String[] precedence = {"councilP.csv","precedence3.csv","precedence4.csv","precedence4.csv"};
			int[] period = {12, 16, 20, 20};
			double[] runtimes = new double[4];
			double[] discountRate = {0.02, 0.01, 0.008, 0.008};
			for(int i=1; i < 2; i++){
				long startTime = System.currentTimeMillis();
				Project project = new Project("Project "+ i, period[i], discountRate[i],featureCost[i], featureValue[i], precedence[i], "Normal");
				System.out.println("Parsing = " + (System.currentTimeMillis() - startTime));
				MOIFM.simulate_cf(project.getMmfs(), project.getPeriods(), project.getInterestRate());
				System.out.println("Simulation = " + (System.currentTimeMillis() - startTime));
				project.setSimSanpv(MOIFM.calculateSanpv(project.getMmfs(), project.getPeriods(), project.getInterestRate()));
				System.out.println("SANPV = " + (System.currentTimeMillis() - startTime));
				Population randomPop = MOIFM.generateRandomPlan(project, 50);
				System.out.println("Random = " + (System.currentTimeMillis() - startTime));
				Population finalPop = MOIFM.evolvePopulation(project, randomPop, 150);
				System.out.println("Evolution = " + (System.currentTimeMillis() - startTime));
				Front pareto = MOIFM.getParetoSolutions(finalPop);
				pareto.sortNpv();
				System.out.println(pareto.members.size());
				MOIFM.drawScatterPlot(pareto, Genetic.allSolution, output[i]);
				MOIFM.writeSolutionsToFile(pareto, Genetic.allSolution, output[i]);
				HashMap<String, Double[][]> Analysis = MOIFM.CashAnalysis(pareto.members);
				MOIFM.analyisCurve(Analysis, project.getPeriods(),project.getFeatures().size());
				RoadMap roadmap = new RoadMap(pareto.members);
				roadmap.writeDot1(output[i]);
				roadmap.writeDot2(output[i]);
				//Double[][] cfa = MOIFM.planCashAnalysis(pareto.members.get(0));
				//MOIFM.analyisCurve(project.getMmfs(), project.getPeriods(), cfa,pareto.members.get(0).transformPlan().toString());
				long runtime = System.currentTimeMillis() - startTime;
				runtimes[i] = (runtime / 1000) / 60.0;
				System.out.println("Runtime = " + runtimes[i] + " Minutes");
			}
			for(int i=0; i < 4; i++){
				System.out.println(output[i] + "\t" + runtimes[i]);
			}
			
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
