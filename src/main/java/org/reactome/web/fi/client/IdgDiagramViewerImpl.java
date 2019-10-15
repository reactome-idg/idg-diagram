package org.reactome.web.fi.client;

import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.data.loader.IDGLoaderManager;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.TargetLevelDataRequestedEvent;
import org.reactome.web.fi.handlers.CytoscapeToggledHandler;
import org.reactome.web.fi.handlers.TargetLevelDataRequestedHandler;	

/**
 * 
 * @author brunsont
 *
 */
public class IdgDiagramViewerImpl extends DiagramViewerImpl implements CytoscapeToggledHandler,
TargetLevelDataRequestedHandler{
	
	public IdgDiagramViewerImpl() {
		super();
		
		eventBus.addHandler(CytoscapeToggledEvent.TYPE, this);
		eventBus.addHandler(TargetLevelDataRequestedEvent.TYPE, this);
		
	}
	
	@Override
	protected ViewerContainer createViewerContainer() {
		return new IdgViewerContainer(eventBus);
	}
	
	@Override
	protected LoaderManager createLoaderManager() {
		return new IDGLoaderManager(eventBus);
	}

	@Override
	public void onCytoscapeToggled(CytoscapeToggledEvent event) {
		load(event.getContext().getContent().getStableId());
	}

	@Override
	public void onTargetLevelDataRequested(TargetLevelDataRequestedEvent event) {
		((IDGLoaderManager)loaderManager).loadTCRDTargetLevel(event.getIds());
	}
}
