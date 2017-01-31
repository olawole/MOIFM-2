package cs.ucl.moifm.model;

public class DistributionFactory {
	public Distribution getDistribution(String distributionType){
		if (distributionType == null){
			return null;
		}
		if (distributionType.equalsIgnoreCase("Triangular")){
			return new TDistribution();
		}
		if (distributionType.equalsIgnoreCase("NormalCI")){
			return new NormalCIDistribution();
		}
		return null;
	}
}
