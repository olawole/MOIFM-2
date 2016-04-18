package cs.ucl.moifm.util;

import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.Project;

public class Genetic {
	
	//parameters
	private static final double MUTATION_RATE = 1.0 / 9.0;
	public static int mutationNumber = 0;
	public static int crossOverNumber = 0;
	//private static final int TOURNAMENT_SIZE = 3;
	//private static final boolean ELITISM = true;
	private static final int POPULATION_SIZE = 30;
	
	public static Population evolvePopulation(Population pop){
		//System.out.println("Enter Evolve");
		Population newPopulation = new Population(POPULATION_SIZE, pop.project, false);
		Population children = new Population(POPULATION_SIZE, pop.project, false);
		children = reproduce(pop);
		newPopulation = selection(pop, children);
			
		//System.out.println("Exit Evolve");
		return newPopulation;
	}

	// Mutate a sequence using swap mutation
	private static void mutate(DeliverySequence deliverySequence) {
		// Loop through MMF sequences
		for (int pos1 = 0; pos1 < deliverySequence.getSequence().size(); pos1++){
			// Apply mutation
			if (Math.random() < MUTATION_RATE){
				mutationNumber += 1;
				// Get another random position
				int pos2 = (int) (deliverySequence.getSequence().size() * Math.random());
				Collections.swap(deliverySequence.getSequence(), pos1, pos2);
			}
		}
		
	}

	private static DeliverySequence crossover(DeliverySequence parent_1,
			DeliverySequence parent_2) {
		DeliverySequence child = new DeliverySequence();
		int startPos, endPos;
		for(int k = 0; k < parent_1.getSequence().size(); k++){
			child.getSequence().add(null);
		}
		// Get the start and end subsequence for parent 1 sequence
		do {
			startPos = (int) (Math.random() * parent_1.getSequence().size());
			endPos = (int) (Math.random() * parent_1.getSequence().size());
		} while (Math.abs(startPos - endPos) < 2);
		
		//note
		for (int i = 0; i < child.getSequence().size(); i++){
			if (startPos < endPos && i > startPos && i < endPos){
				child.getSequence().remove(i);
				child.getSequence().add(i, parent_1.getSequence().get(i));
			}
			else if (startPos > endPos){
				if (!(i < startPos && i > endPos)){
					child.getSequence().remove(i);
					child.getSequence().add(i, parent_1.getSequence().get(i));
				}
			}
		}
		
		// Loop through parent 2 sequence
		for (int i = 0; i < parent_2.getSequence().size(); i++){
			// if child doesn't have the MMF add it
			if (!child.getSequence().contains(parent_2.getSequence().get(i))){
				//loop through to find a spare position in the child sequence
				for(int ii = 0; ii < child.getSequence().size(); ii++){
					if(child.getSequence().get(ii) == null){
						child.getSequence().remove(ii);
						child.getSequence().add(ii, parent_2.getSequence().get(i));
						break;
					}
				}
				
			}
		}
		++crossOverNumber;
		return child;
	}
	
	// Select candidate sequence for crossover operation
	public static DeliverySequence tournamentSelection(Population pop) {
		// Create a tournament population
		int i = 0, j = 0;
		do {
			i = (int) (Math.random() * pop.dSequence.size());
			j = (int) (Math.random() * pop.dSequence.size());
		} while (i == j);
		DeliverySequence candidate1 = pop.dSequence.get(i);
		DeliverySequence candidate2 = pop.dSequence.get(j);
		if (crowdedComparison(candidate1, candidate2))
			return candidate1;
		else
			return candidate2;
	}
	
	public static Population reproduce(Population pop){
		//System.out.println("Enter Reporoduce");
		Population children = new Population(POPULATION_SIZE, pop.project, false);
		for (int i = 0; i < POPULATION_SIZE; i++){
			DeliverySequence child;
			do{
				DeliverySequence parent_1 = tournamentSelection(pop);
				DeliverySequence parent_2 = tournamentSelection(pop);
				child = crossover(parent_1, parent_2);
				mutate(child);
				if(!(child.isValidSequence(pop.project)) && !(Population.invalid.contains(child.toString()))){
					Population.invalid.add(child.toString());
					continue;
				}
				//else if (child.isValidSequence(pop.project) && (pop.project.getMaxMmfsPerPeriod() > 1)){
				//		child.convertSequence(pop.project);
				//}
			} while (Population.archive.contains(child.toString()) || !(child.isValidSequence(pop.project)));
			child.setFitnes(pop.project);
			children.saveSequence(i, child);
			//System.out.println(i);
			Population.archive.add(child.toString());
		}
		//System.out.println("Exit Reproduce");
		return children;
	}
	
	public static Population selection(Population parent, Population children){
		//System.out.println("Enter Selection");
		Population newPopulation = new Population(POPULATION_SIZE, parent.project, false);
		Population union = new Population(2 * POPULATION_SIZE, parent.project, false);
		union.dSequence.addAll(parent.dSequence);
		union.dSequence.addAll(children.dSequence);
		
		int inserted = 0; int last_front = 0;
		List<Front> fronts = union.fastNonDominatedSort();
		
		for (int i = 0; i < fronts.size(); ++i){
			last_front = i;
			//System.out.println("Enter loop");
			Front front = fronts.get(i);
			front.crowdingDistance();
			if (inserted + front.members.size() > POPULATION_SIZE)
				break;
			int j = 0;
			while (j < front.members.size()){
				newPopulation.saveSequence(inserted, front.members.get(j));
				++inserted;
				++j;
			}
			
		}
		//System.out.println("Exit loop");
		int remaining = POPULATION_SIZE - inserted;
		if (remaining > 0){
			Front f = fronts.get(last_front);
			int j = 0;
			f.sortByCrowding(0, fronts.get(last_front).members.size()-1);
			for (int i = inserted; i < POPULATION_SIZE; i++){
				newPopulation.saveSequence(inserted, f.members.get(j));
				j++;
			}
		}
		//System.out.println("Exit selection");
		return newPopulation;
	}
	
	public static boolean crowdedComparison(DeliverySequence d1, DeliverySequence d2){
		
		if(d1.rank == d2.rank)
			return (d1.crowdingDistance > d2.crowdingDistance);
		else
			return (d1.rank < d2.rank);
	}



}
