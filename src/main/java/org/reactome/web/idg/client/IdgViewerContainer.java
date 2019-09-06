package org.reactome.web.idg.client;

import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.idg.client.visualisers.fiview.FIViewVisualiser;

import com.google.gwt.event.shared.EventBus;

public class IdgViewerContainer extends ViewerContainer{

	EventBus eventBus;
	
	IconButton fiviewButton;
	
	public IdgViewerContainer(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		initialise();
	}

	@Override
	protected void initialise() {
		super.initialise();
		this.add(new FIViewVisualiser(eventBus));
		
	}
	
}
