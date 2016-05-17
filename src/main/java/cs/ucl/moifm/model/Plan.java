package cs.ucl.moifm.model;

import java.util.ArrayList;
import java.util.List;

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
	
	int domCount;
	
	double minNPV, maxNPV, minCost, maxCost;
	
	List<Plan> domSet;
	
	int rank;
	
	double crowdingDistance;
	
	//Default Constructor
	
	public Plan(){
		this.chromosome = new ArrayList<Integer>();
		this.expectedCost = 0;
		this.expectedNPV = 0;
		this.expectedROI = 0;
		this.investmentRisk = 0;
		this.domSet = new ArrayList<Plan>();
	}
	
}
