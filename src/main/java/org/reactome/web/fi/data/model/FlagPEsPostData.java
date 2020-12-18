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
public class FlagPEsPostData {

	String term;
	Long dbId; //dbid of pathway
	List<Integer> dataDescKeys;
	Double prd;
	
	public FlagPEsPostData() {
		
	}

	public FlagPEsPostData(String term, Long dbId, List<Integer> dataDescKeys, Double prd) {
		super();
		this.term = term;
		this.dbId = dbId;
		this.dataDescKeys = dataDescKeys;
		this.prd = prd;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Long getDbId() {
		return dbId;
	}

	public void setDbId(Long dbId) {
		this.dbId = dbId;
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

	public String toJSONString() {
		JSONObject rtn = new JSONObject();
		
		rtn.put("term", new JSONString(this.term));
		rtn.put("dbId", new JSONNumber(this.dbId));
		
		JSONArray descArray = new JSONArray();
		dataDescKeys.forEach(d -> {
			descArray.set(descArray.size(), new JSONNumber(d));
		});
		rtn.put("dataDescKeys", descArray);
		
		if(prd != null)
			rtn.put("prd", new JSONNumber(prd));
		
		return rtn.toString();
	}
}
