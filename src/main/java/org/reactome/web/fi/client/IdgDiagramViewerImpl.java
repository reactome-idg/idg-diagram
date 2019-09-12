package org.reactome.web.fi.client;

import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.handlers.ContentLoadedHandler;

public class IdgDiagramViewerImpl extends DiagramViewerImpl{
	
	public IdgDiagramViewerImpl() {
		super();
		
	}
	
	@Override
	protected ViewerContainer createViewerContainer() {
		return new IdgViewerContainer(eventBus);
		
	}
}
