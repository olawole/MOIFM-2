package cs.ucl.moifm.model;

import org.apache.commons.math3.distribution.TriangularDistribution;


/*
 * Describes a triangular distribution for the cash elements.
 */
public class TDistribution extends TriangularDistribution {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * least is the minimum value of the cash element
	 */
	private double least;
	
	/*
	 * most is the maximum value of the cash element
	 */
	private double most;
	
	
	public TDistribution(double least, double most, double mode){
		super(least, mode, most);
		this.setLeast(least);
		this.setMost(most);
	}
	

	public double getMode() {
		return super.getMode();
	}

	public double getMost() {
		return most;
	}

	public void setMost(double most) {
		this.most = most;
	}

	public double getLeast() {
		return least;
	}

	public void setLeast(double least) {
		this.least = least;
	}
	
	

}
