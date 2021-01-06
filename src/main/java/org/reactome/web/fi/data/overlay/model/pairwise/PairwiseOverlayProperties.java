package org.reactome.web.fi.data.overlay.model.pairwise;

import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseOverlayProperties {

	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private List<String> uniprots;
	
	public PairwiseOverlayProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects, List<String> uniprots) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		this.uniprots = uniprots;
	}

	public List<PairwiseOverlayObject> getPairwiseOverlayObjects() {
		return pairwiseOverlayObjects;
	}

	public void setPairwiseOverlayObjects(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	public List<String> getGeneNames() {
		return uniprots;
	}

	public void setGeneNames(List<String> uniprots) {
		this.uniprots = uniprots;
	}
	
	public String toJSONString() {
		JSONObject rtn = new JSONObject();
		
		//have to make JSONArrays manually because they only have an empty constructor
		JSONArray geneArray = new JSONArray();
		this.uniprots.forEach(u -> geneArray.set(geneArray.size(), new JSONString(u)));
		JSONArray descs = new JSONArray();
		this.pairwiseOverlayObjects.forEach(obj -> descs.set(descs.size(), new JSONString(obj.getId())));
		
		rtn.put("genes", geneArray);
		rtn.put("dataDescs", descs);	
		
		return rtn.toString();
	}
	
}
