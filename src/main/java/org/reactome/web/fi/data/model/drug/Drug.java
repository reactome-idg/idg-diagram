package org.reactome.web.fi.data.model.drug;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author brunsont
 *
 */
public class Drug {

	private int id;
	private String name;
	private String compoundChEMBLId;
	private Map<String, DrugInteraction> drugInteractions;
	
	public Drug(int id, String name, String compoundChEMBLId) {
		this.id = id;
		this.name = name;
		this.compoundChEMBLId = compoundChEMBLId;
		drugInteractions = new HashMap<>();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Map<String, DrugInteraction> getDrugInteractions() {
		return drugInteractions;
	}

	public void setDrugInteractions(Map<String, DrugInteraction> drugInteractions) {
		this.drugInteractions = drugInteractions;
	}
	
	public void addDrugTargetInteraction(String uniprot, DrugInteraction drugInteraction) {
		this.getDrugInteractions().put(uniprot, drugInteraction);
	}
	
}
