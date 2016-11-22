package cs.ucl.moifm.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import cs.ucl.moifm.model.Feature;
import cs.ucl.moifm.model.FeatureException;
import cs.ucl.moifm.model.Project;

public class ModelParser {
	
	public static void fileToModelParser (CSVReader reader, Project project) throws IOException, FeatureException{
		String [] nextLine;

	    while ((nextLine = reader.readNext()) != null) {
	    	Feature mmf = new Feature("");
	    	List<Double> cashvalue = new ArrayList<Double>();
	        // nextLine[] is an array of values from the line
	    	for (int i = 0; i < nextLine.length; i++){
	    		if (i == 0){
	    			mmf.setId(nextLine[i]);
	    			mmf.setName("MMF" + mmf.getId());
	    			mmf.setProject(project);
	    		}
	    		else {
	    			cashvalue.add(Double.parseDouble(nextLine[i]));
	    		}
	    		
	    	}
	    	mmf.setCashFlow(cashvalue);
	    	mmf.setCashValue(cashvalue);
	    	mmf.setDevPeriod();
	    	Project.add(mmf);
	    	Project.setPeriods(cashvalue.size());
	     }
	    Project.setFeatures();
	  
	  
	}
	
	public static void convertFileToPrecedence(CSVReader predReader, Project project) throws IOException, FeatureException{
		String[] nextLine;
		
		while ((nextLine = predReader.readNext()) != null){
			if (Project.getMmfs().containsKey(nextLine[0]))
				Project.getMmfs().get(nextLine[0]).addPrecursor(Project.getMmfs().get(nextLine[1]));
		}
		
		Project.setStrands();
	}
}
