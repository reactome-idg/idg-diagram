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
	
}
