package cs.ucl.moifm;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.jfree.ui.RefineryUtilities;

import com.opencsv.CSVReader;
import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.data.xyz.XYZDataset;

import cs.ucl.moifm.model.MMFException;
import cs.ucl.moifm.model.Plan;
import cs.ucl.moifm.model.Project;
import cs.ucl.moifm.util.Curve;
import cs.ucl.moifm.util.Front;
import cs.ucl.moifm.util.Genetic;
import cs.ucl.moifm.util.MCSimulation;
import cs.ucl.moifm.util.ModelParser;
import cs.ucl.moifm.util.Plot3D;
import cs.ucl.moifm.util.Population;

public class MOIFM {
	public static final String HEADER = "Plan\tExpected NPV\tExpected Cost\tInvestment Risk\tPareto?";
	public static final String TAB_SEPERATOR = "\t";
	public static final String LINE_SEPERATOR = "\n";
	
	/**
	 * 
	 * 
	 * @param cashFlowPath file path to the CSV file storing the 
	 * set of features to be developed together with their 
	 * cash flow values 
	 * 
	 * @param precedencePath file path of the CSV file storing the 
	 * pairwise precedence relationship among features
	 * 
	 * @param interestRate discount rate of the project
	 * @return a Project object storing the features, cash values and
	 * relationships
	 * @throws IOException throws exception when file not found
	 * @throws MMFException throws exception for invalid feature id
	 */
	public static Project parseModel(String cashFlowPath, String precedencePath, double interestRate) throws IOException, MMFException{
		Project project = new Project();
		CSVReader reader = new CSVReader(new FileReader(cashFlowPath));
		ModelParser.fileToModelParser(reader, project);
		reader = new CSVReader(new FileReader(precedencePath));
		ModelParser.convertFileToPrecedence(reader, project);
		reader.close();
		return project;
	}
	
	/**
	 * 
	 * @param project reference to the current project
	 * @return {@code Project} object storing simulated values
	 */
	
	public static Project simulate_cf(Project project){
		MCSimulation.simulate(project);
		MCSimulation.simulate_sanpv(project.getSimCashflow(), project);
		
		return project;
	}
	
	/**
	 * 
	 * @param size Number of solutions to be generated per generation
	 * @param project reference to the current project
	 * @return a population of length {@literal size}
	 */
	
	public static Population generateRandomPlan(int size, Project project){
		Population randPopulation = new Population(size, project, true);
		return randPopulation;
	}
	
	/**
	 * 
	 * @param initial Initial population to be evolved
	 * @param noOfGeneration number of generation 
	 * before the termination of the evolution 
	 * algorithm
	 * @return final population after the termination of the search
	 */
	public static Population evolvePopulation(Population initial, int noOfGeneration){
		Population pop = initial;
		for (int i = 0; i < noOfGeneration; i++){
			 pop = Genetic.evolvePopulation(pop);
		}
		
		return pop;
	}
	
	/**
	 * 
	 * @param pop obtain the non-dominated solutions from
	 * the population
	 * @return Pareto front of the solution
	 */
	public static Front getParetoSolutions(Population pop){
		Front pareto = pop.fastNonDominatedSort().get(0);
		pareto.sortNpv(0, pareto.members.size() - 1);
		return pareto;
	}
	
	/**
	 * 
	 * @param pareto a set of non-dominated solutions
	 * @param dominated a set of dominated solutions
	 */
	public static void drawScatterPlot(Front pareto, List<Plan> dominated){
		Plot3D scatter = new Plot3D(pareto, dominated);
    	XYZDataset data = scatter.createDataset();
    	Chart3D chart = scatter.createChart(data);
    	Chart3DPanel panel = new Chart3DPanel(chart);
    	panel.setPreferredSize(new java.awt.Dimension(700, 400));
    	scatter.add(panel);
    	scatter.pack();
    	scatter.setLocationRelativeTo(null);
    	scatter.setVisible(true);
	}
	
	/**
	 * 
	 * @param plan a plan denoting a mapping of features to iterations
	 * @param project reference to the project
	 * @return cash flow analysis table
	 */
	public static Double[][] planCashAnalysis(Plan plan, Project project){
		return plan.cashFlowAnalysis(plan.transformPlan(), project);
	}
	
	/**
	 * 
	 * @param cfa cash flow analysis table
	 * @param project reference to the project
	 * @throws Exception 
	 */
	public static void analyisCurve(Double[][] cfa, Project project) throws Exception{
		int[] xdata = IntStream.rangeClosed(1, project.getPeriods()).toArray();
		Double[] ydata = cfa[project.getFeatures().size()+1];
		final Curve curve = new Curve("Cash Flow Analysis", xdata, ydata);
	    curve.pack();
	    RefineryUtilities.centerFrameOnScreen(curve);
	    curve.setVisible(true);
	}
	
	/**
	 * 
	 * @param pareto a set of non-dominated solutions
	 * @param dominated a set of dominated solutions
	 * @throws IOException
	 */
	public static void writeSolutionsToFile(Front pareto, List<Plan> dominated) throws IOException{
		FileWriter output = new FileWriter("solutions.tsv");
		output.append(HEADER.toString());
		output.append(LINE_SEPERATOR);
		for (int i = 0; i < dominated.size(); i++){
			 output.append(dominated.get(i).transformPlan().toString());
			 output.append(TAB_SEPERATOR);
			 output.append(String.valueOf(dominated.get(i).getExpectedNPV()));
			 output.append(TAB_SEPERATOR);
			 output.append(String.valueOf(dominated.get(i).getExpectedCost()));
			 output.append(TAB_SEPERATOR);
			 output.append(String.valueOf(dominated.get(i).getInvestmentRisk()));
			 output.append(LINE_SEPERATOR);
			 if (pareto.members.contains(dominated.get(i))){
				 output.append("Yes");
			 }
			 else {
				 output.append("No");
			 }
			 output.append(LINE_SEPERATOR);
		}
		output.close();
	}
	
//	public static void writeAnalysisToFile()
	
}
