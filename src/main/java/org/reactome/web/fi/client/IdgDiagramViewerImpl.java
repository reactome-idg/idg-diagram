package org.reactome.web.fi.client;

import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;

public class IdgDiagramViewerImpl extends DiagramViewerImpl {
	
	public IdgDiagramViewerImpl() {
		super();
	}
	
	@Override
	protected ViewerContainer createViewerContainer() {
		return new IdgViewerContainer(eventBus);
		
	}

	
}
