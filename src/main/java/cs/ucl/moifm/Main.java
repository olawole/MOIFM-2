/**
 * 
 */
package cs.ucl.moifm;

import com.opencsv.*;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.MMFException;
import cs.ucl.moifm.model.Project;
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

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MMFException 
	 */
	public static void main(String[] args) throws IOException, MMFException {
		// TODO Auto-generated method stub
		try {
			CSVReader reader = new CSVReader(new FileReader("input2.csv"));
			CSVReader precedenceReader = new CSVReader(new FileReader("precedence2.csv"));
			 Project project = new Project();
			 ModelParser.fileToModelParser(reader, project);
			 ModelParser.convertFileToPrecedence(precedenceReader, project);
			 
			 DeliverySequence dseq = new DeliverySequence();
			 dseq.setSequence(project);
			 project.setSanpv();
			 MCSimulation simu = new MCSimulation(project.getPeriods());
			 simu.simulate(project);
			 simu.simulate_sanpv(project.getSimCashflow(), project);
			 
			 Double value[][] = project.getSimCashflow().get("A");
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
			 Population pop = new Population(10, project, true);
			 for (int i = 0; i < 10; i++){
				 System.out.println(pop.dSequence.get(i));
				 pop.dSequence.get(i).setFitness(project);
				 System.out.println(pop.dSequence.get(i).getExpectedNPV());
			 }
			 dseq.setFitness(project);
			 System.out.println("Cost = " + dseq.getExpectedCost());
			 System.out.println("Revenue = " + dseq.getExpectedNPV());
			 
			 System.out.println(dseq.toString());
			 
			 System.out.println(project.getMmfs().get("C").getPrecursorString());
			 reader.close();
			 precedenceReader.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
