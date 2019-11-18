package org.reactome.web.fi.overlay.profiles;

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

	private Map<Double, String> colourMap;
	private static OverlayColours overlayColours;
	
	private OverlayColours() { /*Nothing Here*/ }
	
	public static OverlayColours get() {
		if(overlayColours ==  null) {
			overlayColours = new OverlayColours();
		}
		return overlayColours;
	}
	
	public Map<Double, String> getColours(){
		if(colourMap == null)
			loadOverlayColours();
		return colourMap;
	}
	
	private void loadOverlayColours() {
		OverlayColourProperties colours = null;
		try {
			colours = OverlayColourFactory.getOverlayObject(OverlayColourProperties.class,
												  ColourSource.SOURCE.targetLevel().getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		colourMap = new HashMap<>();
		for(int i=0; i<colours.getNodes().size(); i++) {
			colourMap.put(new Double(colours.getNodes().indexOf(colours.getNodes().get(i))), colours.getNodes().get(i).getFill());
		}		
	}

	interface ColourSource extends ClientBundle{
		
		ColourSource SOURCE = GWT.create(ColourSource.class);
			
		@Source("target_development_level.json")
		TextResource targetLevel();
	}
	
}
