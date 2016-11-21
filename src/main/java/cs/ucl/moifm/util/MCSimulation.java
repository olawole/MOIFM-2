package cs.ucl.moifm.util;


import java.util.HashMap;




import java.util.List;
import java.util.Map.Entry;

import cs.ucl.moifm.model.TDistribution;
import cs.ucl.moifm.model.MMF;
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
	
	public static void simulate(Project project){
		HashMap<String, Double[][]> scenario = new HashMap<String, Double[][]>();
		HashMap<String, Double[]> average = new HashMap<String, Double[]>();
		for(Entry<String, MMF> mmf : project.getMmfs().entrySet()){
			List<TDistribution> c = mmf.getValue().getCashvalue();
			if(project.getPeriods() != c.size()) return;
			sim = new Double[N][project.getPeriods()];
			avgSim = new Double[project.getPeriods()];
			for (int i = 0; i < c.size(); i++){
				Double sum = 0.0;
				TriangularDistribution distribution = 
						new TriangularDistribution(Math.abs(c.get(i).getLeast()), Math.abs(c.get(i).getMode()), Math.abs(c.get(i).getMost()));
				for (int j = 0; j < N; j++){
					
					if (c.get(i).getLeast() < 0){
						sim[j][i] = -(distribution.sample());
					}
					else {
						sim[j][i] = distribution.sample();
					}
					sum += sim[j][i];
				}
				avgSim[i] = sum / N;
				
			}
			
			scenario.put(mmf.getValue().getId(), sim);
			average.put(mmf.getValue().getId(), avgSim);
		}
		
		project.setSimCashflow(scenario);
		project.setSimAverage(average);
		project.nOfSim = N;
		
	}
	
	public static void simulate_sanpv(HashMap<String, Double[][]> scenarios, Project project){
		
		HashMap<String, Double[][]> sanpv = new HashMap<String, Double[][]>();
		
		
		for(Entry<String, Double[][]> mmfScenario : scenarios.entrySet()){
			Double[][] sanpvList = new Double[N][project.getPeriods()];
			String key = mmfScenario.getKey();
			Double[][] values = mmfScenario.getValue();
			
			for (int i = 0; i < N; i++){
				sanpvList[i] = getSaNpvList(project.getInterestRate(), values[i]);
			}
			
			sanpv.put(key, sanpvList);
		}

		
		project.setSimSanpv(sanpv);
	}
	
	public static Double[] getSaNpvList(double interestRate, Double[] value) {
        int periods = value.length;
        Double sanpv[] = new Double[periods];
        for (int p = 0; p < periods; p++) {
            sanpv[p] = getSaNpv(interestRate, p, value);
        }
        return sanpv;
    }
	
	public static double getSaNpv(double interestRate, int skipPeriods, Double[] value) {
        if (skipPeriods < 0) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + skipPeriods);
        }

        double npv = 0.0F;
        for (int p = 1; p <= value.length - skipPeriods; p++) {
            Double rev = value[p-1];
            int per = (skipPeriods + p);
            npv += rev / Math.pow(interestRate + 1, per);
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
