package org.reactome.web.fi.client.visualisers.diagram.profiles;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public class OverlayColours{

	private Map<String, OverlayColourProperties> overlayColoursMap;
	private static OverlayColours overlayColours;
	
	private OverlayColours() {
		overlayColoursMap = new HashMap<>();
	}
	
	public static OverlayColours get() {
		if(overlayColours ==  null) {
			overlayColours = new OverlayColours();
		}
		return overlayColours;
	}
	
	public Map<String, String> getColours(String name){
		if(overlayColoursMap == null || !overlayColoursMap.containsKey(name)) {
			loadOverlayProperties(name);
		}
		Map<String, String> colourMap = new HashMap<>();
		
		for(OverlayColourNode node: overlayColoursMap.get(name).getNodes()) {
			colourMap.put(colourMap.size()+"", node.getFill());
			colourMap.put(node.getName(), node.getFill());
		}
		return colourMap;
	}
	
	private void loadOverlayProperties(String name) {
		OverlayColourProperties colours = null;
		try {
			colours = OverlayColourFactory.getOverlayObject(OverlayColourProperties.class,
												  getSource(name));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String text = colours.getType();
		overlayColoursMap.put(text, colours);
	}
	
	private String getSource(String name) {
		String result = null;
		switch(name) {
		case "targetlevel": result = ColourSource.SOURCE.targetLevel().getText();	break;
		}
		return result;
	}

	interface ColourSource extends ClientBundle{
		
		ColourSource SOURCE = GWT.create(ColourSource.class);
		
		@Source("target_level.json")
		TextResource targetLevel();
		
	}
	
}
