package cs.ucl.moifm.util;


import java.util.HashMap;




import java.util.List;
import java.util.Map.Entry;

import cs.ucl.moifm.model.FeatureException;
import cs.ucl.moifm.model.TDistribution;
import cs.ucl.moifm.model.Feature;
import cs.ucl.moifm.model.Project;

import org.apache.commons.math3.distribution.TriangularDistribution;



public class MCSimulation {
	
	//Number of simulation
	private static final int N = 1000;
	
//	private int period;
	
	private static Double[][] sim;
	
	private static Double[] avgSim;
	
//	public MCSimulation(int period){
//		
//		this.period = period;
//	}
	
	public static void costSim() throws FeatureException{
		if(Project.getMmfs().isEmpty()){
			throw new FeatureException("No Project data to simulate");
		}
		for(Entry<String, Feature> mmf : Project.getMmfs().entrySet()){
			//Simulate cash distribution
			double sum = 0;
			TDistribution cost = mmf.getValue().getCostDistribution().getDistribution();
			double[] costsim = new double[N];
			for(int i = 0; i < N; i++){
				costsim[i] = cost.sample();
				sum += costsim[i];
			}
			mmf.getValue().getCostDistribution().setAvg_sim(sum / N);
			mmf.getValue().getCostDistribution().setCost_sim(costsim);
		}
		Project.nOfSim = N;
	}
	
	public static void valueSim() throws FeatureException{
		if(Project.getMmfs().isEmpty()){
			throw new FeatureException("No Project data to simulate");
		}
		for(Entry<String, Feature> mmf : Project.getMmfs().entrySet()){
			//Simulate cash distribution
			if(mmf.getValue().getValueDistribution() != null){
				TDistribution[] value = mmf.getValue().getValueDistribution().getValue();
				double[][] valuesim = new double[N][Project.getPeriods()];
				double[] avgsim = new double[Project.getPeriods()];
				for(int i = 0; i < value.length; i++){
					double sum = 0;
					for(int j = 0; j < N; j++){
						valuesim[j][i] = value[i].sample();
						sum += valuesim[j][i];
					}
					avgsim[i] = sum / N;
				}
				mmf.getValue().getValueDistribution().setAvg_value(avgsim);
				mmf.getValue().getValueDistribution().setValue_sim(valuesim);
			}
			else {
				double[] avgsim = new double[Project.getPeriods()];
				for(int i = 0; i < avgsim.length; i++)
					avgsim[i] = 0;
			}
		}
	}
	
	public static void simulate() throws FeatureException{
		MCSimulation.costSim();
		MCSimulation.valueSim();
		MCSimulation.calculate_sanpv();
//		HashMap<String, Double[][]> scenario = new HashMap<String, Double[][]>();
//		HashMap<String, Double[]> average = new HashMap<String, Double[]>();
//		for(Entry<String, Feature> mmf : Project.getMmfs().entrySet()){
//			List<TDistribution> c = mmf.getValue().getCashvalue();
//			if(Project.getPeriods() != c.size()) return;
//			sim = new Double[N][Project.getPeriods()];
//			avgSim = new Double[Project.getPeriods()];
//			for (int i = 0; i < c.size(); i++){
//				Double sum = 0.0;
//				TriangularDistribution distribution = 
//						new TriangularDistribution(Math.abs(c.get(i).getLeast()), Math.abs(c.get(i).getMode()), Math.abs(c.get(i).getMost()));
//				for (int j = 0; j < N; j++){
//					
//					if (c.get(i).getLeast() < 0){
//						sim[j][i] = -(distribution.sample());
//					}
//					else {
//						sim[j][i] = distribution.sample();
//					}
//					sum += sim[j][i];
//				}
//				avgSim[i] = sum / N;
//				
//			}
//			
//			scenario.put(mmf.getValue().getId(), sim);
//			average.put(mmf.getValue().getId(), avgSim);
//		}
//		
//		Project.setSimCashflow(scenario);
//		Project.setSimAverage(average);
//		Project.nOfSim = N;
		
	}
	
	public static void calculate_sanpv(){
		
		HashMap<String, Double[][]> sanpv = new HashMap<String, Double[][]>();
		
		for(Entry<String, Feature> mmf : Project.getMmfs().entrySet()){
			double[][] valuesim = null;
			Double[][] sanpvList = new Double[N][Project.getPeriods()];
			double[] costsim = mmf.getValue().getCostDistribution().getCost_sim();
			if (mmf.getValue().getValueDistribution() != null){
				valuesim = mmf.getValue().getValueDistribution().getValue_sim();
			}			
			for (int i = 0; i < N; i++){
				sanpvList[i] = getSaNpvList(costsim[i], valuesim[i]);
			}
			sanpv.put(mmf.getKey(), sanpvList);
		}
		Project.setSimSanpv(sanpv);
	}
	
//	public static void simulate_sanpv(HashMap<String, Double[][]> scenarios, Project project){
//		
//		HashMap<String, Double[][]> sanpv = new HashMap<String, Double[][]>();
//		
//		
//		for(Entry<String, Double[][]> mmfScenario : scenarios.entrySet()){
//			Double[][] sanpvList = new Double[N][Project.getPeriods()];
//			String key = mmfScenario.getKey();
//			Double[][] values = mmfScenario.getValue();
//			
//			for (int i = 0; i < N; i++){
//				sanpvList[i] = getSaNpvList(Project.getInterestRate(), values[i]);
//			}
//			
//			sanpv.put(key, sanpvList);
//		}
//
//		
//		Project.setSimSanpv(sanpv);
//	}
	
	public static Double[] getSaNpvList(double cost, double[] value) {
        int periods = Project.getPeriods();
        Double sanpv[] = new Double[periods];
        if(value == null){
        	for (int p = 1; p <= periods; p++) {
                sanpv[p-1] = cost / Math.pow(Project.getInterestRate(), p);
            }
        }
        for (int p = 1; p <= periods; p++) {
            sanpv[p-1] = getSaNpv(p, value) - (cost / Math.pow(Project.getInterestRate(), p));
        }
        return sanpv;
    }
	
	public static double getSaNpv(int skipPeriods, double[] value) {
        if (skipPeriods < 0) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + skipPeriods);
        }

        double npv = 0.0F;
        for (int p = 1; p <= value.length - skipPeriods; p++) {
            Double rev = value[p-1];
            int per = (skipPeriods + p);
            npv += rev / Math.pow(Project.getInterestRate() + 1, per);
        }
        return npv;
    }
	
	
	
	/*
	 * Generate simulation scenarios
	 * 
	 * @param project
	 * 
	 */
	
	
	
	
	
}
