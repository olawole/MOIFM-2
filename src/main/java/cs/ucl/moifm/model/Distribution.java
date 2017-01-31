package cs.ucl.moifm.model;

public interface Distribution {
	double sample();
	double[] sample(int N);
}
