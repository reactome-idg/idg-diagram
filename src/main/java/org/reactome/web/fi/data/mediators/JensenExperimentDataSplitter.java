package org.reactome.web.fi.data.mediators;

public class JensenExperimentDataSplitter {

	public String split(String toSplit) {
		
		String valueString = "";
		
		if(toSplit.contains("antibody") || toSplit.contains("antibodies")) {
			if(!toSplit.contains("Medium: ")) return "";
			valueString = toSplit.substring(toSplit.lastIndexOf("M")+8, toSplit.lastIndexOf("M")+9);
		}
		else if(toSplit.contains(",")) {
			String[] splitString = toSplit.split(", ");
			double valueDouble = 0;
			for(String val : splitString)
				if(val != "N/A") valueDouble += new Double(val);
			
			valueString = valueDouble + "";
		}
		else {
			valueString = toSplit.split(" ")[0];
		}
		
		if(valueString == "" || valueString == "NA")
			return "";
		return valueString;
	}
	
}