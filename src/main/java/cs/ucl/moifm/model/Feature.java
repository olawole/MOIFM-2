/*
 * Copyright (C) 2016 Olawole Oni
 * 
 * 
 */


package cs.ucl.moifm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Feature {
//	public static final String EVENT_ID = "mmf.id";
//	public static final String EVENT_NAME = "mmf.name";
//	public static final String EVENT_NUMBER_OF_DEVELOPMENT_PERIOD = "mmf.devPeriod";
//	public static final String EVENT_PRECURSORS = "mmf.precursors";
//	public static final String EVENT_STRAND = "mmf.strand";
//	public static final String EVENT_CASHFLOW = "mmf.cashflow";
//	public static final String EVENT_PROJECT = "mmf.project";
	
	/**
     * The unique ID of the MMF. This is used to identify the MMF and displayed
     * in both the graph and table views.
     * 
     */
    private String id;
    
    /**
     * The name of the MMF. Should concisely describe what the MMF does.
     * 
     */
    private String name;
    
    /**
     * A period is the basic time measurement used in MMFs. A period could be a
     * week, a month, or any other period of time. This usually corresponds to
     * one or more development iteration(s), so that there is enough time to
     * finish at least one MMF. The periods could also be used more loosely,
     * simply identifying the approximate order of work.
     * <p>
     * This value assigns the number of development periods required to develop
     * an MMF
     */
    private int devPeriod;
    
    /**
     * A feature can be a marketable feature (MMF) or an architectural element
     */
    private String type;
    
    /**
     * The view is divided into several strands from top to bottom. This
     * parameter tells which strand the MMF will be drawn in.
     * 
     */
    private int strand;
    
    /**
     * List of precursors by id string. These are MMFs that have to be completed
     * before this MMF. Usually the MMF is positioned in a
     * period after all precursors are completed.
     */
    private List<Feature> precursors;
    
    /**
     * ArrayList of integers representing the cashflow for each period.
     */
    private Cost costDistribution;
    
    private Value valueDistribution;
    
    /**
     * The project this MMF belongs to.
     */
    private Project project;
    
    /**
     * Creates a new MMF with the given id and name.
     * 
     * @param id
     * @param name
     */
    public Feature(String id, String type) {
        this.id = id;
        this.type = type;
        this.name = "";
        this.devPeriod = 1;
        this.precursors = new ArrayList<Feature>();
        this.setCostDistribution(new Cost());
        if (type == "MMF"){
        	this.setValueDistribution(new Value());
        }
        else {
        	this.setValueDistribution(null);
        }
        
        
    }
    
    /**
     * Returns a string representation of this MMF.
     */
    @Override
    public String toString() {
        return type + id + ": " + name + " [" + devPeriod + "," + strand
                + "] > " + precursors;
    }
    
    public String getId() {
        return id;
    }
    
    /**
     * Sets the id and fires an EVENT_ID event. The id must a valid id, and not
     * have duplicates in the project.
     * 
     * @param id
     * @throws MmfException
     */
    public void setId(String id) throws FeatureException {
        if ((null != project) && !project.isValidId(id)) {
            throw new FeatureException("The id is not valid or has a duplicate: "
                    + id);
        }
        this.id = id;
    }
    
    /**
     * Sets the name and fires an EVENT_NAME event
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public int getDevPeriod() {
        return devPeriod;
    }
    
    /**
     * Sets the period and fires an EVENT_PERIOD event
     * 
     * 
     * @throws MmfException
     */
//    public void setDevPeriod() throws FeatureException {
//     /*   if (period < 1) {
//            throw new MMFException("Invalid period: " + period);
//        }
//        if (period == this.devPeriod) {
//            return;
//        }
//
//         */
//    	int period = 0;
//    	if (this.cashflow == null){
//    		throw new FeatureException("No cash flow for the specified MMF");
//    	}
//    	else {
//    		for (Double cash : this.cashflow){
//    			if (cash < 0) ++period;
//    		}
//            this.devPeriod = period;
//    	}
//    }
    
    public List<Feature> getPrecursors() {
        return Collections.unmodifiableList(precursors);
    }
    
    /**
     * @return all precursors as a comma-separated string of ids.
     */
    public String getPrecursorString() {
        if (precursors.size() == 0) {
            return "";
        } else {
            String result = "";
            for (Feature mmfPre : precursors) {
                result += ", " + mmfPre.getId();
            }
            return result.substring(2);
        }
    }
    
    /**
     * Sets all the precurors
     * 
     * @param prestring
     */
    public void setPrecursorString(String prestring) throws FeatureException {
        List<Feature> newPrecursors = new ArrayList<Feature>();

        Pattern pattern = Pattern.compile("Z*[A-Y]");
        Matcher matcher = pattern.matcher(prestring.toUpperCase());

        // check validity of all new precursors
        while (matcher.find()) {
            Feature preMmf = project.get(matcher.group());
            if (newPrecursors.contains(preMmf)) {
                continue;
            }
            checkValidPrecursor(preMmf);
            newPrecursors.add(preMmf);
        }

        // replace existing list
        this.precursors = newPrecursors;
    }
    
    /**
     * Adds a precursor and fires an EVENT_PRECURSORS event. Will cause an
     * exception if a circle of precedence will be created.
     * 
     * @param precursor
     * @throws MmfException
     */
    public void addPrecursor(Feature precursor) throws FeatureException {
        if (this.precursors.indexOf(precursor) < 0) {
            checkValidPrecursor(precursor);
            this.precursors.add(precursor);
        }
    } 
    
   /* public void addPrecursor(MMF precursor) throws MMFException {
        if (this.precursors.indexOf(precursor) < 0) {
        	for (MMF p : precursor.getPrecursors()){
        		this.addPrecursor(p);
        	}
            checkValidPrecursor(precursor);
            this.precursors.add(precursor);
            changeSupport.firePropertyChange(EVENT_PRECURSORS, null, precursor);
        }
    }*/
    /**
     * Checks if the precursor is valid. Mostly that no circular precursors
     * exists.
     * 
     * @param precursor
     * @throws MmfException
     */
    private void checkValidPrecursor(Feature precursor) throws FeatureException {
        if (null == precursor) {
            throw new FeatureException("Precursor does not exist");
        } else if (this.getProject() != precursor.getProject()) {
            throw new FeatureException(
                    "Precursor is not a part of the same project");
        } else if (this == precursor) {
            throw new FeatureException(
                    "MMF can not be a precursor to itself (circular precedence)");
        }
        List<Feature> prePre = precursor.getPrecursors();
        for (Feature pre : prePre) {
            checkValidPrecursor(pre);
        }
    }
    
    /**
     * Remove a precursor and fires an EVENT_PRECURSORS event.
     * 
     * @param precursor
     */
    public void removePrecursor(Feature precursor) {
        if (this.precursors.indexOf(precursor) >= 0) {
            this.precursors.remove(precursor);
        }
    }
    

	

	/**
     * Sets the revenue for the given period. Can be both positive and negative,
     * and can be for a period beyond what is the periodCount in the project.
     * 
     * @param period
     *            to set revenue for
     * @param revenue
     *            value of revenue in this period
     */
//    public void setRevenue(int period, int revenue) {
//        while (period > cashflow.size()) {
//            cashflow.add((double) 0);
//        }
//        this.cashflow.set(period - 1, (double) revenue);
//    }
//    
//    public Double getRevenue(int period) {
//        if (period > cashflow.size()) {
//            return (double) 0;
//        }
//        return cashflow.get(period - 1);
//    }
//    
//    public int getRevenueLength() {
//        return cashflow.size();
//    }
    
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project for this MMF and fires an EVENT_PROJECT event.
     * 
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }
    
    /**
     * Returns a list of SANPV for this project for each start period. This
     * function will call getSaNpv(double, int) for each possible start period.
     * 
     * @param interestRate
     */
//    public Double[] getSaNpvList(double interestRate) {
//        int periods = project.getPeriods();
//        Double sanpv[] = new Double[periods];
//        for (int p = 0; p < periods; p++) {
//            sanpv[p] = getSaNpv(interestRate, p);
//        }
//        return sanpv;
//    }

    /**
     * Returns the SANPV for the given start period and interest rate.
     * 
     * @param interestRate
     * @param skipPeriods
     * @throws MmfException
     */
//    public double getSaNpv(double interestRate, int skipPeriods) {
//        if (skipPeriods < 0) {
//            throw new IllegalArgumentException("Invalid startPeriod: "
//                    + skipPeriods);
//        }
//
//        double npv = 0.0F;
//        for (int p = 1; p <= project.getPeriods() - skipPeriods; p++) {
//            Double rev = getRevenue(p);
//            int per = (skipPeriods + p);
//            npv += rev / Math.pow(interestRate + 1, per);
//        }
//        return npv;
//    }
//    
//    public double getDiscountedValue(double interestRate, int period){
//    	if (period < 1 || period >= project.getPeriods()) {
//            throw new IllegalArgumentException("Invalid startPeriod: "
//                    + period);
//        }
//    	
//    	double discountValue = 0.0;
//    	discountValue = getRevenue(period) / Math.pow(interestRate + 1, period);
//    	
//    	return discountValue;
//    }
    
    public int getStrand(){
    	return this.strand;
    }
    
    public void setStrand(int strand){
    	this.strand = strand;
    }

	public Cost getCostDistribution() {
		return costDistribution;
	}

	public void setCostDistribution(Cost costDistribution) {
		this.costDistribution = costDistribution;
	}

	public Value getValueDistribution() {
		return valueDistribution;
	}

	public void setValueDistribution(Value valueDistribution) {
		this.valueDistribution = valueDistribution;
	}
    
    
    
    
}
