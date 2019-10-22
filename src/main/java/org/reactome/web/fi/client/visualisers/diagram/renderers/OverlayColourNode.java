package org.reactome.web.fi.client.visualisers.diagram.renderers;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface OverlayColourNode {

	@PropertyName("name")
	String getName();
	
	@PropertyName("fill")
	String getFill();	
	
	void setFill(String colour);
	
}
