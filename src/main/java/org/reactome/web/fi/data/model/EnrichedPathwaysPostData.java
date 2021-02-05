package org.reactome.web.fi.data.model;

import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * 
 * @author brunsont
 *
 */
public class EnrichedPathwaysPostData {
	String term;
	private List<Integer> dataDescKeys;
	Double prd;
	
	public EnrichedPathwaysPostData() {
		
	}
	
	public EnrichedPathwaysPostData(String term, List<Integer> dataDescKeys, Double prd) {
		this.term = term;
		this.dataDescKeys = dataDescKeys;
		this.prd = prd;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public List<Integer> getDataDescKeys() {
		return dataDescKeys;
	}

	public void setDataDescKeys(List<Integer> dataDescKeys) {
		this.dataDescKeys = dataDescKeys;
	}
	
	public Double getPrd() {
		return prd;
	}

	public void setPrd(Double prd) {
		this.prd = prd;
	}

	public String toJSON() {
		JSONObject rtn = new JSONObject();
		rtn.put("term", new JSONString(this.term));
		JSONArray descJSON = new JSONArray();
		dataDescKeys.forEach(d -> {
			descJSON.set(descJSON.size(), new JSONNumber(d));
		});
		rtn.put("dataDescKeys", descJSON);
		
		if(prd != null)
			rtn.put("prd", new JSONNumber(prd));
		
		return rtn.toString();
	}
}
