package org.reactome.web.fi.client;

import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.data.loader.LoaderManager;

import org.reactome.web.fi.data.loader.IDGLoaderManager;

public class IdgDiagramViewerImpl extends DiagramViewerImpl{
	
	public IdgDiagramViewerImpl() {
		super();
		
	}
	
	@Override
	protected ViewerContainer createViewerContainer() {
		return new IdgViewerContainer(eventBus);
		
	}
	
	@Override
	protected LoaderManager createLoaderManager() {
		return new IDGLoaderManager(eventBus);
	}
}
