package org.reactome.web.fi.data.model.drug;

/**
 * 
 * @author brunsont
 *
 */
public interface DrugTargetEntity {

	Double getId();
	
	Target getTarget();
	
	String getSmiles();
	
	Float getActivityValue();
	
	String getActivityType();
	
	String getReference();
	
	String getCompoundChEMBLId();
	
	String getDrug();
	
	String getActionType();
	
	boolean getHasMoa();
	
	String getSource();
	
	String getNlmDrugInfo();
	
}
