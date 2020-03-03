package org.reactome.web.fi.data.loader;

import org.reactome.web.diagram.data.layout.Diagram;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectsFactory;
import org.reactome.web.diagram.data.loader.LayoutLoader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONNumber;

public class IDGLayoutLoader extends LayoutLoader{

	IDGLayoutLoader(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		switch(response.getStatusCode()) {
			case Response.SC_OK:
				try {
					long start = System.currentTimeMillis();
					String responseString = correctResponse(response.getText());
					Diagram diagram = DiagramObjectsFactory.getModelObject(Diagram.class, responseString);
					long time = System.currentTimeMillis() - start;
					this.handler.layoutLoaded(diagram, time);
				}catch(DiagramObjectException e) {
					this.handler.onLayoutLoaderError(e);
				}
				break;
			default:
				this.handler.onLayoutLoaderError(new Exception(response.getStatusText()));
		}
	}

	private String correctResponse(String text) {
		GWT.log(text);
		JSONValue value = JSONParser.parseStrict(text);
		JSONObject valueObj = value.isObject();
		
		JSONArray nodeArray = valueObj.get("nodes").isArray();
		
		if(nodeArray == null || nodeArray.size() == 0)
			return text;
		
		for(int i=0; i<nodeArray.size(); i++) {
			JSONObject node = nodeArray.get(i).isObject();
			double newX = node.get("maxX").isNumber().doubleValue() + 10;
			node.put("maxX", new JSONNumber(newX));
			double newY = node.get("minY").isNumber().doubleValue() - 10;
			node.put("minY", new JSONNumber(newY));
		}
		
		valueObj.put("nodes", nodeArray);
		
		GWT.log(valueObj.toString());
		
		return valueObj.toString();
	}

	
	
}
