/**
 * 
 */
package cs.ucl.moifm;

import com.opencsv.*;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.MMFException;
import cs.ucl.moifm.model.Plan;
import cs.ucl.moifm.model.Project;
import cs.ucl.moifm.util.Front;
import cs.ucl.moifm.util.Genetic;
import cs.ucl.moifm.util.MCSimulation;
import cs.ucl.moifm.util.ModelParser;
import cs.ucl.moifm.util.Population;

import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.random.RandomGenerator;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
/**
 * @author Olawole
 *
 */
public class Main {
	
	public static final int NO_GENERATION = 5;
	public static final int POP_SIZE = 20;

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MMFException 
	 */
	public static void main(String[] args) throws IOException, MMFException {
		// TODO Auto-generated method stub
		try {
			long startTime = System.currentTimeMillis();
			CSVReader reader = new CSVReader(new FileReader("input2.csv"));
			CSVReader precedenceReader = new CSVReader(new FileReader("precedence2.csv"));
			 Project project = new Project();
			 ModelParser.fileToModelParser(reader, project);
			 ModelParser.convertFileToPrecedence(precedenceReader, project);
			 MCSimulation simu = new MCSimulation(project.getPeriods());
			 simu.simulate(project);
			 simu.simulate_sanpv(project.getSimCashflow(), project);
//			 System.out.println(project.getFeatures().toString());
//			 Plan p = new Plan(project.getFeatures().size(), project);
//			 for (int i=0; i<100;i++){
//				 do{
//				 p.generatePlan(project);
//				 } while (!p.isValidPlan(project));
//				 p.evaluateFitness(project);
//				 System.out.println(p.getChromosome().toString());
//				 System.out.println(p.transformPlan().toString());
//				 System.out.println("Cost = "+ p.getExpectedCost());
//				 System.out.println("Value = " + p.getExpectedNPV());
//
//			 }
			 
			 
			/* Double value[][] = project.getSimCashflow().get("A");
			 Double sanpv[][] = project.getSimSanpv().get("A");
			 Double[] val = value[0];
			 Double[] val2 = sanpv[0];
			 for (int i = 0; i < val.length; i++)
				 System.out.print(val[i] + " ");
			 System.out.println("\n");
			 for (int i = 0; i < val2.length; i++)
				 System.out.print(val2[i] + " ");
			/* for (int i = 0; i < 100; i++){
				 for (int j = 0; j < project.getPeriods(); j++){
					 System.out.print(value[i][j] + " ");
				 }
				 System.out.println("\n");
			 }
			 */
			 Population pop = new Population(POP_SIZE, project, true);			 
			 for (int i = 0; i < NO_GENERATION; i++){
				 System.out.println("Generation " + (i+1));
				 long start = System.currentTimeMillis();
				 pop = Genetic.evolvePopulation(pop);
				 long runT = System.currentTimeMillis() - start;
				 System.out.println("Time = " + runT/1000 + " Seconds");
			 }
			 System.out.println("\n----------------------------------------------------------------------");
			 Front pareto = pop.fastNonDominatedSort().get(0);
			 pareto.sortNpv(0, pareto.members.size() - 1);
			 for (int i = 0; i < pareto.members.size(); i++){
				 System.out.println(pareto.members.get(i).transformPlan().toString() + "\tENPV = " + pareto.members.get(i).getExpectedNPV()
						 + "\tECOST = " + pareto.members.get(i).getExpectedCost() + "\tRisk = " + 
						 pareto.members.get(i).getInvestmentRisk() + "\tEROI = " + pareto.members.get(i).getExpectedROI());
			//	 pop.dSequence.get(i).setFitness(project);
			 }
			 System.out.println("Solutions Explored = " + Population.archive.size());
			 System.out.println("Mutation = " + Genetic.mutationNumber);
			 System.out.println("Crossover = " + Genetic.crossOverNumber);
			 reader.close();
			 precedenceReader.close(); 
			 long runtime = System.currentTimeMillis() - startTime;
			 System.out.println("Runtime = " + runtime / 1000 + " Seconds");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
