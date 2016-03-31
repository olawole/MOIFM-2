/**
 * 
 */
package cs.ucl.moifm.util;

import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;

/**
 * @author Olawole
 *
 * Finds the non-dominated solutions in the population
 */
public class Pareto {
	
//	private static final double[] MARGIN = {5.0,10.0,0.01,5.0};
	
//	private static final String[] MODE = {"max","max","min","max"}; 
	
//	private static final int NO_OF_OBJ = 3;
	
	private static Population pop;
	
	private static List<DeliverySequence> nonDominated;
	
	public Pareto(Population population){
		Pareto.pop = population;
	}
	
	public static List<DeliverySequence> shortlist(){
		boolean pareto;
		
		for (int i = 0; i < pop.dSequence.size(); i++){
			pareto = true;
			for (int j = 0; j < pop.dSequence.size(); j++){
				if (i == j) continue;
				if (dominates(pop.dSequence.get(j), pop.dSequence.get(i))){
					pareto = false;
					break;
				}
			}
			if (pareto)
				nonDominated.add(pop.dSequence.get(i));
		}
		return nonDominated;
		
	}

	public static Population getPop() {
		return pop;
	}

	public static void setPop(Population pop) {
		Pareto.pop = pop;
	}

	public static List<DeliverySequence> getNonDominated() {
		return nonDominated;
	}

	public static void setNonDominated(List<DeliverySequence> nonDominated) {
		Pareto.nonDominated = nonDominated;
	}
	
	public static boolean dominates (DeliverySequence s1, DeliverySequence s2){
		boolean dominate = false;
		
		if (s1.getExpectedCost() >= s2.getExpectedCost() &&
				s1.getExpectedNPV() >= s2.getExpectedNPV() &&
				s1.getInvestmentRisk() <= s2.getInvestmentRisk()){
			dominate = true;
		}
		
		return dominate;
	}
	
}
