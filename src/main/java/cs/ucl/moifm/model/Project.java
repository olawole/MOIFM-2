package cs.ucl.moifm.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.opencsv.CSVReader;


public class Project{
	
    private  String name;
    private  int periods;
    private  double interestRate;
    private  LinkedHashMap<String, Feature> myFeatures;
    private  List<String> features;
    private  HashMap<String, Double[]> sanpv;
  //  private  HashMap<String, Double[][]> simCashflow;
    private  HashMap<String, Double[][]> simSanpv;
    private  HashMap<String, Double[]> simAverage;
   // private  String nextId;
    private  int maxMmfsPerPeriod;
    private  List<String> strands;
    public  int nOfSim;
    
    private  double budgetConstraint;
    
    /**
     * Creates a new IFM Project with default values for all properties.
     */
     public Project(){
        this.name = "New MMF Project";
        this.periods = 12;
        this.interestRate = 0.0241;
    //    this.nextId = "A";
        this.myFeatures = new LinkedHashMap<String, Feature>();
        this.maxMmfsPerPeriod = 1;
        this.budgetConstraint = 0.0;
        this.sanpv = new HashMap<String, Double[]>();
        this.nOfSim = 10000;
        this.strands = new ArrayList<String>();
        this.features = new ArrayList<String>();
    }
     
    public Project(String projName, int noOfPeriod, double intRate, String featurePath, String valuePath, String precPath) throws FileNotFoundException, IOException, FeatureException{
    	 this.name = projName;
         this.periods = noOfPeriod;
         this.interestRate = intRate;
         this.myFeatures = new LinkedHashMap<String, Feature>();
         //this.maxMmfsPerPeriod = 1;
        // this.budgetConstraint = 0.0;
         this.sanpv = new HashMap<String, Double[]>();
         this.nOfSim = 10000;
         this.strands = new ArrayList<String>();
         this.features = new ArrayList<String>();
         readFeatures(new CSVReader(new FileReader(featurePath)));
         readValues(new CSVReader(new FileReader(valuePath)));
         convertFileToPrecedence(new CSVReader(new FileReader(precPath)));
    }
    
    public  String getName() {
        return name;
    }

    /**
     * Sets the project name and fires the event EVENT_NAME.
     */
    public  void setName(String name) {
        this.name = name;
    }

    public  int getPeriods() {
        return periods;
    }
    
    /**
     * Sets the number of periods and fires the event EVENT_PERIODS. Must be
     * greater than 0. Will not make any changes to the revenue data of MMFs.
     * That means MMF will remember revenue data that is entered for periods
     * beyond this setting.
     *
     * @throws MmfException
     */
    public  void setPeriods(int periods) throws FeatureException {
        if ((periods < 1) || (periods > 105)) {
            throw new FeatureException("Invalid number of periods: " + periods);
        }
        this.periods = periods;
    }

    public  double getInterestRate() {
        return interestRate;
    }
    
    /**
     * Sets the interest rate per period and fires the event
     * EVENT_INTEREST_RATE. The value should be absolute, not in percent. (i.e.
     * 12% = 0.12)
     */
    public  void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    /**
     * @return the maxMMFsPerPeriod
     */
    public  int getMaxMmfsPerPeriod() {
        return maxMmfsPerPeriod;

    }
    
    /**
     * @param maxMmfsPerPeriod the maxMMFsPerPeriod to set
     * @throws MmfException
     */
    public  void setMaxMmfsPerPeriod(int maxMmfsPerPeriod) throws FeatureException {
        if (maxMmfsPerPeriod < 0) {
            throw new FeatureException("Invalid maxMMFsPerPeriod: "
                    + maxMmfsPerPeriod);
        }
        this.maxMmfsPerPeriod = maxMmfsPerPeriod;
    }
    
    /**
     * Adds the mmf to the list of mmfs and fires the EVENT_MMFS event.
     *
     * If the MMF contains an invalid or duplicate id it will be reassigned a
     * new id.
     */
    public  void add(Feature mmf) {
        if (myFeatures.containsKey(mmf.getId())) {
            throw new IllegalArgumentException("This MMF already exists: "
                    + mmf);
        }

//        if (!isValidId(mmf.getId())) {
//            try {
//                mmf.setId(getNextId());
//            } catch (Exception e) {
//                // Should never happend so we rethrow as a RuntimeException
//                throw new IllegalArgumentException(
//                        "isValidId() != isValidId()", e);
//            }
//        }
   //     mmf.setProject(this);
        myFeatures.put(mmf.getId(), mmf);
    }
    
//    public  void setNextId(String nextId) {
//        this.nextId = nextId;
//    }
    
    /**
     * Checks if the given id is valid and has no duplicates in the current
     * project
     *
     * @return true of valid, false if not or a duplicate exists
     */
    public  boolean isValidId(String id) {
        return (null != id) && id.matches("Z*[A-Y]*[1-9]*") && (null == get(id));
    }
    
    /**
     * @return the id that should be used for the next MMF that is added. The
     *         value is not increased until the next MMF is actually added.
     */
//    public  String getNextId() {
//        // check if next id is correct.
//        while (!isValidId(nextId)) {
//            // find next id value
//            char nextChar = (char) (1 + nextId.charAt(nextId.length() - 1));
//            String pre = nextId.substring(0, nextId.length() - 1);
//            nextId = pre + nextChar;
//            if (nextChar == 'Z') {
//                // We're at the last usable character in this set. We retry all
//                // previous characters
//                // in an attempt to avoid multiple characters, otherwise we add
//                // another 'A' character
//                for (int i = 0; i < 25 * 10 + 1; i++) {
//                    nextId = "ZZZZZZZZZZ".substring(0, i / 25)
//                            + (char) ('A' + i % 25);
//                    if (isValidId(nextId)) {
//                        return nextId;
//                    }
//                }
//            }
//        }
//        return nextId;
//    }

    public  Feature get(int index) {
        return myFeatures.get(index);
    }

    public  Feature get(String id) {
        
        return myFeatures.get(id);
    }

    /**
     * @return a unmodifiable copy of the mmf list
     */
    public  LinkedHashMap<String, Feature> getMmfs() {
        return this.myFeatures;
    }
    
    /**
     * Removes the mmf from the list of mmfs and fires the EVENT_MMFS event.
     */
    public  void remove(Feature mmf) {
        for (Entry<String, Feature> m : myFeatures.entrySet()) {
            if (m.getValue().getPrecursors().contains(mmf)) {
                m.getValue().removePrecursor(mmf);
            }
        }
        mmf.setProject(null);
        myFeatures.remove(mmf);
    }

    /**
     * @return the number of MMFs in the project
     */
    public  int size() {
        return myFeatures.size();
    }
    
	public  double getBudgetConstraint() {
		return budgetConstraint;
	}

	public  void setBudgetConstraint(double budgetConstraint) {
		this.budgetConstraint = budgetConstraint;
	}

	public  HashMap<String, Double[]> getSanpv() {
		return sanpv;
	}

//	public  void setSanpv() {
//		for (Map.Entry<String, Feature> value : myFeatures.entrySet()){
//			sanpv.put(value.getKey(), value.getValue().getSaNpvList(this.interestRate));
//		}
//		
//	}

	/**
	 * @return the simCashflow
	 */
//	public  HashMap<String, Double[][]> getSimCashflow() {
//		return simCashflow;
//	}

	/**
	 * @param simCashflow the simCashflow to set
	 */
//	public  void setSimCashflow(HashMap<String, Double[][]> simCashflow) {
//		this.simCashflow = simCashflow;
//	}

	/**
	 * @return the simSanpv
	 */
	public  HashMap<String, Double[][]> getSimSanpv() {
		return simSanpv;
	}

	/**
	 * @param simSanpv the simSanpv to set
	 */
	public  void setSimSanpv(HashMap<String, Double[][]> simSanpv) {
		this.simSanpv = simSanpv;
	}

	/**
	 * @return the strands
	 */
	public  List<String> getStrands() {
		return strands;
	}

	/**
	 * @param strands the strands to set
	 */
//	public  void setStrands() {
//		Set<String> strand = myFeatures.keySet();
//		Set<String> newStrand = new HashSet<String>(strand.size());
//		Set<String> temp = new HashSet<String>(strand.size());
//		do{
//			newStrand = new HashSet<String>(strand.size());
//			temp = strand;
//			for (String s: strand){
//				String index = s.substring(0, 1);
//				if (!myFeatures.get(index).getPrecursors().isEmpty()){
//					s = myFeatures.get(index).getPrecursors().get(0).getId() + s;
//				}
//				newStrand.add(s);
//			}
//			strand = newStrand;
//		} while(!temp.equals(newStrand));
//		for (String s: strand){
//			boolean exist = false;
//			for (String t : strand){
//				if (s.equals(t)) continue;
//				if (t.contains(s)){
//					exist = true;
//					break;
//				}
//			}
//			if (!exist){
//				strands.add(s);
//			}
//		}
//		for (Map.Entry<String, Feature> mmf : myFeatures.entrySet()){
//			for (String s : strands){
//				if (s.contains(mmf.getKey())){
//					mmf.getValue().setStrand(strands.indexOf(s)+1);
//					break;
//				}
//			}
//		}
//		
//	}

	/**
	 * @return the features
	 */
	public  List<String> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public  void setFeatures() {
		for (String s : myFeatures.keySet()){
			features.add(s);
		}
	}

	public  HashMap<String, Double[]> getSimAverage() {
		return simAverage;
	}

	public  void setSimAverage(HashMap<String, Double[]> simAverage) {
		this.simAverage = simAverage;
	}
	
	public  void readFeatures (CSVReader reader) throws IOException, FeatureException{
		String [] nextLine;
		reader.readNext();
	    while ((nextLine = reader.readNext()) != null) {
	    	String type = null, id = null;
	    	Double cost;
	    	String[] name = nextLine[0].split(" ");
	    	type = name[0];
	    	id = name[1];
	    	Feature mmf = new Feature(id, type);
	    	cost = Double.parseDouble(nextLine[1]);
	//    	growth = Double.parseDouble(nextLine[2]);
	    	Cost costD = new Cost(cost, 1.5, 1.2);
	    	mmf.setCostDistribution(costD);
	    	this.add(mmf);
	 //   	this.setPeriods(cashvalue.size());
	     }
	    this.setFeatures();	  	  
	}
	
	public void readValues (CSVReader reader) throws IOException, FeatureException{
		String[] line;
		reader.readNext();
		while ((line = reader.readNext()) != null){
			String id = line[0];
			Double value = Double.parseDouble(line[1]);
			Double growth = Double.parseDouble(line[2]);
			Value valueD = new Value(value, growth, this.getPeriods());
			if (this.getMmfs().get(id) != null){
				this.getMmfs().get(id).setValueDistribution(valueD);
			}
		}
	}
	
	public void convertFileToPrecedence(CSVReader predReader) throws IOException, FeatureException{
		String[] nextLine;
		
		while ((nextLine = predReader.readNext()) != null){
			if (this.getMmfs().containsKey(nextLine[0]))
				this.getMmfs().get(nextLine[0]).addPrecursor(this.getMmfs().get(nextLine[1]));
		}
		
	//	this.setStrands();
	}

}
