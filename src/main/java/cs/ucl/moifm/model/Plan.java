package cs.ucl.moifm.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * 
 * @author Olawole Oni
 * 
 * The class implements the assignment of features to various iterations or periods
 * during planning of the delivery of a software system. It also stores the 
 * corresponding objective values of the plan
 *
 */
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
	
	public Plan(int size){
		this.chromosome = new ArrayList<Integer>(Collections.nCopies(size, 0));
		this.expectedCost = 0;
		this.expectedNPV = 0;
		this.expectedROI = 0;
		this.investmentRisk = 0;
		this.domSet = new ArrayList<Plan>();
		this.featureVector = Project.getFeatures();
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
	
	/**
	 * return the value of the investment risk
	 * @return
	 */
	public double getInvestmentRisk() {
		return investmentRisk;
	}
	
	/**
	 * get the value of the return on investment
	 * @return
	 */
	public double getExpectedROI() {
		return expectedROI;
	}
	/**
	 * set the value of the ROI
	 * @param expectedROI
	 */
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
	
	/**Compute the objective values for the three objectives
	 * 
	 * @param project reference to the current project
	 */
	public void evaluateFitness(){
//		System.out.println("Enter Fitness");
		Double[] npv = new Double[Project.nOfSim];
		Double[] cost = new Double[Project.nOfSim];
		HashMap<Integer, String> plan = this.transformPlan();
						
		for (int k = 0; k < Project.nOfSim; k++){
			npv[k] = cost[k] = 0.0;
			List<String> executedFeatures = new ArrayList<String>();
			for (Map.Entry<Integer, String> entry : plan.entrySet()){
				int currentPeriod = entry.getKey();
				double periodRevenue = 0;
				double periodCost = 0;
				double periodInvestment = 0 ;
				if (currentPeriod == 0)
					continue;
				String[] features = entry.getValue().split(",");
				//get investment cost
				if (!executedFeatures.isEmpty()){
					for (String deliveredFeature : executedFeatures){
						Double value = Project.getMmfs().get(deliveredFeature).getValueDistribution().getValue_sim()[k][currentPeriod-1];
					//	Double value = Project.getSimCashflow().get(deliveredFeature)[k][currentPeriod-1];
						periodRevenue += getDiscountedValue(Project.getInterestRate(), currentPeriod, value);
					}
				}
				for (String feature : features){
					periodInvestment += getDiscountedValue(Project.getInterestRate(), currentPeriod, 
								Project.getMmfs().get(feature).getCostDistribution().getCost_sim()[k]);
					npv[k] += Project.getSimSanpv().get(feature)[k][currentPeriod-1];
					executedFeatures.add(feature);
				}
				periodInvestment -= periodRevenue;
				periodCost += (periodInvestment > 0) ? periodInvestment: 0;
				if (Math.abs(periodCost) > 800){
					expectedCost = Double.NEGATIVE_INFINITY;
					expectedNPV = Double.NEGATIVE_INFINITY;
					investmentRisk = Double.POSITIVE_INFINITY;
					return;
				}
				//value of the feature
				cost[k] += periodCost;
				
				
			}
		}
		//Compute expected npv, expected cost and investment risk
		expectedValue(cost, npv);
	}
	
	public void expectedValue(Double[] cost, Double[] value){
		DescriptiveStatistics statsCost = new DescriptiveStatistics();
		DescriptiveStatistics statsNpv = new DescriptiveStatistics();

		
		for (int i = 0; i < Project.nOfSim; i++){
			statsNpv.addValue(value[i]);
			statsCost.addValue(cost[i]);
		}
		
		expectedCost = statsCost.getMean();
		minCost = statsCost.getMin();
		maxCost = statsCost.getMax();
		expectedNPV = statsNpv.getMean();
		minNPV = statsNpv.getMin();
		maxNPV = statsNpv.getMax();
		double npvSD = statsNpv.getStandardDeviation();
		if (npvSD == 0){
			investmentRisk = 0;
		}
		else{
			investmentRisk = Math.abs(npvSD / expectedNPV);
		}
		expectedROI = (expectedNPV / Math.abs(expectedCost)) * 100;
	}
	
	/**
	 * Randomly generate new plan based on the features in project
	 * and precedence constraints
	 * @param project
	 */
	public void generatePlan(){	
		int noOfFeatures = featureVector.size();
		int startedFeatures = 0;
		int unstartedFeatures = noOfFeatures;
		List<Integer> indexAdded = new ArrayList<Integer>();
		int period = 0;
		while(period < Project.getPeriods() && startedFeatures < noOfFeatures){
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
	
	/**
	 * Converts the encoding of the plan to readable map assignment
	 * @return
	 */
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
	
	/**
	 * checks the validity of a plan
	 * @param project
	 * @return
	 */
	public boolean isValidPlan1(){
		boolean isValid = true;
		
		//for each feature in featurevector
		for (int i = 0; i < chromosome.size(); i++){
			String featureId = featureVector.get(i);
			String precursor = Project.getMmfs().get(featureId).getPrecursorString();
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
	
	public boolean isValidPlan2(){
		boolean isValid = true;
		
		//for each feature in featurevector
		for (int i = 0; i < chromosome.size(); i++){
			if (chromosome.get(i) == 0){
				continue;
			}
			String featureId = featureVector.get(i);
			String precursor = Project.getMmfs().get(featureId).getPrecursorString();
			if (precursor == ""){
				continue;
			}
			int precursorIndex = featureVector.indexOf(precursor);			
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
	
	public boolean isValidPlan(){
		boolean isValid = true;
		
		//for each feature in featurevector
		for (int i = 0; i < chromosome.size(); i++){
			if (chromosome.get(i) == 0){
				continue;
			}
			String featureId = featureVector.get(i);
			for (Feature feature : Project.getMmfs().get(featureId).getPrecursors()){
				String precursor = feature.getId();
				if (precursor == ""){
					continue;
				}
				int precursorIndex = featureVector.indexOf(precursor);			
				if (chromosome.get(i) < chromosome.get(precursorIndex)){
					return false;
				}
				else if (chromosome.get(i) == chromosome.get(precursorIndex)){
					if (chromosome.get(i) != 0)
						return false;
				}
			}
		}
		// if 
		return isValid;
	}
	
	/**
	 * Converts the encoding of the plan to a string
	 */
	public String toString(){
		String newString = "";
		for (Integer s : chromosome){
			newString += s;
		}
		return newString;
	}
	
	/**
	 * discount a value based on the current period and interest rate
	 * @param interestRate
	 * @param period
	 * @param value
	 * @return
	 */
	public Double getDiscountedValue(double interestRate, int period, Double value){
    	if (period < 1) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + period);
        }  	
    	return value / Math.pow(interestRate + 1, period);
    }
	
	/**
	 * generate a cash flow analysis table for the plan
	 * @param plan
	 * @return
	 */
	public Double[][] cashFlowAnalysis(HashMap<Integer, String> plan){
		Double [][] cFlow = new Double[featureVector.size()+2][Project.getPeriods()];
		int row = 0;
		DecimalFormat df = new DecimalFormat("#.##");
		for (Map.Entry<Integer, String> entry : plan.entrySet()){
			if (entry.getKey() == 0){
				String[] features = entry.getValue().split(",");
				for (@SuppressWarnings("unused") String feature : features){
					int column = 0;
					while (column < cFlow[row].length){
						cFlow[row][column] = 0.0;
						column++;
					}
					row++;
				}
			}
			else {
				String[] features = entry.getValue().split(",");
				for (String feature : features){
					int column = entry.getKey()-1;
					Double[] cf = new Double[Project.getPeriods()]; //Project.getSimAverage().get(feature);
					cf[0] = Project.getMmfs().get(feature).getCostDistribution().getAvg_sim();
					double[] value = Project.getMmfs().get(feature).getValueDistribution().getAvg_value(); 
					for (int k = 1; k < cf.length; k++){
						cf[k] = value[k-1];
					}
					int index = 0;
					while (column < cFlow[row].length){
						Double discValue = getDiscountedValue(Project.getInterestRate(), column+1, cf[index]);
						cFlow[row][column] = Double.valueOf(df.format(discValue));
						column++;
						index++;
					}
					row++;
				}
			}
		}
		for (int i = 0; i < Project.getPeriods(); i++){
			Double sum = 0.0;
			for (int j = 0; j < featureVector.size();j++){
				if (cFlow[j][i] != null){
					sum += cFlow[j][i];
				}
				else {
					cFlow[j][i] = 0.0;
				}
			}
			cFlow[featureVector.size()][i] = Double.valueOf(df.format(sum));
		}
		
		Double rollingNpv = 0.0;
		for (int i = 0; i < Project.getPeriods(); i++){
			rollingNpv += cFlow[featureVector.size()][i];
			cFlow[featureVector.size()+1][i] = Double.valueOf(df.format(rollingNpv));
		}
		
		
		return cFlow;
	}
	
}
