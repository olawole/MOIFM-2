/**
 * 
 */
package cs.ucl.moifm;

import com.opencsv.*;
import com.orsoncharts.Chart3D;
import com.orsoncharts.Chart3DPanel;
import com.orsoncharts.data.xyz.XYZDataset;

import cs.ucl.moifm.model.MMF;
import cs.ucl.moifm.model.Project;
import cs.ucl.moifm.util.Curve;
import cs.ucl.moifm.util.Front;
import cs.ucl.moifm.util.Genetic;
import cs.ucl.moifm.util.MCSimulation;
import cs.ucl.moifm.util.ModelParser;
import cs.ucl.moifm.util.Plot;
import cs.ucl.moifm.util.Plot3D;
import cs.ucl.moifm.util.Population;
import cs.ucl.moifm.util.PrecedenceGraph;

import org.jfree.ui.RefineryUtilities;
import org.jzy3d.analysis.AnalysisLauncher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JFrame;
/**
 * @author Olawole
 *
 */
public class Main {
	
	public static final int NO_GENERATION = 20;
	public static final int POP_SIZE = 50;
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
			List<MMF> m = new ArrayList<MMF>();
			for (String id : project.getMmfs().keySet()){
				m.add(project.getMmfs().get(id));
			}
//			PrecedenceGraph applet = new PrecedenceGraph(m);
//	        applet.init();
//	        JFrame frame = new JFrame();
//	        frame.getContentPane().add(applet);
//	        frame.setTitle("Precedence graph for the project");
//	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//	        frame.pack();
//	        frame.setVisible(true);
	        
			MCSimulation.simulate(project);
			MCSimulation.simulate_sanpv(project.getSimCashflow(), project);
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
//			pareto.sortNpv(0, pareto.members.size() - 1);
			pareto.sortNpv();
			for (int i = 0; i < pareto.members.size(); i++){
				 System.out.println(pareto.members.get(i).transformPlan().toString() + "\tENPV = " + pareto.members.get(i).getExpectedNPV()
						 + "\tECOST = " + pareto.members.get(i).getExpectedCost() + "\tRisk = " + 
						 pareto.members.get(i).getInvestmentRisk() + "\tEROI = " + pareto.members.get(i).getExpectedROI());
			//	 pop.dSequence.get(i).setFitness(project);
			}
//			Double [][] cfa = pareto.members.get(2).cashFlowAnalysis(pareto.members.get(0).transformPlan(), project);
//			int[] xdata = IntStream.rangeClosed(1, project.getPeriods()).toArray();
//			Double[] ydata = cfa[project.getFeatures().size()+1];
//			final Curve curve = new Curve("Cash Flow Analysis", xdata, ydata);
//		    curve.pack();
//		    RefineryUtilities.centerFrameOnScreen(curve);
//		    curve.setVisible(true);
//			for (int i = 0; i <= project.getFeatures().size()+1; i++){
//				 for (int j = 0; j < cfa[i].length;j++){
//					 System.out.print(cfa[i][j] + "\t");
//				 }
//				 System.out.println();
//			}
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
			
//			Plot sd = new Plot(pareto, Genetic.allSolution);
//			sd.setCanvasType("newt");
//			AnalysisLauncher.open(sd);
			
			Plot3D scatter = new Plot3D(pareto, Genetic.allSolution);
	    	XYZDataset data = scatter.createDataset();
	    	Chart3D chart = scatter.createChart(data);
	    	Chart3DPanel panel = new Chart3DPanel(chart);
	    	panel.setPreferredSize(new java.awt.Dimension(700, 400));
	    	scatter.add(panel);
	    	scatter.pack();
	    	scatter.setLocationRelativeTo(null);
	    	scatter.setVisible(true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
