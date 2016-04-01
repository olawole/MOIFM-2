package cs.ucl.moifm.util;

import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.Project;

public class Genetic {
	
	//parameters
	private static final double MUTATION_RATE = 0.015;
	private static final int TOURNAMENT_SIZE = 5;
	private static final boolean ELITISM = true;
	private static final int POPULATION_SIZE = 10;
	
	
	
	public static Population evolvePopulation(Population pop){
		Population newPopulation = new Population(POPULATION_SIZE, pop.project, false);
		//keep best solutions if eliticism is enabled
		
		int elitismOffset = 0;
		if (ELITISM){
			for (DeliverySequence d : pop.getFittestSequences()){
				newPopulation.saveSequence(0, d);
				elitismOffset++;
			}
		}
		//Crossover population to form new offsprings
		for (int i = elitismOffset; i < POPULATION_SIZE; i++){
			//Select Parents
			DeliverySequence child;
			do{
				DeliverySequence parent_1 = tournamentSelection(pop);
				DeliverySequence parent_2 = tournamentSelection(pop);
			
				child = crossover(parent_1, parent_2);
				mutate(child);
			} while (Population.archive.contains(child.toString()));
			
			child.setFitness(pop.project);
			newPopulation.saveSequence(i, child);
			Population.archive.add(child.toString());
		}
			
		
		return newPopulation;
	}

	// Mutate a sequence using swap mutation
	private static void mutate(DeliverySequence deliverySequence) {
		// Loop through MMF sequences
		for (int pos1 = 0; pos1 < deliverySequence.getSequence().size(); pos1++){
			// Apply mutation
			if (Math.random() < MUTATION_RATE){
				// Get another random position
				int pos2 = (int) (deliverySequence.getSequence().size() * Math.random());
				Collections.swap(deliverySequence.getSequence(), pos1, pos2);
			}
		}
		
	}

	private static DeliverySequence crossover(DeliverySequence parent_1,
			DeliverySequence parent_2) {
		DeliverySequence child = new DeliverySequence();
		for(int k = 0; k < parent_1.getSequence().size(); k++){
			child.getSequence().add("");
		}
		// Get the start and end subsequence for parent 1 sequence
		int startPos = (int) (Math.random() * parent_1.getSequence().size());
		int endPos = (int) (Math.random() * parent_1.getSequence().size());
		
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
				child.getSequence().remove(i);
				child.getSequence().add(i, parent_1.getSequence().get(i));
			}
		}
		return child;
	}
	
	// Select candidate sequence for crossover operation
	private static DeliverySequence tournamentSelection(Population pop) {
		// Create a tournament population
		Population tournament = new Population(TOURNAMENT_SIZE);
		
		for (int i = 0; i < TOURNAMENT_SIZE; i++){
			int randomId = (int) (Math.random() * pop.dSequence.size());
			tournament.saveSequence(i, pop.dSequence.get(randomId));
		}
		
		List<DeliverySequence> fittest = tournament.getFittestSequences();
		
		int randIndex = (int) (Math.random() * fittest.size());
		
		return fittest.get(randIndex);
	}

}
