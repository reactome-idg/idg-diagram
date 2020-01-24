package org.reactome.web.fi.client.visualisers.fiview;

import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper;

import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class CytoscapeEntity extends CytoscapeWrapper{

	String baseStyle;
	
	public CytoscapeEntity(String baseStyle, Handler handler) {
		super(baseStyle, handler);
		this.baseStyle = baseStyle;
	}	
}
