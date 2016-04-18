package cs.ucl.moifm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.Project;

public class Population {
	
	public List<DeliverySequence> dSequence;
	
	public static List<String> archive = new ArrayList<String>();
	public static List<String> invalid = new ArrayList<String>();
	
	public Project project;
	
	/*
	 * Generate initial population for the Genetic process
	 * 
	 *  @param populationSize
	 *  @param proj
	 *  @param initialize
	 */
	public Population(int populationSize, Project proj, boolean initialize){
		dSequence = new ArrayList<DeliverySequence>();
	//	Population.archive = new ArrayList<String>();
		this.project = proj;
		if (initialize && proj != null){
			int counter = 0;
			while (counter < populationSize){
				DeliverySequence newSequence = new DeliverySequence();
				newSequence.generateIndividuals(proj);
//				if (proj.getMaxMmfsPerPeriod() > 1)
//					newSequence.convertSequence(proj);
				while(Population.archive.contains(newSequence.toString())){
					Collections.shuffle(newSequence.getSequence());
				}
				if (newSequence.isValidSequence(proj)){
					//if (proj.getMaxMmfsPerPeriod() > 1)
					//	newSequence.convertSequence(proj);
					newSequence.setFitnes(proj);
					saveSequence(counter++, newSequence);
					Population.archive.add(newSequence.toString());
				}
				else {
					Population.invalid.add(newSequence.toString());
				}
			}
		}
	}
	
	public Population(int populationSize){
		dSequence = new ArrayList<DeliverySequence>(populationSize);
	}
	
	/*
	 * Save valid delivery sequences
	 * 
	 * @param index
	 * @param newSequence
	 * 
	 */
	public void saveSequence(int index, DeliverySequence newSequence) {
		dSequence.add(index, newSequence);
	}
	
	public List<DeliverySequence> getFittestSequences(){
		List<DeliverySequence> nonDominated = new ArrayList<DeliverySequence>();
		boolean pareto;
		
		for (int i = 0; i < dSequence.size(); i++){
			pareto = true;
			for (int j = 0; j < dSequence.size(); j++){
				if (i == j) continue;
				if (dominates(dSequence.get(j), dSequence.get(i))){
					pareto = false;
					break;
				}
			}
			if (pareto)
				nonDominated.add(dSequence.get(i));
		}
		return nonDominated;
	}
	
	public List<Front> fastNonDominatedSort(){
	//	System.out.println("Enter Fast");
		List<Front> fronts = new ArrayList<Front>(5);
		Front first = new Front(0);
		
		for (DeliverySequence p : dSequence){
			p.domCount = 0;
			p.domSet.clear();
			for (DeliverySequence q : dSequence){
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
			for (DeliverySequence p : fronts.get(i).members){
				for (DeliverySequence q : p.domSet){
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
	//	System.out.println("Exit Fast");
		return fronts;

	}
	
	public boolean dominates (DeliverySequence s1, DeliverySequence s2){
		boolean dominate = false;
		
		if (s1.getExpectedCost() >= s2.getExpectedCost() &&
				s1.getExpectedNPV() >= s2.getExpectedNPV() &&
				s1.getInvestmentRisk() <= s2.getInvestmentRisk()){
			dominate = true;
		}
		
		return dominate;
	}

}
