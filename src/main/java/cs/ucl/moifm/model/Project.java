package cs.ucl.moifm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Project{
	

    private static String name;
    private static int periods;
    private static double interestRate;
    private static LinkedHashMap<String, Feature> myFeatures;
    private static List<String> features;
    private static HashMap<String, Double[]> sanpv;
  //  private static HashMap<String, Double[][]> simCashflow;
    private static HashMap<String, Double[][]> simSanpv;
    private static HashMap<String, Double[]> simAverage;
    private static String nextId;
    private static int maxMmfsPerPeriod;
    private static List<String> strands;
    public static int nOfSim;
    
    private static double budgetConstraint;
    
    /**
     * Creates a new IFM Project with default values for all properties.
     */
    public Project() {
        Project.name = "New MMF Project";
        Project.periods = 12;
        Project.interestRate = 0.0241;
        Project.nextId = "A";
        Project.myFeatures = new LinkedHashMap<String, Feature>();
        Project.maxMmfsPerPeriod = 1;
        Project.budgetConstraint = 0.0;
        Project.sanpv = new HashMap<String, Double[]>();
        Project.nOfSim = 10000;
        Project.strands = new ArrayList<String>();
        Project.features = new ArrayList<String>();
    }
    
    public static String getName() {
        return name;
    }

    /**
     * Sets the project name and fires the event EVENT_NAME.
     */
    public static void setName(String name) {
        Project.name = name;
    }

    public static int getPeriods() {
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
    public static void setPeriods(int periods) throws FeatureException {
        if ((periods < 1) || (periods > 105)) {
            throw new FeatureException("Invalid number of periods: " + periods);
        }
        Project.periods = periods;
    }

    public static double getInterestRate() {
        return interestRate;
    }
    
    /**
     * Sets the interest rate per period and fires the event
     * EVENT_INTEREST_RATE. The value should be absolute, not in percent. (i.e.
     * 12% = 0.12)
     */
    public static void setInterestRate(double interestRate) {
        Project.interestRate = interestRate;
    }
    
    /**
     * @return the maxMMFsPerPeriod
     */
    public static int getMaxMmfsPerPeriod() {
        return maxMmfsPerPeriod;

    }
    
    /**
     * @param maxMmfsPerPeriod the maxMMFsPerPeriod to set
     * @throws MmfException
     */
    public static void setMaxMmfsPerPeriod(int maxMmfsPerPeriod) throws FeatureException {
        if (maxMmfsPerPeriod < 0) {
            throw new FeatureException("Invalid maxMMFsPerPeriod: "
                    + maxMmfsPerPeriod);
        }
        Project.maxMmfsPerPeriod = maxMmfsPerPeriod;
    }
    
    /**
     * Adds the mmf to the list of mmfs and fires the EVENT_MMFS event.
     *
     * If the MMF contains an invalid or duplicate id it will be reassigned a
     * new id.
     */
    public static void add(Feature mmf) {
        if (myFeatures.containsKey(mmf.getId())) {
            throw new IllegalArgumentException("This MMF already exists: "
                    + mmf);
        }

        if (!isValidId(mmf.getId())) {
            try {
                mmf.setId(getNextId());
            } catch (Exception e) {
                // Should never happend so we rethrow as a RuntimeException
                throw new IllegalArgumentException(
                        "isValidId() != isValidId()", e);
            }
        }
   //     mmf.setProject(this);
        myFeatures.put(mmf.getId(), mmf);
    }
    
    public static void setNextId(String nextId) {
        Project.nextId = nextId;
    }
    
    /**
     * Checks if the given id is valid and has no duplicates in the current
     * project
     *
     * @return true of valid, false if not or a duplicate exists
     */
    public static boolean isValidId(String id) {
        return (null != id) && id.matches("Z*[A-Y]*[1-9]*") && (null == get(id));
    }
    
    /**
     * @return the id that should be used for the next MMF that is added. The
     *         value is not increased until the next MMF is actually added.
     */
    public static String getNextId() {
        // check if next id is correct.
        while (!isValidId(nextId)) {
            // find next id value
            char nextChar = (char) (1 + nextId.charAt(nextId.length() - 1));
            String pre = nextId.substring(0, nextId.length() - 1);
            nextId = pre + nextChar;
            if (nextChar == 'Z') {
                // We're at the last usable character in this set. We retry all
                // previous characters
                // in an attempt to avoid multiple characters, otherwise we add
                // another 'A' character
                for (int i = 0; i < 25 * 10 + 1; i++) {
                    nextId = "ZZZZZZZZZZ".substring(0, i / 25)
                            + (char) ('A' + i % 25);
                    if (isValidId(nextId)) {
                        return nextId;
                    }
                }
            }
        }
        return nextId;
    }

    public static Feature get(int index) {
        return myFeatures.get(index);
    }

    public static Feature get(String id) {
        
        return myFeatures.get(id);
    }

    /**
     * @return a unmodifiable copy of the mmf list
     */
    public static LinkedHashMap<String, Feature> getMmfs() {
        return Project.myFeatures;
    }
    
    /**
     * Removes the mmf from the list of mmfs and fires the EVENT_MMFS event.
     */
    public static void remove(Feature mmf) {
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
    public static int size() {
        return myFeatures.size();
    }
    
	public static double getBudgetConstraint() {
		return budgetConstraint;
	}

	public static void setBudgetConstraint(double budgetConstraint) {
		Project.budgetConstraint = budgetConstraint;
	}

	public static HashMap<String, Double[]> getSanpv() {
		return sanpv;
	}

//	public static void setSanpv() {
//		for (Map.Entry<String, Feature> value : myFeatures.entrySet()){
//			sanpv.put(value.getKey(), value.getValue().getSaNpvList(Project.interestRate));
//		}
//		
//	}

	/**
	 * @return the simCashflow
	 */
//	public static HashMap<String, Double[][]> getSimCashflow() {
//		return simCashflow;
//	}

	/**
	 * @param simCashflow the simCashflow to set
	 */
//	public static void setSimCashflow(HashMap<String, Double[][]> simCashflow) {
//		Project.simCashflow = simCashflow;
//	}

	/**
	 * @return the simSanpv
	 */
	public static HashMap<String, Double[][]> getSimSanpv() {
		return simSanpv;
	}

	/**
	 * @param simSanpv the simSanpv to set
	 */
	public static void setSimSanpv(HashMap<String, Double[][]> simSanpv) {
		Project.simSanpv = simSanpv;
	}

	/**
	 * @return the strands
	 */
	public static List<String> getStrands() {
		return strands;
	}

	/**
	 * @param strands the strands to set
	 */
//	public static void setStrands() {
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
	public static List<String> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public static void setFeatures() {
		for (String s : myFeatures.keySet()){
			features.add(s);
		}
	}

	public static HashMap<String, Double[]> getSimAverage() {
		return simAverage;
	}

	public static void setSimAverage(HashMap<String, Double[]> simAverage) {
		Project.simAverage = simAverage;
	}
	

}
