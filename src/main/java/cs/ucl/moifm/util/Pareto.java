/**
 * 
 */
package cs.ucl.moifm.util;

import java.util.List;

import cs.ucl.moifm.model.Plan;

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
	
	private static List<Plan> nonDominated;
	
//	public Pareto(Population population){
//		pop = population;
//	}
	
	public static List<Plan> shortlist(){
		boolean pareto;
		
		for (int i = 0; i < pop.plans.size(); i++){
			pareto = true;
			for (int j = 0; j < pop.plans.size(); j++){
				if (i == j) continue;
				if (dominates(pop.plans.get(j), pop.plans.get(i))){
					pareto = false;
					break;
				}
			}
			if (pareto)
				nonDominated.add(pop.plans.get(i));
		}
		return nonDominated;
		
	}

	public static Population getPop() {
		return pop;
	}

	public static void setPop(Population pop) {
		Pareto.pop = pop;
	}

	public static List<Plan> getNonDominated() {
		return nonDominated;
	}

	public static void setNonDominated(List<Plan> nonDominated) {
		Pareto.nonDominated = nonDominated;
	}
	
	public static boolean dominates (Plan plan, Plan plan2){
		boolean dominate = false;
		
		if (plan.getExpectedCost() >= plan2.getExpectedCost() &&
				plan.getExpectedNPV() >= plan2.getExpectedNPV() &&
				plan.getInvestmentRisk() <= plan2.getInvestmentRisk()){
			dominate = true;
		}
		
		return dominate;
	}
	
}
