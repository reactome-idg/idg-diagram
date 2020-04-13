package org.reactome.web.fi.tools.popup.tables.models;

public class DrugTargetResult {

	private String geneName;
	private String uniprot;
	private String drugName;
	private String compoundChMBLId;
	private String actionType;
	private String activityType;
	private Float activityValue;
	
	public DrugTargetResult(String uniprot, String geneName, String drugName, String compoundChMBLId, String actionType,
			String activityType, Float activityValue) {
		this.geneName = geneName;
		this.uniprot = uniprot;
		this.drugName = drugName;
		this.compoundChMBLId = compoundChMBLId;
		this.actionType = actionType;
		this.activityType = activityType;
		this.activityValue = activityValue;
	}
	public String getGeneName() {
		return geneName;
	}
	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}
	public String getUniprot() {
		return uniprot;
	}
	public void setUniprot(String uniprot) {
		this.uniprot = uniprot;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getCompoundChMBLId() {
		return compoundChMBLId;
	}
	public void setCompoundChMBLId(String compoundChMBLId) {
		this.compoundChMBLId = compoundChMBLId;
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
