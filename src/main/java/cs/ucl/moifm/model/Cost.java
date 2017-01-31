package cs.ucl.moifm.model;

public class Cost {
	private double cost_amount;
	
	private double indus_under_estimation;
	
	private double indus_over_estimation;
	
	private Distribution distribution;
	
	private double[] cost_sim;
	
	private double avg_sim;
	
	public Cost(double amount, double over, double under){
		this.setCost_amount(amount);
		this.setIndus_over_estimation(over);
		this.setIndus_under_estimation(under);
		setDistribution(new TDistribution(cost_amount,cost_amount * indus_under_estimation, cost_amount * indus_over_estimation));
	}
	
	public Cost(double lower, double upper) throws Exception{
		setDistribution(new NormalCIDistribution(lower, upper));
	}
	
	public Cost(){}

	public double getCost_amount() {
		return cost_amount;
	}

	public void setCost_amount(double cost_amount) {
		this.cost_amount = cost_amount;
	}

	public double getIndus_under_estimation() {
		return indus_under_estimation;
	}

	public void setIndus_under_estimation(double indus_under_estimation) {
		this.indus_under_estimation = indus_under_estimation;
	}

	public double getIndus_over_estimation() {
		return indus_over_estimation;
	}

	public void setIndus_over_estimation(double indus_over_estimation) {
		this.indus_over_estimation = indus_over_estimation;
	}

	public Distribution getDistribution() {
		return distribution;
	}

	public void setDistribution(Distribution distribution) {
		this.distribution = distribution;
	}

	public double[] getCost_sim() {
		return cost_sim;
	}

	public void setCost_sim(double[] cost_sim) {
		this.cost_sim = cost_sim;
	}

	public double getAvg_sim() {
		return avg_sim;
	}

	public void setAvg_sim(double avg_sim) {
		this.avg_sim = avg_sim;
	}
}
