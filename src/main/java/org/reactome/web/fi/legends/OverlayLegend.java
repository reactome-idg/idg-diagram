package org.reactome.web.fi.legends;

import org.reactome.web.diagram.legends.LegendPanel;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.shared.EventBus;

public class OverlayLegend extends LegendPanel{
	
	Canvas gradient;

	public OverlayLegend(EventBus eventBus) {
		super(eventBus);
		
	}

}
