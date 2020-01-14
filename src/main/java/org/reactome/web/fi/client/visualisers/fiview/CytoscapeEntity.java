package org.reactome.web.fi.client.visualisers.fiview;

import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper;

import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class CytoscapeEntity extends CytoscapeWrapper{

	EventBus eventBus;
	String baseStyle;
	
	public CytoscapeEntity(EventBus eventBus, String baseStyle, Handler handler) {
		super(eventBus, baseStyle, handler);
		this.eventBus = eventBus;
		this.baseStyle = baseStyle;
	}	
}
