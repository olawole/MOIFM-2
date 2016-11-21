package cs.ucl.moifm.model;


/*
 * Describes a triangular distribution for the cash elements.
 */
public class TDistribution {
	
	/*
	 * least is the minimum value of the cash element
	 */
	private double least;
	
	/*
	 * most is the maximum value of the cash element
	 */
	private double most;
	
	/*
	 * mode is the most likely value of the cash element
	 */
	private double mode;
	
	public TDistribution(double least, double most, double mode){
		this.setLeast(least);
		this.setMost(most);
		this.setMode(mode);
	}
	
	public TDistribution(){
		this.setLeast(0);
		this.setMost(0);
		this.setMode(0);
	}

	public double getMode() {
		return mode;
	}

	public void setMode(double mode) {
		this.mode = mode;
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
