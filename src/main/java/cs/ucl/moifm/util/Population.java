package cs.ucl.moifm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.Plan;
import cs.ucl.moifm.model.Project;

public class Population {
	
	public List<Plan> plans;
	
	public static List<String> archive = new ArrayList<String>();
	public static List<String> invalid = new ArrayList<String>();
	public static Double[] MARGIN = new Double[]{30.0, 50.0, 0.01};
	
	public Project project;
	
	/*
	 * Generate initial population for the Genetic process
	 * 
	 *  @param populationSize
	 *  @param proj
	 *  @param initialize
	 */
	public Population(int populationSize, Project proj, boolean initialize){
		plans = new ArrayList<Plan>(populationSize);
	//	Population.archive = new ArrayList<String>();
		this.project = proj;
		if (initialize && proj != null){
			int counter = 0;
			while (counter < populationSize){
				Plan newPlan = new Plan(proj.getFeatures().size(), proj);
				do{
					newPlan.generatePlan(proj);
				} while (!(newPlan.isValidPlan(proj)) || Population.archive.contains(newPlan.toString()));
				newPlan.evaluateFitness(project);
				savePlan(counter++, newPlan);
				Population.archive.add(newPlan.toString());
			}
		}
	}
	
	public Population(int populationSize){
		//dSequence = new ArrayList<DeliverySequence>(populationSize);
	}
	
	/*
	 * Save valid delivery sequences
	 * 
	 * @param index
	 * @param newSequence
	 * 
	 */
	public void savePlan(int index, Plan newPlan) {
		plans.add(index, newPlan);
	}
	
	public List<Plan> getFittestPlans(){
		List<Plan> nonDominated = new ArrayList<Plan>();
		boolean pareto;
		
		for (int i = 0; i < plans.size(); i++){
			pareto = true;
			for (int j = 0; j < plans.size(); j++){
				if (i == j) continue;
				if (dominates(plans.get(j), plans.get(i))){
					pareto = false;
					break;
				}
			}
			if (pareto)
				nonDominated.add(plans.get(i));
		}
		return nonDominated;
	}
	
	public List<Front> fastNonDominatedSort(){
//		System.out.println("Enter Fast");
		List<Front> fronts = new ArrayList<Front>(5);
		Front first = new Front(0);
		
		for (Plan p : plans){
			p.domCount = 0;
			p.domSet.clear();
			for (Plan q : plans){
				if (p.equals(q)){
					continue;
				}
				if (dominates(p,q)){
					p.domSet.add(q);
				}
				else if (dominates(q,p)){
					p.domCount = p.domCount + 1;
				}
			}
			if (p.domCount == 0){
				p.rank = 0;
				first.members.add(p);
			}
		}
		fronts.add(first);
		
		int i = 0;
		while (i < fronts.size() && !fronts.get(i).members.isEmpty()){
			Front nextFront = new Front(i+1);
			for (Plan p : fronts.get(i).members){
				for (Plan q : p.domSet){
					q.domCount = q.domCount - 1;
					if (q.domCount == 0){
						q.rank = i + 1;
						nextFront.members.add(q);
					}
				}
			}
			++i;
			fronts.add(nextFront);
 
		}
//		System.out.println("Exit Fast");
		return fronts;

	}
	
	public boolean dominates (Plan plan1, Plan plan2){
		boolean dominate = false;
		
		if (all(plan1,plan2) && any(plan1,plan2)){
			dominate = true;
		}
		
		return dominate;
	}
	
	public boolean all (Plan plan1, Plan plan2){
		boolean value = false;
		if (plan1.getExpectedCost() >= plan2.getExpectedCost() &&
				plan1.getExpectedNPV() >= plan2.getExpectedNPV() &&
				plan1.getInvestmentRisk() <= plan2.getInvestmentRisk()){
			value = true;
		}
		return value;
	}
	
	public boolean any (Plan plan1, Plan plan2){
		boolean value = false;
		if (plan1.getExpectedCost() > plan2.getExpectedCost() + MARGIN[0] ||
				plan1.getExpectedNPV() > plan2.getExpectedNPV() + MARGIN[1] ||
				plan1.getInvestmentRisk() < plan2.getInvestmentRisk() + MARGIN[2]){
			value = true;
		}
		return value;
	}

}
