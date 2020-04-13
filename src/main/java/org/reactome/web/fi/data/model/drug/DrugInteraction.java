package org.reactome.web.fi.data.model.drug;

/**
 * 
 * @author brunsont
 *
 */
public class DrugInteraction {

	private String targetUniprot;
	private String targetSymbol;
	private String actionType;
	private String activityType;
	private Float activityValue;
	
	public DrugInteraction(String targetUniprot, String targetSymbol, String actionType, String activityType,
			Float activityValue) {
		this.targetUniprot = targetUniprot;
		this.targetSymbol = targetSymbol;
		this.actionType = actionType;
		this.activityType = activityType;
		this.activityValue = activityValue;
	}

	public String getTargetUniprot() {
		return targetUniprot;
	}

	public void setTargetUniprot(String targetUniprot) {
		this.targetUniprot = targetUniprot;
	}

	public String getTargetSymbol() {
		return targetSymbol;
	}

	public void setTargetSymbol(String targetSymbol) {
		this.targetSymbol = targetSymbol;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public Float getActivityValue() {
		return activityValue;
	}

	public void setActivityValue(Float activityValue) {
		this.activityValue = activityValue;
	}
}
