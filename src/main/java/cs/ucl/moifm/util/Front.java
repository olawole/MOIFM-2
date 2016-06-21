package cs.ucl.moifm.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs.ucl.moifm.model.DeliverySequence;
import cs.ucl.moifm.model.Plan;

/**
 * 
 * @author Olawole Oni
 * This class implements dominated levels of Pareto set
 *
 */
public class Front {
	
	
	public List<Plan> members;
	private static final String[] OBJECTIVES = {"ENPV", "ECOST","RISK"};
	
	int rank;
	
	/**
	 * A constructor of the class Front
	 * @param rank Rank of the pareto front
	 */
	public Front(int rank){
		members = new ArrayList<Plan>();
		this.rank = rank;
	}
	
	/**
	 * Method to compute the crowding distance of solutions in the same front
	 */
	public void crowdingDistance() {
//		System.out.println("Enter Crowding");
		int l = members.size();
		for (Plan d : members){
			d.crowdingDistance = 0;
		}
		for (String obj : OBJECTIVES){
			switch (obj){
			case "ENPV": {
//				System.out.println("Enter ENPV");
				sortNpv();
//				sortNpv(0, members.size()-1);
//				System.out.println("After sorting");
				members.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
				members.get(l-1).crowdingDistance = Double.POSITIVE_INFINITY;
				Double min = members.get(l-1).getExpectedNPV();
				Double max = members.get(0).getExpectedNPV();
				for (int i = 1; i < l-1; i++){
					members.get(i).crowdingDistance += (members.get(i+1).getExpectedNPV() - members.get(i-1).getExpectedNPV()) / (max - min); 
				}
//				System.out.println("Exit ENPV");
				break;
				}
			case "ECOST": {
//				System.out.println("Enter ECOST");
//				sortCost(0, members.size()-1);
				sortCost();
//				System.out.println("After sorting");
				members.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
				members.get(l-1).crowdingDistance = Double.POSITIVE_INFINITY;
				Double min = members.get(l-1).getExpectedCost();
				Double max = members.get(0).getExpectedCost();
				for (int i = 1; i < l-1; i++){
					members.get(i).crowdingDistance += (members.get(i+1).getExpectedCost() - members.get(i-1).getExpectedCost()) / (max - min); 
				}
//				System.out.println("Exit ECOST");
				break;
				}
			case "RISK": {
//				System.out.println("Enter RISK");
//				sortRisk(0, members.size()-1);
				sortRisk();
//				System.out.println("After sorting");
				members.get(0).crowdingDistance = Double.POSITIVE_INFINITY;
				members.get(l-1).crowdingDistance = Double.POSITIVE_INFINITY;
				Double min = members.get(l-1).getInvestmentRisk();
				Double max = members.get(0).getInvestmentRisk();
				for (int i = 1; i < l-1; i++){
					members.get(i).crowdingDistance += (members.get(i+1).getInvestmentRisk() - members.get(i-1).getInvestmentRisk()) / (max - min); 
				}
//				System.out.println("Exit RISK");
				break;
				}
			default:;
			}
			
		}
//		System.out.println("Exit Crowding");
	}
	
	/**
	 * Sort the members of the front using the Investment risk objective
	 * @param lowerIndex index of the first element in the members list
	 * @param higherIndex index of the last element in the members list
	 */
	public void sortRisk(int lowerIndex, int higherIndex) {
		// TODO Auto-generated method stub
		int i = lowerIndex;
		int j = higherIndex;
		int pivotIndex = i + (j-i)/2;
		Double pivot = members.get(pivotIndex).getInvestmentRisk();
		while(i <= j){
			while(i >= 0 && members.get(i).getInvestmentRisk() < pivot) 
				i++;
			while(j >= 0 && members.get(j).getInvestmentRisk() > pivot) 
				j--;
			if (i >= 0 && j>= 0 && i <= j){
				Collections.swap(members, i, j);
				i++; j--;
			}
			if (lowerIndex < j)
				sortRisk(lowerIndex, j);
			if (i < higherIndex)
				sortRisk(i, higherIndex);
		}
	}
	
	public void sortRisk(){
		for (int i = 1; i < members.size(); i++){
			Double index = members.get(i).getInvestmentRisk();
			int j = i;
			while (j > 0 && members.get(j-1).getInvestmentRisk() < index){
				Collections.swap(members, j, j-1);
				j--;
			}
		}
	}
	
	/**
	 * Sort the members of the front using the expected cost objective
	 * @param lowerIndex index of the first element in the members list
	 * @param higherIndex index of the last element in the members list
	 */
	public void sortCost(int lowerIndex, int higherIndex) {
		// TODO Auto-generated method stub
		int i = lowerIndex;
		int j = higherIndex;
		int pivotIndex = i + (j-i)/2;
		Double pivot = members.get(pivotIndex).getExpectedCost();
		while(i <= j){
			while(i >= 0 && members.get(i).getExpectedCost() > pivot) 
				i++;
			while(j >= 0 && members.get(j).getExpectedCost() < pivot) 
				j--;
			if (i >= 0 && j>= 0 && i <= j){
				Collections.swap(members, i, j);
				i++; j--;
			}
			if (lowerIndex < j)
				sortCost(lowerIndex, j);
			if (i < higherIndex)
				sortCost(i, higherIndex);
		}
	}
	
	public void sortCost(){
		for (int i = 1; i < members.size(); i++){
			Double index = members.get(i).getExpectedCost();
			int j = i;
			while (j > 0 && members.get(j-1).getExpectedCost() < index){
				Collections.swap(members, j, j-1);
				j--;
			}
		}
	}
	/**
	 * Sort the members of the front using the Net Present Value objective
	 * @param lowerIndex index of the first element in the members list
	 * @param higherIndex index of the last element in the members list
	 */
	public void sortNpv(int lowerIndex, int higherIndex) {
		int i = lowerIndex;
		int j = higherIndex;
		int pivotIndex = i + (j-i)/2;
		Double pivot = members.get(pivotIndex).getExpectedNPV();
		while(i <= j){
			while(i >= 0 && members.get(i).getExpectedNPV() > pivot) 
				i++;
			while(j >= 0 && members.get(j).getExpectedNPV() < pivot) 
				j--;
			if (i >= 0 && j>= 0 && i <= j){
				Collections.swap(members, i, j);
				i++; j--;
			}
			if (lowerIndex < j)
				sortNpv(lowerIndex, j);
			if (i < higherIndex)
				sortNpv(i, higherIndex);
		}
	//	Collections.reverse(members);
	}
	
	public void sortNpv(){
		for (int i = 1; i < members.size(); i++){
			Double index = members.get(i).getExpectedNPV();
			int j = i;
			while (j > 0 && members.get(j-1).getExpectedNPV() < index){
				Collections.swap(members, j, j-1);
				j--;
			}
		}
	}
	
	public boolean crowdedComparison(DeliverySequence d1, DeliverySequence d2){
		
		if(d1.rank == d2.rank)
			return (d1.crowdingDistance > d2.crowdingDistance);
		else
			return (d1.rank < d2.rank);
	}
	
	/**
	 * Sort the members of the front using the crowding distance
	 * @param lowerIndex index of the first element in the members list
	 * @param higherIndex index of the last element in the members list
	 */
	public void sortByCrowding(int lowerIndex, int higherIndex){
//		System.out.println("Enter Sort Crowding");
		int i = lowerIndex;
		int j = higherIndex;
		int pivotIndex = (i + j)/2;
		Double pivot = members.get(pivotIndex).crowdingDistance;
		while(i <= j){
			while(i >= 0 && members.get(i).crowdingDistance > pivot) 
				i++;
			while(j >= 0 && members.get(j).crowdingDistance < pivot) 
				j--;
			if (i >= 0 && j>= 0 && i <= j){
				Collections.swap(members, i, j);
				i++; j--;
			}
			if (lowerIndex < j)
				sortByCrowding(lowerIndex, j);
			if (i < higherIndex)
				sortByCrowding(i, higherIndex);
		}
//		System.out.println("Exit Sort Crowding"); 
		//Collections.reverse(members);
	}
	
	public void sortByCrowding(){
//		System.out.println("Enter crowd sorting");
		for (int i = 1; i < members.size(); i++){
			Double index = members.get(i).crowdingDistance;
			int j = i;
			while (j > 0 && members.get(j-1).crowdingDistance < index){
				Collections.swap(members, j, j-1);
				j--;
			}
		}
//		System.out.println("Exit crowd sorting");
	}

}
