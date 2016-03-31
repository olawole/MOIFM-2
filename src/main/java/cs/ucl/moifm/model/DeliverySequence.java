package cs.ucl.moifm.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.*;
public class DeliverySequence {
	
	// A list of String containing MMF label
	private List<String> sequence;
	
	// cost of implementing a sequence
	private double expectedCost;
	
	// revenue generated from implementing a delivery sequence
	private double expectedNPV;
	
	// investment risk of the sequence
	private double investmentRisk;
	
	// internal rate of return of the delivery sequence
	private double irr;
	
	// Return on investment of the delivery sequence
	private double expectedROI;
	
	//Default constructor
	public DeliverySequence(){
		this.sequence = new ArrayList<String>();
		this.expectedCost = 0;
		this.expectedNPV = 0;
		this.investmentRisk = 0;
		this.irr = 0;
		this.expectedROI = 0;
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
	
	/**
     * Get the sequence ordering
     */
	public List<String> getSequence() {
		return sequence;
	}
	
	/**
     * Add MMF to the sequence list and generate a new sequence
     * 
     * @param project
     */
	public void setSequence(Project project){
		for (Entry<String, MMF> mmf : project.getMmfs().entrySet()){
			this.sequence.add(mmf.getKey());
		}
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
	public void setExpectedPresentValue(double expectedPresentValue) {
		this.expectedNPV = expectedPresentValue;
	}

	public double getInvestmentRisk() {
		return investmentRisk;
	}

	public void setInvestmentRisk(double investmentRisk) {
		this.investmentRisk = investmentRisk;
	}

	public double getIrr() {
		return irr;
	}

	public void setIrr(double irr) {
		this.irr = irr;
	}
	
	public double getExpectedROI() {
		return expectedROI;
	}

	public void setExpectedROI(double expectedROI) {
		this.expectedROI = expectedROI;
	}

	/**
     * Check whether the sequence does violate the precedence
     * relationship specified in the precedence graph. return 
     * true if it is valid and false otherwise
     * 
     * @param sequence
     */
	public boolean isValidSequence(Project project){
		int length = sequence.size();
		boolean valid = true;
		for (int i = 0; i < length; i++){
			List<MMF> precursors = project.getMmfs().get(sequence.get(i)).getPrecursors();
			if (precursors.size() == 0){
				continue;
			}
			else {
				for (MMF mmf : precursors){
					int current = sequence.indexOf(mmf.getId());
					if (current < 0 || i < current){
						return false;
					}
				}
			}
		}
		
		return valid;
		
	}
	
	/**
     * Determine the fitness values of the delivery sequence
     * 
     * @param project
     */
	public void setFitness(Project project){
		
		Double[] npv = new Double[project.nOfSim];
		Double[] cost = new Double[project.nOfSim];
		
		for (int k = 0; k < project.nOfSim; k++){
			int periodCount = 0; // number of development period exceeded
			npv[k] = cost[k] = 0.0;
			for (int i = 0; i < sequence.size(); i++){
				String currentMmf = sequence.get(i);
				int devPeriod = project.getMmfs().get(currentMmf).getDevPeriod();
				double periodCum = 0; //cumulative revenue generated in a development period
				if (periodCount != 0){
					int j = 1; 
					while (j <= periodCount){
						Double value = project.getSimCashflow().get(sequence.get(j-1))[k][periodCount];
						periodCum += getDiscountedValue(project.getInterestRate(), periodCount+1, value);
						//periodCum += project.getMmfs().get(sequence.get(j-1)).getDiscountedValue(project.getInterestRate(), periodCount+1);
						j += project.getMmfs().get(sequence.get(j-1)).getDevPeriod();
					}
				}
				// Get investment cost
				if (devPeriod == 1){
					Double value = project.getSimCashflow().get(currentMmf)[k][devPeriod-1];
					periodCum += getDiscountedValue(project.getInterestRate(), devPeriod, value);
					//periodCum += project.getMmfs().get(currentMmf).getDiscountedValue(project.getInterestRate(), devPeriod);		
					//expectedCost += (periodCum < 0) ? periodCum : 0;
					cost[k] += (periodCum < 0) ? periodCum : 0;
				}
				else {
					for (int j = 1; j <= devPeriod; j++){
						Double value = project.getSimCashflow().get(currentMmf)[k][j-1];
						periodCum += getDiscountedValue(project.getInterestRate(), j, value);
						//periodCum += project.getMmfs().get(currentMmf).getDiscountedValue(project.getInterestRate(), devPeriod);		
						cost[k] += (periodCum < 0) ? periodCum : 0;
					}
				}
				//Get Net present value
				Double[] temp = project.getSimSanpv().get(currentMmf)[k];
				//Double[] temp = project.getSanpv().get(currentMmf);
				double value = temp[periodCount];
				npv[k] += value;
				
				periodCount += devPeriod;
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
		expectedNPV = statsNpv.getMean();
		double npvSD = statsNpv.getStandardDeviation();
		investmentRisk = Math.abs(expectedNPV / npvSD);
		expectedROI = expectedNPV / expectedCost;
		
		System.out.println("Risk = " + investmentRisk);
		/*for (int i = 0; i < sequence.size(); i++){
			int devPeriod = project.getMmfs().get(sequence.get(i)).getDevPeriod();
			double periodCum = 0;
			if (periodCount != 0){
				int j = 1; 
				while (j <= periodCount){
					periodCum += project.getMmfs().get(sequence.get(j-1)).getDiscountedValue(project.getInterestRate(), periodCount+1);
					j += project.getMmfs().get(sequence.get(j-1)).getDevPeriod();
				}
			}
			// Get investment cost
			if (devPeriod == 1){
				periodCum += project.getMmfs().get(sequence.get(i)).getDiscountedValue(project.getInterestRate(), devPeriod);		
				expectedCost += (periodCum < 0) ? periodCum : 0;
			}
			else {
				for (int j = 1; j <= devPeriod; j++){
					periodCum += project.getMmfs().get(sequence.get(i)).getDiscountedValue(project.getInterestRate(), devPeriod);		
					expectedCost += (periodCum < 0) ? periodCum : 0;
				}
			}
			//Get Net present value
			Double[] temp = project.getSanpv().get(sequence.get(i));
			double value = temp[periodCount];
			expectedNPV += value;
			
			periodCount += devPeriod;
		}*/
	}
	
	
	
	public void generateIndividuals(Project project){	
		setSequence(project);
		Collections.shuffle(sequence);
	}
	
	public String toString(){
		String newString = "";
		for (String s : sequence){
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
}
