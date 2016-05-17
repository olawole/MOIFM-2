package cs.ucl.moifm.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import cs.ucl.moifm.model.MMF;
import cs.ucl.moifm.model.MMFException;
import cs.ucl.moifm.model.Project;

public class ModelParser {
	
	public static void fileToModelParser (CSVReader reader, Project project) throws IOException, MMFException{
		String [] nextLine;

	    while ((nextLine = reader.readNext()) != null) {
	    	MMF mmf = new MMF("");
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
	    	project.add(mmf);
	    	project.setPeriods(cashvalue.size());
	     }
	    project.setFeatures();
	  
	  
	}
	
	public static void convertFileToPrecedence(CSVReader predReader, Project project) throws IOException, MMFException{
		String[] nextLine;
		
		while ((nextLine = predReader.readNext()) != null){
			if (project.getMmfs().containsKey(nextLine[0]))
				project.getMmfs().get(nextLine[0]).addPrecursor(project.getMmfs().get(nextLine[1]));
		}
		
		project.setStrands();
	}
}
