package org.reactome.web.fi.data.model.drug;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author brunsont
 *
 */
public class Drug {

	private String name;
	private String compoundChEMBLId;
	private List<DrugInteraction> drugInteractions;
	
	public Drug(String name, String compoundChEMBLId) {
		this.name = name;
		this.compoundChEMBLId = compoundChEMBLId;
		drugInteractions = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompoundChEMBLId() {
		return compoundChEMBLId;
	}

	public void setCompoundChEMBLId(String compoundChEMBLId) {
		this.compoundChEMBLId = compoundChEMBLId;
	}

	public List<DrugInteraction> getDrugInteractions() {
		return drugInteractions;
	}

	public void setDrugInteractions(List<DrugInteraction> drugInteractions) {
		this.drugInteractions = drugInteractions;
	}
	
	public void addDrugTargetInteraction(DrugInteraction drugInteraction) {
		this.getDrugInteractions().add(drugInteraction);
	}
	
}
