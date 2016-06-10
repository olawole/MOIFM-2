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
import cs.ucl.moifm.util.Plot;
import cs.ucl.moifm.util.Population;

import org.jzy3d.analysis.AnalysisLauncher;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * @author Olawole
 *
 */
public class Main {
	
	public static final int NO_GENERATION = 20 ;
	public static final int POP_SIZE = Genetic.POPULATION_SIZE;
	public static final String HEADER = "Plan\tExpected NPV\tExpected Cost\tInvestment Risk";
	public static final String TAB_SEPERATOR = "\t";
	public static final String LINE_SEPERATOR = "\n";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
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
			 System.out.println(Genetic.allSolution.size());
			 FileWriter output = new FileWriter("output.tsv");
			 output.append(HEADER.toString());
			 output.append(LINE_SEPERATOR);
			 for (int i = 0; i < pareto.members.size(); i++){
				 output.append(pareto.members.get(i).transformPlan().toString());
				 output.append(TAB_SEPERATOR);
				 output.append(String.valueOf(pareto.members.get(i).getExpectedNPV()));
				 output.append(TAB_SEPERATOR);
				 output.append(String.valueOf(pareto.members.get(i).getExpectedCost()));
				 output.append(TAB_SEPERATOR);
				 output.append(String.valueOf(pareto.members.get(i).getInvestmentRisk()));
				 output.append(LINE_SEPERATOR);
			 }
			 output.close();
			 reader.close();
			 precedenceReader.close(); 
			 long runtime = System.currentTimeMillis() - startTime;
			 System.out.println("Runtime = " + (runtime / 1000)/60 + " Minutes");
			 Plot sd = new Plot(pareto, Genetic.allSolution);
			 sd.setCanvasType("newt");
			 AnalysisLauncher.open(sd);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
