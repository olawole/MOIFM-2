package cs.ucl.moifm.util;


import java.util.HashMap;




import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.StatUtils;

import cs.ucl.moifm.model.FeatureException;
import cs.ucl.moifm.model.TDistribution;
import cs.ucl.moifm.model.Distribution;
import cs.ucl.moifm.model.Feature;



public class MCSimulation {
	//Number of simulation
	private static final int N = 10000;
		
	public static void costSim(LinkedHashMap<String, Feature> myFeatures) throws FeatureException{
		if(myFeatures.isEmpty()){
			throw new FeatureException("No Project data to simulate");
		}
		for(Entry<String, Feature> mmf : myFeatures.entrySet()){
			//Simulate cash distribution
			double avg = 0;
			Distribution cost = mmf.getValue().getCostDistribution().getDistribution();
			double[] costsim = new double[N];
			costsim = cost.sample(N);
			avg = StatUtils.mean(costsim);
			mmf.getValue().getCostDistribution().setAvg_sim(avg);
			mmf.getValue().getCostDistribution().setCost_sim(costsim);
		}
	//	Project.nOfSim = N;
	}
	
	public static void valueSim(LinkedHashMap<String, Feature> myFeatures, int period) throws FeatureException{
		if(myFeatures.isEmpty()){
			throw new FeatureException("No Project data to simulate");
		}
		for(Entry<String, Feature> mmf : myFeatures.entrySet()){
			//Simulate cash distribution
			if(mmf.getValue().getValueDistribution() != null){
				Distribution value = mmf.getValue().getValueDistribution().getValue();
				double[][] valuesim = new double[N][period];
				double[] avgsim = new double[period];
				for(int i = 0; i < period; i++){
					double sum = 0;
					for(int j = 0; j < N; j++){ //create samples without loop
						valuesim[j][i] = Math.abs(value.sample());
						sum += valuesim[j][i];
					}
					avgsim[i] = sum / N;
				}
				mmf.getValue().getValueDistribution().setAvg_value(avgsim);
				mmf.getValue().getValueDistribution().setValue_sim(valuesim);
			}
			else {
				double[] avgsim = new double[period];
				for(int i = 0; i < avgsim.length; i++)
					avgsim[i] = 0;
			}
		}
	}
	
	public static void simulate(LinkedHashMap<String, Feature> myFeatures, int period, double intRate) throws FeatureException{
		MCSimulation.costSim(myFeatures);
		MCSimulation.valueSim(myFeatures, period);
		//MCSimulation.calculate_sanpv();		
	}
	
	public static HashMap<String, Double[][]> calculate_sanpv(LinkedHashMap<String, Feature> myFeatures, int period, double intRate){
		
		HashMap<String, Double[][]> sanpv = new HashMap<String, Double[][]>();
		
		for(Entry<String, Feature> mmf : myFeatures.entrySet()){
			double[][] valuesim;
			Double[][] sanpvList = new Double[N][period];
			double[] costsim = mmf.getValue().getCostDistribution().getCost_sim();
			if (mmf.getValue().getValueDistribution() != null){
				valuesim = mmf.getValue().getValueDistribution().getValue_sim();
				for (int i = 0; i < N; i++){
					sanpvList[i] = getSaNpvList(costsim[i], valuesim[i], period, intRate);
				}
			}
			else {
				for (int i = 0; i < N; i++){
					sanpvList[i] = getSaNpvList(costsim[i], null, period, intRate);
				}
			}
			
			sanpv.put(mmf.getKey(), sanpvList);
		}
		return sanpv;
	}

	
	public static Double[] getSaNpvList(double cost, double[] value, int noPeriods, double intRate) {
        Double sanpv[] = new Double[noPeriods];
        if(value == null){
        	for (int p = 1; p <= noPeriods; p++) {
                sanpv[p-1] = -cost / Math.pow(1 + intRate, p);
            }
        	return sanpv;
        }
        for (int p = 1; p <= noPeriods; p++) {
            sanpv[p-1] = getSaNpv(p, value, intRate) - (cost / Math.pow(1 + intRate, p));
        }
        return sanpv;
    }
	
	public static double getSaNpv(int skipPeriods, double[] value, double intRate) {
        if (skipPeriods < 0) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + skipPeriods);
        }

        double npv = 0.0F;
        for (int p = 1; p <= value.length - skipPeriods; p++) {
            Double rev = value[p-1];
            int per = (skipPeriods + p);
            npv += rev / Math.pow(intRate + 1, per);
        }
        return npv;
    }

	
	
	
	
}
