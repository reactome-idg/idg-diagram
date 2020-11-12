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
	List<String> dataDescs;
	
	public FlagPEsPostData() {
		
	}

	public FlagPEsPostData(String term, Long dbId, List<String> dataDescs) {
		super();
		this.term = term;
		this.dbId = dbId;
		this.dataDescs = dataDescs;
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

	public List<String> getDataDescs() {
		return dataDescs;
	}

	public void setDataDescs(List<String> dataDescs) {
		this.dataDescs = dataDescs;
	}
	
	public String toJSONString() {
		JSONObject rtn = new JSONObject();
		
		rtn.put("term", new JSONString(this.term));
		rtn.put("dbId", new JSONNumber(this.dbId));
		
		JSONArray descArray = new JSONArray();
		this.dataDescs.forEach(d -> {
			descArray.set(descArray.size(), new JSONString(d));
		});
		rtn.put("dataDescs", descArray);
		
		
		return rtn.toString();
	}
}
