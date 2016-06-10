package cs.ucl.moifm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class Plan {
	// Representation of the plan for genetic manipulation
	List<Integer> chromosome;
	
	// cost of implementing the plan
	double expectedCost;
	
	// revenue generating power of the plan
	double expectedNPV;
	
	// investment risk of the plan
	double investmentRisk;
	
	double expectedROI;
	
	public int domCount;
	
	double minNPV, maxNPV, minCost, maxCost;
	
	public List<Plan> domSet;
	
	public int rank;
	
	public double crowdingDistance;
	
	public List<String> featureVector;
	
	//Constructor
	
	public Plan(int size, Project project){
		this.chromosome = new ArrayList<Integer>(Collections.nCopies(size, 0));
		this.expectedCost = 0;
		this.expectedNPV = 0;
		this.expectedROI = 0;
		this.investmentRisk = 0;
		this.domSet = new ArrayList<Plan>();
		this.featureVector = project.getFeatures();
	}
	
	public void setChromosome(List<Integer> chromosome){
		this.chromosome = chromosome;
	}
	
	public List<Integer> getChromosome(){
		return chromosome;
	}
	
	/**
     * Get the net present value generated from the sequence
     * 
     */
	public double getExpectedNPV() {
		return expectedNPV;
	}
	
	/**
     * Sets the revenue to be generated from implementing the sequence
     * 
     * @param expectedPresentValue
     */
	public void setExpectedNPV(double expectedPresentValue) {
		this.expectedNPV = expectedPresentValue;
	}
	
	public double getInvestmentRisk() {
		return investmentRisk;
	}

	public void setInvestmentRisk(double investmentRisk) {
		this.investmentRisk = investmentRisk;
	}
	
	public double getExpectedROI() {
		return expectedROI;
	}

	public void setExpectedROI(double expectedROI) {
		this.expectedROI = expectedROI;
	}
	
	/**
     * Get the expected cost to implement features in the delivery sequence
     */
	public double getExpectedCost() {
		return expectedCost;
	}
	
	/**
     * Sets the cost of implementing the sequence
     * 
     * @param expectedCost
     */
	public void setExpectedCost(double expectedCost) {
		this.expectedCost = expectedCost;
	}
	
	public void evaluateFitness(Project project){
		Double[] npv = new Double[project.nOfSim];
		Double[] cost = new Double[project.nOfSim];
		HashMap<Integer, String> plan = this.transformPlan();
						
		for (int k = 0; k < project.nOfSim; k++){
			npv[k] = cost[k] = 0.0;
			List<String> executedFeatures = new ArrayList<String>();
			for (Map.Entry<Integer, String> entry : plan.entrySet()){
				int currentPeriod = entry.getKey();
				double periodRevenue = 0;
				double periodInvestment ;
				if (currentPeriod == 0)
					continue;
				String[] features = entry.getValue().split(",");
				//get investment cost
				if (!executedFeatures.isEmpty()){
					for (String deliveredFeature : executedFeatures){
						Double value = project.getSimCashflow().get(deliveredFeature)[k][currentPeriod-1];
						periodRevenue += getDiscountedValue(project.getInterestRate(), currentPeriod, value);
					}
				}
				for (String feature : features){
					int j = 0;
					while(project.getSimCashflow().get(feature)[k][j] < 0){
						periodInvestment = getDiscountedValue(project.getInterestRate(), currentPeriod+j, 
								project.getSimCashflow().get(feature)[k][j]);
						periodInvestment += periodRevenue;
						cost[k] += (periodInvestment < 0) ? periodInvestment: 0;
						j++;
					}
					
					//value of the feature
					npv[k] += project.getSimSanpv().get(feature)[k][currentPeriod-1];
					executedFeatures.add(feature);
				}
				
				
			}
		}
		//Compute expected npv, expected cost and investment risk
		
				DescriptiveStatistics statsCost = new DescriptiveStatistics();
				DescriptiveStatistics statsNpv = new DescriptiveStatistics();

				
				for (int i = 0; i < project.nOfSim; i++){
					statsNpv.addValue(npv[i]);
					statsCost.addValue(cost[i]);
				}
				
				expectedCost = statsCost.getMean();
				minCost = statsCost.getMin();
				maxCost = statsCost.getMax();
				expectedNPV = statsNpv.getMean();
				minNPV = statsNpv.getMin();
				maxNPV = statsNpv.getMax();
				double npvSD = statsNpv.getStandardDeviation();
				investmentRisk = Math.abs(expectedNPV / npvSD);
				expectedROI = (expectedNPV / Math.abs(expectedCost)) * 100;
	}
	
	public void generatePlan(Project project){	
		int noOfFeatures = featureVector.size();
		int startedFeatures = 0;
		int unstartedFeatures = noOfFeatures;
		List<Integer> indexAdded = new ArrayList<Integer>();
		int period = 0;
		while(period < project.getPeriods() && startedFeatures < noOfFeatures){
			period++;
			int randIndex;
			int noOfFeaturesInPeriod = (int) Math.round(Math.random() * unstartedFeatures);
			for (int i = 0; i < noOfFeaturesInPeriod; i++){
				do{
					randIndex = (int) (Math.random() * noOfFeatures);
				} while(indexAdded.contains(randIndex));
				chromosome.remove(randIndex);
				chromosome.add(randIndex, period);
				
				indexAdded.add(randIndex);
			}
			startedFeatures += noOfFeaturesInPeriod;
			unstartedFeatures -= noOfFeaturesInPeriod;
		}
		
	}
	
	public HashMap<Integer, String> transformPlan(){
		HashMap<Integer, String> decodedPlan = new HashMap<Integer, String>();
		for (int i = 0; i < chromosome.size(); i++){
			String feature = featureVector.get(i);
			int gene = chromosome.get(i);
			if (decodedPlan.containsKey(gene)){
				String value = decodedPlan.get(gene) + "," + feature;
				decodedPlan.put(gene, value);
			}
			else {
				decodedPlan.put(gene, feature);
			}
		}
		return decodedPlan;
	}
	
	public boolean isValidPlan1(Project project){
		boolean isValid = true;
		
		//for each feature in featurevector
		for (int i = 0; i < chromosome.size(); i++){
			String featureId = featureVector.get(i);
			String precursor = project.getMmfs().get(featureId).getPrecursorString();
			if (precursor == ""){
				continue;
			}
			int precursorIndex = featureVector.indexOf(precursor);
			if (chromosome.get(i) <= chromosome.get(precursorIndex)){
				return false;
			}
		}
		// if 
		return isValid;
	}
	
	public boolean isValidPlan(Project project){
		boolean isValid = true;
		
		//for each feature in featurevector
		for (int i = 0; i < chromosome.size(); i++){
			if (chromosome.get(i) == 0){
				continue;
			}
			String featureId = featureVector.get(i);
			String precursor = project.getMmfs().get(featureId).getPrecursorString();
			if (precursor == ""){
				continue;
			}
			int precursorIndex = featureVector.indexOf(precursor);
//			if (chromosome.get(precursorIndex) == 0){
//				return false;
//			}
			
			if (chromosome.get(i) < chromosome.get(precursorIndex)){
				return false;
			}
			else if (chromosome.get(i) == chromosome.get(precursorIndex)){
				if (chromosome.get(i) != 0)
					return false;
			}
		}
		// if 
		return isValid;
	}
	

	public String toString(){
		String newString = "";
		for (Integer s : chromosome){
			newString += s;
		}
		return newString;
	}
	
	public Double getDiscountedValue(double interestRate, int period, Double value){
    	if (period < 1) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + period);
        }  	
    	return value / Math.pow(interestRate + 1, period);
    }
	
	public Double[][] cashFlowAnalysis(HashMap<Integer, String> plan){
		Double [][] cFlow = new Double[featureVector.size()+2][];
		
		return cFlow;
	}
	
}
