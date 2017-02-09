package cs.ucl.moifm.model;

public class Value {
//	private static final double AVERAGE_UNDER_ESTIMATION = 1.2;
	
//	private double initial_value;
//	
//	private double growth_rate;
//	
//	private NormalDistribution valDistribution;
	
	private Distribution value;
	
	private double[][] value_sim;
	
	private double[] avg_value;
	
	
//	public Value (double value, double growthRate, int period){
//		this.valDistribution = new NormalDistribution();
//		initial_value = value;
//		growth_rate = growthRate;
//		valueDistribution(period);
//	}
	
	public Value (double least, double mode, double most){
		setValueDistribution(new TDistribution(least, mode, most));
	}
	
	public Value(double lower, double upper, String distType) throws Exception{
		if (distType.equalsIgnoreCase("NormalCI"))
			setValueDistribution(new NormalCIDistribution(lower, upper));
		else if (distType.equalsIgnoreCase("Normal"))
			setValueDistribution(new NDistribution(lower, upper));
		else;
	}
	
	public Value(){}
	
//	public double valueGrowth(int period){
//		if (period < 1){
//			return initial_value;
//		}
//		double period_rate = growth_rate * Math.abs(valDistribution.sample());
//		double periodValue;
//		periodValue = initial_value * Math.pow(1 + period_rate / 100, period);
//		return periodValue;
//	}
	
//	public void valueDistribution(int no_period){
//		value = new TDistribution[no_period];
//		double least = 0.0, most, mode;
//		for (int i = 0; i < no_period; i++){
//			mode = valueGrowth(i);
//			most = mode * AVERAGE_UNDER_ESTIMATION;
//			value[i] = new TDistribution(least, mode, most);
//		}
//	}

	public Distribution getValue() {
		return value;
	}

	public void setValueDistribution(Distribution value) {
		this.value = value;
	}

	public double[][] getValue_sim() {
		return value_sim;
	}

	public void setValue_sim(double[][] value_sim) {
		this.value_sim = value_sim;
	}

	public double[] getAvg_value() {
		return avg_value;
	}

	public void setAvg_value(double[] avg_value) {
		this.avg_value = avg_value;
	}
}
