package org.reactome.web.fi.client.visualisers.diagram.profiles;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayColours{

	private Map<String, Map<String, String>> overlayColoursMap;
	private Map<String, Map<Double, String>> overlayDoubleColoursMap;
	private static OverlayColours overlayColours;
	
	private OverlayColours() { /*Nothing Here*/ }
	
	public static OverlayColours get() {
		if(overlayColours ==  null) {
			overlayColours = new OverlayColours();
		}
		return overlayColours;
	}
	
	public Map<String, String> getColours(String name){
		if(overlayColoursMap == null || !overlayColoursMap.containsKey(name)) 
			loadOverlayProperties(name);
	
		return overlayColoursMap.get(name);
	}
	
	public Map<String, Map<String,String>> getOverlayColoursMap(){
		return this.overlayColoursMap;
	}
	
	public Map<Double, String> getDoubleColoursMap(String name){
		if(overlayDoubleColoursMap==null || !overlayDoubleColoursMap.containsKey(name))
			loadOverlayProperties(name);
		
		return overlayDoubleColoursMap.get(name);
	}
	
	private void loadOverlayProperties(String name) {
		OverlayColourProperties colours = null;
		try {
			colours = OverlayColourFactory.getOverlayObject(OverlayColourProperties.class,
												  getSource(name));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(overlayColoursMap == null)
			overlayColoursMap = new HashMap<>();
		if(overlayDoubleColoursMap == null)
			overlayDoubleColoursMap = new HashMap<>();
		
		Map<String, String> colourMap = new HashMap<>();
		Map<Double, String> doubleColourMap = new HashMap<>();
		for(OverlayColourNode node: colours.getNodes()) {
			colourMap.put(node.getName(), node.getFill());
			doubleColourMap.put((double)doubleColourMap.size(), colourMap.get(node.getName()));
		}
		
		overlayColoursMap.put(name, colourMap);
		overlayDoubleColoursMap.put(name, doubleColourMap);
	}
	
	private String getSource(String name) {

		String result = null;
		switch(name) {
		case "Target Development Level": result = ColourSource.SOURCE.targetLevel().getText();	break;
		}
		return result;
	}

	interface ColourSource extends ClientBundle{
		
		ColourSource SOURCE = GWT.create(ColourSource.class);
			
		@Source("target_development_level.json")
		TextResource targetLevel();
	}
	
}
