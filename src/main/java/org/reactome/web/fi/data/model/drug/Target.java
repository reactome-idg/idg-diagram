package org.reactome.web.fi.data.model.drug;

/**
 * 
 * @author brunsont
 *
 */
public interface Target {

	Double getId();
	
	String getName();
	
	String getTargetType();
	
	String getDescription();
	
	String getComment();
	
	String getTargetDevLevel();
	
	String getFamily();
	
	Protein getProtein();
	
}
