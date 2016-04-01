/*
 * Copyright (C) 2016 Olawole Oni
 * 
 * 
 */


package cs.ucl.moifm.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MMF {
	public static final String EVENT_ID = "mmf.id";
	public static final String EVENT_NAME = "mmf.name";
	public static final String EVENT_NUMBER_OF_DEVELOPMENT_PERIOD = "mmf.devPeriod";
	public static final String EVENT_PRECURSORS = "mmf.precursors";
	public static final String EVENT_STRAND = "mmf.strand";
	public static final String EVENT_CASHFLOW = "mmf.cashflow";
	public static final String EVENT_PROJECT = "mmf.project";
	
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
    private List<MMF> precursors;
    
    /**
     * ArrayList of integers representing the cashflow for each period.
     */
    private List<Double> cashflow;
    
    private List<CashDistribution> cashvalue;
    
    /**
     * The project this MMF belongs to.
     */
    private Project project;
    
    private PropertyChangeSupport changeSupport;
    
    
    /**
     * Creates a new MMF with the given id and name.
     * 
     * @param id
     * @param name
     */
    public MMF(String id) {
        this.id = id;
        this.name = "";
        this.devPeriod = 1;
        this.strand = 1;
        this.precursors = new ArrayList<MMF>();
        this.cashflow = new ArrayList<Double>();
        this.cashvalue = new ArrayList<CashDistribution>();
        this.changeSupport = new PropertyChangeSupport(this);
    }
    
    /**
     * Returns a string representation of this MMF.
     */
    @Override
    public String toString() {
        return "MMF " + id + ": " + name + " [" + devPeriod + "," + strand
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
    public void setId(String id) throws MMFException {
        if ((null != project) && !project.isValidId(id)) {
            throw new MMFException("The id is not valid or has a duplicate: "
                    + id);
        }
        String oldValue = this.id;
        this.id = id;
        changeSupport.firePropertyChange(EVENT_ID, oldValue, id);
    }
    
    /**
     * Sets the name and fires an EVENT_NAME event
     * 
     * @param name
     */
    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        changeSupport.firePropertyChange(EVENT_NAME, oldValue, name);
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
    public void setDevPeriod() throws MMFException {
     /*   if (period < 1) {
            throw new MMFException("Invalid period: " + period);
        }
        if (period == this.devPeriod) {
            return;
        }

         */
    	int period = 0;
    	if (this.cashflow == null){
    		throw new MMFException("No cash flow for the specified MMF");
    	}
    	else {
    		for (Double cash : this.cashflow){
    			if (cash < 0) ++period;
    		}
    		int oldValue = this.devPeriod;
            this.devPeriod = period;
            changeSupport.firePropertyChange(EVENT_NUMBER_OF_DEVELOPMENT_PERIOD, oldValue, period);
    	}
    }
    
    public List<MMF> getPrecursors() {
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
            for (MMF mmfPre : precursors) {
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
    public void setPrecursorString(String prestring) throws MMFException {
        List<MMF> newPrecursors = new ArrayList<MMF>();

        Pattern pattern = Pattern.compile("Z*[A-Y]");
        Matcher matcher = pattern.matcher(prestring.toUpperCase());

        // check validity of all new precursors
        while (matcher.find()) {
            MMF preMmf = project.get(matcher.group());
            if (newPrecursors.contains(preMmf)) {
                continue;
            }
            checkValidPrecursor(preMmf);
            newPrecursors.add(preMmf);
        }

        // replace existing list
        this.precursors = newPrecursors;
        changeSupport.firePropertyChange(EVENT_PRECURSORS, null, null);
    }
    
    /**
     * Adds a precursor and fires an EVENT_PRECURSORS event. Will cause an
     * exception if a circle of precedence will be created.
     * 
     * @param precursor
     * @throws MmfException
     */
    public void addPrecursor(MMF precursor) throws MMFException {
        if (this.precursors.indexOf(precursor) < 0) {
            checkValidPrecursor(precursor);
            this.precursors.add(precursor);
            changeSupport.firePropertyChange(EVENT_PRECURSORS, null, precursor);
        }
    }
    
    /**
     * Checks if the precursor is valid. Mostly that no circular precursors
     * exists.
     * 
     * @param precursor
     * @throws MmfException
     */
    private void checkValidPrecursor(MMF precursor) throws MMFException {
        if (null == precursor) {
            throw new MMFException("Precursor does not exist");
        } else if (this.getProject() != precursor.getProject()) {
            throw new MMFException(
                    "Precursor is not a part of the same project");
        } else if (this == precursor) {
            throw new MMFException(
                    "MMF can not be a precursor to itself (circular precedence)");
        }
        List<MMF> prePre = precursor.getPrecursors();
        for (MMF pre : prePre) {
            checkValidPrecursor(pre);
        }
    }
    
    /**
     * Remove a precursor and fires an EVENT_PRECURSORS event.
     * 
     * @param precursor
     */
    public void removePrecursor(MMF precursor) {
        if (this.precursors.indexOf(precursor) >= 0) {
            this.precursors.remove(precursor);
            changeSupport.firePropertyChange(EVENT_PRECURSORS, precursor, null);
        }
    }
    
    public List<Double> getCashFlow(){
    	return cashflow;
    }
    
    public void setCashFlow(List<Double> cash){
    	this.cashflow = cash;
    }
    public List<CashDistribution> getCashvalue() {
		if (cashvalue == null)
			setCashValue(cashflow);
    	return cashvalue;
	}

	public void setCashValue(List<Double> cash) {
		int i = 0;
		List<CashDistribution> cashvalue = new ArrayList<CashDistribution>();
		
		
		for (Double value : cash){
			CashDistribution buffer = new CashDistribution();
			if (value > 0){
				buffer.setLeast(0.0);
				buffer.setMost(1.2 * value);
				buffer.setMode(value);
				cashvalue.add(i++, buffer);
			}
			else if (value == 0){
				buffer.setLeast(0.0);
				buffer.setMost(value + 0.001);
				buffer.setMode(value + 0.0005);
				cashvalue.add(i++, buffer);
			}
			else {
				buffer.setLeast(value);
				buffer.setMost(1.5 * value);
				buffer.setMode(1.2 * value);
				cashvalue.add(i++, buffer);
			}
		}
		this.cashvalue = cashvalue;
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
    public void setRevenue(int period, int revenue) {
        while (period > cashflow.size()) {
            cashflow.add((double) 0);
        }
        Double oldValue = this.getRevenue(period);
        this.cashflow.set(period - 1, (double) revenue);
        changeSupport.firePropertyChange(EVENT_CASHFLOW, oldValue, revenue);
    }
    
    public Double getRevenue(int period) {
        if (period > cashflow.size()) {
            return (double) 0;
        }
        return cashflow.get(period - 1);
    }
    
    public int getRevenueLength() {
        return cashflow.size();
    }
    
    /**
     * Add a PropertyChangeListener to be notified of changes to this object
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
    
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project for this MMF and fires an EVENT_PROJECT event.
     * 
     * @param project
     */
    public void setProject(Project project) {
        Project oldValue = this.project;
        this.project = project;
        changeSupport.firePropertyChange(EVENT_PROJECT, oldValue, project);
    }
    
    /**
     * Returns a list of SANPV for this project for each start period. This
     * function will call getSaNpv(double, int) for each possible start period.
     * 
     * @param interestRate
     */
    public Double[] getSaNpvList(double interestRate) {
        int periods = project.getPeriods();
        Double sanpv[] = new Double[periods];
        for (int p = 0; p < periods; p++) {
            sanpv[p] = getSaNpv(interestRate, p);
        }
        return sanpv;
    }

    /**
     * Returns the SANPV for the given start period and interest rate.
     * 
     * @param interestRate
     * @param skipPeriods
     * @throws MmfException
     */
    public double getSaNpv(double interestRate, int skipPeriods) {
        if (skipPeriods < 0) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + skipPeriods);
        }

        double npv = 0.0F;
        for (int p = 1; p <= project.getPeriods() - skipPeriods; p++) {
            Double rev = getRevenue(p);
            int per = (skipPeriods + p);
            npv += rev / Math.pow(interestRate + 1, per);
        }
        return npv;
    }
    
    public double getDiscountedValue(double interestRate, int period){
    	if (period < 1 || period >= project.getPeriods()) {
            throw new IllegalArgumentException("Invalid startPeriod: "
                    + period);
        }
    	
    	double discountValue = 0.0;
    	discountValue = getRevenue(period) / Math.pow(interestRate + 1, period);
    	
    	return discountValue;
    }
    
    
    
    
}
