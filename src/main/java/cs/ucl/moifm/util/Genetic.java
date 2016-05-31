package cs.ucl.moifm.util;

import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.Plan;
import cs.ucl.moifm.model.Project;

public class Genetic {
	
	//parameters
	private static final double MUTATION_RATE = 1.0 / 9.0;
	public static int mutationNumber = 0;
	public static int crossOverNumber = 0;
	//private static final int TOURNAMENT_SIZE = 3;
	//private static final boolean ELITISM = true;
	public static final int POPULATION_SIZE = 50;
	
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
	private static void mutate(Plan child) {
		// Loop through Chromosome
		for (int pos1 = 0; pos1 < child.getChromosome().size(); pos1++){
			// Apply mutation
			if (Math.random() < MUTATION_RATE){
				mutationNumber += 1;
				// Get another random position
				int pos2 = (int) (child.getChromosome().size() * Math.random());
				Collections.swap(child.getChromosome(), pos1, pos2);
			}
		}
		
	}

	private static Plan crossover(Plan parent_1,
			Plan parent_2, Project project) {
		Plan child = new Plan(project.getFeatures().size(), project);
		int startPos, endPos;
//		for(int k = 0; k < parent_1.getChromosome().size(); k++){
//			child.getChromosome().add(null);
//		}
		// Get the start and end subsequence for parent 1 sequence
		do {
			startPos = (int) (Math.random() * parent_1.getChromosome().size());
			endPos = (int) (Math.random() * parent_1.getChromosome().size());
		} while (Math.abs(startPos - endPos) < 2);
		
		//note
		for (int i = 0; i < child.getChromosome().size(); i++){
			if (startPos < endPos && i > startPos && i < endPos){
				child.getChromosome().remove(i);
				child.getChromosome().add(i, parent_1.getChromosome().get(i));
			}
			else if (startPos > endPos){
				if (!(i < startPos && i > endPos)){
					child.getChromosome().remove(i);
					child.getChromosome().add(i, parent_1.getChromosome().get(i));
				}
			}
		}
		
		// Loop through parent 2 plan
		for (int i = 0; i < parent_2.getChromosome().size(); i++){
			// if child doesn't have the MMF add it
			if (child.getChromosome().get(i) == 0){
				//loop through to find a spare position in the child sequence
				//for(int ii = 0; ii < child.getChromosome().size(); ii++){
					//if(child.getChromosome().get(ii) == 0){
						child.getChromosome().remove(i);
						child.getChromosome().add(i, parent_2.getChromosome().get(i));
					//	break;
					//}
				//}
				
			}
		}
		++crossOverNumber;
		return child;
	}
	
	// Select candidate sequence for crossover operation
	public static Plan tournamentSelection(Population pop) {
		// Create a tournament population
		int i = 0, j = 0;
		do {
			i = (int) (Math.random() * pop.plans.size());
			j = (int) (Math.random() * pop.plans.size());
		} while (i == j);
		Plan candidate1 = pop.plans.get(i);
		Plan candidate2 = pop.plans.get(j);
		if (crowdedComparison(candidate1, candidate2))
			return candidate1;
		else
			return candidate2;
	}
	
	public static Population reproduce(Population pop){
		//System.out.println("Enter Reporoduce");
		Population children = new Population(POPULATION_SIZE, pop.project, false);
		for (int i = 0; i < POPULATION_SIZE; i++){
			Plan child;
			do{
				Plan parent_1 = tournamentSelection(pop);
				Plan parent_2 = tournamentSelection(pop);
				child = crossover(parent_1, parent_2,pop.project);
				mutate(child);
//				if(!(child.isValidPlan(pop.project))){
//					//Population.invalid.add(child.toString());
//					continue;
//				}
				//else if (child.isValidSequence(pop.project) && (pop.project.getMaxMmfsPerPeriod() > 1)){
				//		child.convertSequence(pop.project);
				//}
			} while (!(child.isValidPlan(pop.project)) || Population.archive.contains(child.toString()));
			child.evaluateFitness(pop.project);
			children.savePlan(i, child);
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
		union.plans.addAll(parent.plans);
		union.plans.addAll(children.plans);
		
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
				newPopulation.savePlan(inserted, front.members.get(j));
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
				newPopulation.savePlan(inserted, f.members.get(j));
				j++;
			}
		}
		//System.out.println("Exit selection");
		return newPopulation;
	}
	
	public static boolean crowdedComparison(Plan candidate1, Plan candidate2){
		
		if(candidate1.rank == candidate2.rank)
			return (candidate1.crowdingDistance > candidate2.crowdingDistance);
		else
			return (candidate1.rank < candidate2.rank);
	}



}
