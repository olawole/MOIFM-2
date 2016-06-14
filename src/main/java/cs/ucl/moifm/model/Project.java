package cs.ucl.moifm.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class Project implements PropertyChangeListener {
	public static final String EVENT_NAME = "project.name";
    public static final String EVENT_PERIODS = "project.periods";
    public static final String EVENT_INTEREST_RATE = "project.interestRate";
    public static final String EVENT_MMFS = "project.mmfs";
    public static final String EVENT_MAX_MMFS = "project.maxMMFs";

    private String name;
    private int periods;
    private double interestRate;
    private LinkedHashMap<String, MMF> mmfs;
    private List<String> features;
    private HashMap<String, Double[]> sanpv;
    private HashMap<String, Double[][]> simCashflow;
    private HashMap<String, Double[][]> simSanpv;
    private HashMap<String, Double[]> simAverage;
   // private List<MMF> mmfs;
    private String nextId;
    private int maxMmfsPerPeriod;
    private List<String> strands;
    public int nOfSim;
    
    private double budgetConstraint;
    private PropertyChangeSupport changeSupport;
    
    /**
     * Creates a new IFM Project with default values for all properties.
     */
    public Project() {
        this.name = "New MMF Project";
        this.periods = 12;
        this.interestRate = 0.0241;
        this.nextId = "A";
        this.mmfs = new LinkedHashMap<String, MMF>();
        this.maxMmfsPerPeriod = 1;
        this.budgetConstraint = 0.0;
        this.sanpv = new HashMap<String, Double[]>();
        this.changeSupport = new PropertyChangeSupport(this);
        this.nOfSim = 10000;
        this.strands = new ArrayList<String>();
        this.features = new ArrayList<String>();
    }
    
    public String getName() {
        return name;
    }

    /**
     * Sets the project name and fires the event EVENT_NAME.
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        changeSupport.firePropertyChange(EVENT_NAME, oldValue, name);
    }

    public int getPeriods() {
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
    public void setPeriods(int periods) throws MMFException {
        if ((periods < 1) || (periods > 105)) {
            throw new MMFException("Invalid number of periods: " + periods);
        }
        int oldValue = this.periods;
        this.periods = periods;
        changeSupport.firePropertyChange(EVENT_PERIODS, oldValue, periods);
    }

    public double getInterestRate() {
        return interestRate;
    }
    
    /**
     * Sets the interest rate per period and fires the event
     * EVENT_INTEREST_RATE. The value should be absolute, not in percent. (i.e.
     * 12% = 0.12)
     */
    public void setInterestRate(double interestRate) {
        double oldValue = this.interestRate;
        this.interestRate = interestRate;
        changeSupport.firePropertyChange(EVENT_INTEREST_RATE, oldValue,
                interestRate);
    }
    
    /**
     * @return the maxMMFsPerPeriod
     */
    public int getMaxMmfsPerPeriod() {
        return maxMmfsPerPeriod;

    }
    
    /**
     * @param maxMmfsPerPeriod the maxMMFsPerPeriod to set
     * @throws MmfException
     */
    public void setMaxMmfsPerPeriod(int maxMmfsPerPeriod) throws MMFException {
        if (maxMmfsPerPeriod < 0) {
            throw new MMFException("Invalid maxMMFsPerPeriod: "
                    + maxMmfsPerPeriod);
        }
        int oldValue = this.maxMmfsPerPeriod;
        this.maxMmfsPerPeriod = maxMmfsPerPeriod;
        changeSupport.firePropertyChange(EVENT_MAX_MMFS, oldValue,
                maxMmfsPerPeriod);
    }
    
    /**
     * Adds the mmf to the list of mmfs and fires the EVENT_MMFS event.
     *
     * If the MMF contains an invalid or duplicate id it will be reassigned a
     * new id.
     */
    public void add(MMF mmf) {
        if (mmfs.containsKey(mmf.getId())) {
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
        mmf.setProject(this);
        mmf.addPropertyChangeListener(this);
        mmfs.put(mmf.getId(), mmf);
        changeSupport.firePropertyChange(EVENT_MMFS, null, mmf);
    }
    
    public void setNextId(String nextId) {
        this.nextId = nextId;
    }
    
    /**
     * Checks if the given id is valid and has no duplicates in the current
     * project
     *
     * @return true of valid, false if not or a duplicate exists
     */
    public boolean isValidId(String id) {
        return (null != id) && id.matches("Z*[A-Y]*[1-9]*") && (null == get(id));
    }
    
    /**
     * @return the id that should be used for the next MMF that is added. The
     *         value is not increased until the next MMF is actually added.
     */
    public String getNextId() {
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

    public MMF get(int index) {
        return mmfs.get(index);
    }

    public MMF get(String id) {
        
        return mmfs.get(id);
    }

    /**
     * @return a unmodifiable copy of the mmf list
     */
    public LinkedHashMap<String, MMF> getMmfs() {
        return this.mmfs;
    }
    
    /**
     * Removes the mmf from the list of mmfs and fires the EVENT_MMFS event.
     */
    public void remove(MMF mmf) {
        mmf.removePropertyChangeListener(this);
        for (Entry<String, MMF> m : mmfs.entrySet()) {
            if (m.getValue().getPrecursors().contains(mmf)) {
                m.getValue().removePrecursor(mmf);
            }
        }
        mmf.setProject(null);
        mmfs.remove(mmf);
        changeSupport.firePropertyChange(EVENT_MMFS, mmf, null);
    }

    /**
     * @return the number of MMFs in the project
     */
    public int size() {
        return mmfs.size();
    }
    
    /**
     * Is called whenever there is a change in a child MMF or Category. Project
     * does not directly use this, but forwards all events to the
     * PropertyChangeListeners of this project.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        changeSupport.firePropertyChange(evt);
    }

    /**
     * Add a PropertyChangeListener to be notified of changes to this object or
     * child objects (MMFs and Categories)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

	public double getBudgetConstraint() {
		return budgetConstraint;
	}

	public void setBudgetConstraint(double budgetConstraint) {
		this.budgetConstraint = budgetConstraint;
	}

	public HashMap<String, Double[]> getSanpv() {
		return sanpv;
	}

	public void setSanpv() {
		for (Map.Entry<String, MMF> value : mmfs.entrySet()){
			sanpv.put(value.getKey(), value.getValue().getSaNpvList(this.interestRate));
		}
		
	}

	/**
	 * @return the simCashflow
	 */
	public HashMap<String, Double[][]> getSimCashflow() {
		return simCashflow;
	}

	/**
	 * @param simCashflow the simCashflow to set
	 */
	public void setSimCashflow(HashMap<String, Double[][]> simCashflow) {
		this.simCashflow = simCashflow;
	}

	/**
	 * @return the simSanpv
	 */
	public HashMap<String, Double[][]> getSimSanpv() {
		return simSanpv;
	}

	/**
	 * @param simSanpv the simSanpv to set
	 */
	public void setSimSanpv(HashMap<String, Double[][]> simSanpv) {
		this.simSanpv = simSanpv;
	}

	/**
	 * @return the strands
	 */
	public List<String> getStrands() {
		return strands;
	}

	/**
	 * @param strands the strands to set
	 */
	public void setStrands() {
		Set<String> strand = mmfs.keySet();
		Set<String> newStrand = new HashSet<String>(strand.size());
		Set<String> temp = new HashSet<String>(strand.size());
		do{
			newStrand = new HashSet<String>(strand.size());
			temp = strand;
			for (String s: strand){
				String index = s.substring(0, 1);
				if (!mmfs.get(index).getPrecursors().isEmpty()){
					s = mmfs.get(index).getPrecursors().get(0).getId() + s;
				}
				newStrand.add(s);
			}
			strand = newStrand;
		} while(!temp.equals(newStrand));
		for (String s: strand){
			boolean exist = false;
			for (String t : strand){
				if (s.equals(t)) continue;
				if (t.contains(s)){
					exist = true;
					break;
				}
			}
			if (!exist){
				strands.add(s);
			}
		}
		for (Map.Entry<String, MMF> mmf : mmfs.entrySet()){
			for (String s : strands){
				if (s.contains(mmf.getKey())){
					mmf.getValue().setStrand(strands.indexOf(s)+1);
					break;
				}
			}
		}
		
	}

	/**
	 * @return the features
	 */
	public List<String> getFeatures() {
		return features;
	}

	/**
	 * @param features the features to set
	 */
	public void setFeatures() {
		for (String s : mmfs.keySet()){
			features.add(s);
		}
	}

	public HashMap<String, Double[]> getSimAverage() {
		return simAverage;
	}

	public void setSimAverage(HashMap<String, Double[]> simAverage) {
		this.simAverage = simAverage;
	}
	

}
