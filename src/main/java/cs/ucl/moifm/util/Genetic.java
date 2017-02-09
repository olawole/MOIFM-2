package cs.ucl.moifm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.Feature;
import cs.ucl.moifm.model.Plan;
import cs.ucl.moifm.model.Project;

public class Genetic {
	
	//parameters
	public static final double MUTATION_RATE = 1.0 / 12.0;
	public static final double CROSSOVER_PROBABILITY = 0.8;
	public static final String[] MUTATION_OPERATOR = new String[]{"swap","swap","swap","flipnonzero"};
	public static int mutationNumber = 0;
	public static int crossOverNumber = 0;
//	public static final int POPULATION_SIZE = 100;
	public static List<Plan> allSolution = new ArrayList<Plan>();
	
	public static Project project;
	
	public static Population evolvePopulation(Project proj, Population pop){
		if (pop.plans.size() < 1)
			return null;
		project = proj;
		Population newPopulation = new Population(project, pop.plans.size(), false);
		Population children = new Population(project, pop.plans.size(), false);
		children = reproduce(pop);
		newPopulation = selection(pop, children);
		return newPopulation;
	}

	// Mutate a sequence using swap mutation
	public static void mutate(Plan child) {
		if (Math.random() < MUTATION_RATE){
		int opIndex = (int)(Math.random() * MUTATION_OPERATOR.length);
		switch (MUTATION_OPERATOR[opIndex]){
//		case "flipzero":
//			mutationNumber += 1;
//			int mutPoint = (int) (child.getChromosome().size() * Math.random());
//			child.getChromosome().remove(mutPoint);
//			child.getChromosome().add(mutPoint, 0);
//			break;
		case "swap":
			mutationNumber += 1;
			int pos1 = (int) (child.getChromosome().size() * Math.random());
			int pos2 = (int) (child.getChromosome().size() * Math.random());
			Collections.swap(child.getChromosome(), pos1, pos2);
			break;
		case "flipnonzero":
			mutationNumber += 1;
			int point = (int) (child.getChromosome().size() * Math.random());
			child.getChromosome().remove(point);
			child.getChromosome().add(point, point);
			break;
		}
		// Loop through Chromosome
//		for (int pos1 = 0; pos1 < child.getChromosome().size(); pos1++){
//			// Apply mutation
//			if (Math.random() < MUTATION_RATE){
//				mutationNumber += 1;
//				// Get another random position
//				int pos2 = (int) (child.getChromosome().size() * Math.random());
//				Collections.swap(child.getChromosome(), pos1, pos2);
//			}
//		}
		}
	}

	public static Plan crossover(Plan parent_1,
			Plan parent_2) {
		Plan child = new Plan(project, project.getFeatures().size());
		int startPos, endPos;
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
				child.getChromosome().remove(i);
				child.getChromosome().add(i, parent_2.getChromosome().get(i));
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
//		System.out.println("Enter Reproduce");
		final int POPULATION_SIZE = pop.plans.size();
		Population children = new Population(project, POPULATION_SIZE, false);
		for (int i = 0; i < POPULATION_SIZE; i++){
			Plan child;
			do{
				Plan parent_1 = tournamentSelection(pop);
				Plan parent_2 = tournamentSelection(pop);
				child = crossover(parent_1, parent_2);
				mutate(child);
				if(child.getChromosome().contains(0))
					repairChromosome(child);
//				System.out.println(child.toString() + " = " + child.isValidPlan(pop.project));
			} while (!(child.isValidPlan()) || Population.archive.contains(child.toString()));
			
			//child.evaluateFitness();
			children.savePlan(i, child);
			Population.archive.add(child.toString());
			allSolution.add(child);
		}
//		System.out.println("Exit Reproduce");
		return children;
	}
	
	public static Population selection(Population parent, Population children){
//		System.out.println("Enter selection");
		final int POPULATION_SIZE = parent.plans.size();
		Population newPopulation = new Population(project, POPULATION_SIZE, false);
		Population union = new Population(project, 2 * POPULATION_SIZE, false);
		union.plans.addAll(parent.plans);
		union.plans.addAll(children.plans);
		
		int inserted = 0; int last_front = 0;
		List<Front> fronts = union.fastNonDominatedSort();
		
		for (int i = 0; i < fronts.size(); ++i){
			last_front = i;
			Front front = fronts.get(i);
			
			if (inserted + front.members.size() > POPULATION_SIZE){
//				System.out.println("Enter if");
				front.crowdingDistance();
//				System.out.println("Exit if");
				break;
			}
			int j = 0;
//			System.out.println("Enter while");
			while (j < front.members.size()){
				newPopulation.savePlan(inserted, front.members.get(j));
				++inserted;
				++j;
			}
//			System.out.println("Exit while");
			
		}
		int remaining = POPULATION_SIZE - inserted;
		if (remaining > 0){
			Front f = fronts.get(last_front);
			int j = 0;
//			f.sortByCrowding(0, fronts.get(last_front).members.size()-1);
			f.sortByCrowding();
			for (int i = inserted; i < POPULATION_SIZE; i++){
				newPopulation.savePlan(inserted, f.members.get(j));
				j++;
			}
		}
//		System.out.println("Exit selection");
		return newPopulation;
	}
	
	public static boolean crowdedComparison(Plan candidate1, Plan candidate2){
		
		if(candidate1.rank == candidate2.rank)
			return (candidate1.crowdingDistance > candidate2.crowdingDistance);
		else
			return (candidate1.rank < candidate2.rank);
	}
	
	public static void repairChromosome(Plan plan){
		for(int i = 0; i < plan.getChromosome().size();i++){
			if (plan.getChromosome().get(i) == 0){
				continue;
			}
			else {
				String featureId = plan.featureVector.get(i);
				for (Feature feature : project.getMmfs().get(featureId).getPrecursors()){
					String precursor = feature.getId();
					if (precursor.equals("")){
						continue;
					}
					int precursorIndex = plan.featureVector.indexOf(precursor);
				
					if(plan.getChromosome().get(precursorIndex) == 0){
						plan.getChromosome().remove(i);
						plan.getChromosome().add(i, 0);
					}
				}
			}
		}
	}
}
