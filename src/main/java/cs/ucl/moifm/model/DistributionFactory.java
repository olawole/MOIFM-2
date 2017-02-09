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
		if (distributionType.equalsIgnoreCase("Normal")){
			return new NDistribution();
		}
		if (distributionType.equalsIgnoreCase("LogNormal")){
			return new LogNDistribution();
		}
		return null;
	}
}
