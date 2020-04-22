package org.reactome.web.fi.client.visualisers.fiview;

import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author brunsont
 *
 */
public class CytoscapeEntity extends CytoscapeWrapper{

	String baseStyle;
	String layout;
	
	public CytoscapeEntity(String baseStyle, Handler handler) {
		super(baseStyle, handler);
		this.baseStyle = baseStyle;
	}

	@Override
	public void setCytoscapeLayout(String layoutString) {
		this.layout = layoutString;
		super.setCytoscapeLayout(layoutString);
	}	
	
	public String getLayout() {
		return layout;
	}
	
	/**
	 * Makes a node for only a passed in gene name string
	 * @param displayName
	 * @return
	 */
	public JSONValue getProtein(String id, String displayName, boolean interactor) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		node.put("id", new JSONString(id));
		node.put("name", new JSONString(displayName));
		if(interactor == true)
			node.put("interactor", new JSONString("true"));
		else
			node.put("interactor", new JSONString("false"));
		node.put("color", new JSONString("#FF0000"));
		result.put("data", node);
		return result;
	}
	
	/**
	 * Makes a FI edge bassed on a passed in id, target and source
	 * @param id
	 * @param source
	 * @param target
	 * @return
	 */
	public JSONValue makeFI(int id, String source, String target, String relationship) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("edges"));
		
		JSONObject edge = new JSONObject();
		edge.put("id", new JSONString(id+""));
		edge.put("source", new JSONString(source));
		edge.put("target", new JSONString(target));
		edge.put("direction", new JSONString("-"));
		edge.put("lineStyle", new JSONString(relationship));
		
		result.put("data", edge);
		return result;
	}
	
}
