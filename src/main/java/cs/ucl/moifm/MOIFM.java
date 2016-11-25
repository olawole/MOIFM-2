package cs.ucl.moifm;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.ui.RefineryUtilities;

import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.data.xyz.XYZDataset;

import cs.ucl.moifm.model.Feature;
import cs.ucl.moifm.model.FeatureException;
import cs.ucl.moifm.model.Plan;
import cs.ucl.moifm.model.Project;
import cs.ucl.moifm.util.Curve;
import cs.ucl.moifm.util.Front;
import cs.ucl.moifm.util.Genetic;
import cs.ucl.moifm.util.MCSimulation;
import cs.ucl.moifm.util.Plot3D;
import cs.ucl.moifm.util.Population;
import cs.ucl.moifm.util.PrecedenceGraph;

public class MOIFM {
	public static final String HEADER = "Plan\tExpected NPV\tExpected Cost\tInvestment Risk\tPareto?";
	public static final String TAB_SEPERATOR = "\t";
	public static final String LINE_SEPERATOR = "\n";
	
	/**
	 * Read cash flow values and precedence relationship from a file and parse it
	 * into a Project object
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
	 * @throws FeatureException throws exception for invalid feature id
	 */
//	public static void parseModel(String featurePath, String precedencePath, String valuePath) throws IOException, FeatureException{
//		CSVReader reader = new CSVReader(new FileReader(featurePath));
//		Project.readFeatures(reader);
//		reader = new CSVReader(new FileReader(precedencePath));
//		Project.convertFileToPrecedence(reader);
//		reader = new CSVReader(new FileReader(valuePath));
//		Project.readValues(reader);
//		reader.close();
//		
//	}
	
	/**
	 * run Monte Carlo Simulation to generate cash flow scenarios
	 * @param project reference to the current project
	 * @throws FeatureException 
	 * 
	 */
	
	public static void simulate_cf(LinkedHashMap<String, Feature> myFeatures, int period, double intRate) throws FeatureException{
		MCSimulation.simulate(myFeatures, period, intRate);
	}
	
	public static HashMap<String, Double[][]> calculateSanpv(LinkedHashMap<String, Feature> myFeatures, int period, double intRate) throws FeatureException{
		return MCSimulation.calculate_sanpv(myFeatures, period, intRate);
	}
	/**
	 * Generates random assignment of features to iterations or periods
	 * @param size Number of solutions to be generated per generation
	 * @param project reference to the current project
	 * @return a population of length {@literal size}
	 */
	
	public static Population generateRandomPlan(Project project, int size){
		Population randPopulation = new Population(project, size, true);
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
	public static Population evolvePopulation(Project project, Population initial, int noOfGeneration){
		Population pop = initial;
		for (int i = 0; i < noOfGeneration; i++){
			 System.out.println("Generation " + (i+1));
			 pop = Genetic.evolvePopulation(project, pop);
		}
		
		return pop;
	}
	
	/**
	 * Short-list the Pareto front from the final population of 
	 * the evolution process
	 * @param pop obtain the non-dominated solutions from
	 * the population
	 * @return Pareto front of the solution
	 */
	public static Front getParetoSolutions(Population pop){
		Front pareto = pop.fastNonDominatedSort().get(0);
		pareto.sortNpv();
//		pareto.sortNpv(0, pareto.members.size() - 1);
		return pareto;
	}
	
	/**
	 * Plots the dominated and non-dominated solutions on a 
	 * 3D scatter plot with tool tips showing the objective values
	 * of each coordinate
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
    	scatter.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	/**
	 * Generates a cash flow analysis table of the plan
	 * @param plan a plan denoting a mapping of features to iterations
	 * @param project reference to the project
	 * @return cash flow analysis table
	 */
	public static Double[][] planCashAnalysis(Plan plan){
		return plan.cashFlowAnalysis(plan.transformPlan());
	}
	
	public static HashMap<String,Double[][]> CashAnalysis(List<Plan> plans){
		HashMap<String, Double[][]> analysis = new HashMap<String, Double[][]>();
		Double[][] data;
		String label;
		int count = 0;
		for (Plan plan:plans){
			if (++count > 10){
				break;
			}
			data = plan.cashFlowAnalysis(plan.transformPlan());
			label = plan.transformPlan().toString();
			if (!analysis.containsKey(label))
				analysis.put(label, data);
		}
		return analysis; 
	}
	/**
	 * Plot the cash flow analysis curve with NPV on y-axis and
	 * period being on the x-axis
	 * @param cfa cash flow analysis table
	 * @param project reference to the project
	 * @throws Exception 
	 */
	public static void analyisCurve(LinkedHashMap<String, Feature> myFeatures, int period,Double[][] cfa, String name) throws Exception{
		int[] xdata = IntStream.rangeClosed(1, period).toArray();
		Double[] ydata = cfa[myFeatures.size()+1];
		final Curve curve = new Curve("Cash Flow Analysis", xdata, ydata,name);
	    curve.pack();
	    RefineryUtilities.centerFrameOnScreen(curve);
	    curve.setVisible(true);
	}
	
	public static void analyisCurve(HashMap<String, Double[][]> data, int period, int features) throws Exception{
		final Curve curve = new Curve("Cash Flow Analysis", data, period,features);
	    curve.pack();
	    RefineryUtilities.centerFrameOnScreen(curve);
	    curve.setVisible(true);
	}
	/**
	 * Writes the solutions to a TSV file
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
			 output.append(TAB_SEPERATOR);
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
	
	/**
	 * Generates precedence graph for the project
	 * @param project
	 */
	public static void precedenceGraph(LinkedHashMap<String, Feature> myFeatures){
		List<Feature> m = new ArrayList<Feature>();
		for (String id : myFeatures.keySet()){
			m.add(myFeatures.get(id));
		}
		PrecedenceGraph applet = new PrecedenceGraph(m);
        applet.init();
        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle("Precedence graph for the project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
	}
	
}
