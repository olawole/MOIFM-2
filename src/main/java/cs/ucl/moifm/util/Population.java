package cs.ucl.moifm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.Project;

public class Population {
	
	public List<DeliverySequence> dSequence;
	
	public static List<String> archive;
	
	Project project;
	
	/*
	 * Generate initial population for the Genetic process
	 * 
	 *  @param populationSize
	 *  @param proj
	 *  @param initialize
	 */
	public Population(int populationSize, Project proj, boolean initialize){
		dSequence = new ArrayList<DeliverySequence>();
		Population.archive = new ArrayList<String>();
		this.project = proj;
		if (initialize && proj != null){
			int counter = 0;
			while (counter < populationSize){
				DeliverySequence newSequence = new DeliverySequence();
				newSequence.generateIndividuals(proj);
				while(Population.archive.contains(newSequence.toString())){
					Collections.shuffle(newSequence.getSequence());
				}
				if (newSequence.isValidSequence(proj)){
					saveSequence(counter++, newSequence);
					Population.archive.add(newSequence.toString());
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
