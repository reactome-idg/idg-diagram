package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public class OverlayColours{

	private Map<String, OverlayColourProperties> overlayColoursMap;
	private static OverlayColours overlayColours;
	
	private OverlayColours() {
		OverlayColourProperties colours = null;
		overlayColoursMap = new HashMap<>();
		try {
			colours = OverlayColourFactory.getOverlayObject(OverlayColourProperties.class,
												  ColourSource.SOURCE.targetLevel()
												  .getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String text = colours.getType();
		overlayColoursMap.put(text, colours);
	}
	
	public static OverlayColours get() {
		if(overlayColours ==  null) {
			overlayColours = new OverlayColours();
		}
		return overlayColours;
	}
	
	public OverlayColourProperties getColours(String name){
		return overlayColoursMap.get(name);
	}
	
	interface ColourSource extends ClientBundle{
		
		ColourSource SOURCE = GWT.create(ColourSource.class);
		
		@Source("target_level.json")
		TextResource targetLevel();
		
	}
	
}
