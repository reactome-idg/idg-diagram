package org.reactome.web.fi.client;

import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.fi.data.loader.IDGLoaderManager;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.OverlayDataRequestedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.CytoscapeToggledHandler;
import org.reactome.web.fi.handlers.OverlayDataRequestedHandler;	

/**
 * 
 * @author brunsont
 *
 */
public class IdgDiagramViewerImpl extends DiagramViewerImpl implements CytoscapeToggledHandler,
OverlayDataRequestedHandler{
	
	public IdgDiagramViewerImpl() {
		super();
		
		eventBus.addHandler(CytoscapeToggledEvent.TYPE, this);
		eventBus.addHandler(OverlayDataRequestedEvent.TYPE, this);
		
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
	public void setAnalysisToken(String token, ResultFilter filter) {
		eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
		super.setAnalysisToken(token, filter);
	}

	@Override
	public void onTargetLevelDataRequested(OverlayDataRequestedEvent event) {
		((IDGLoaderManager)loaderManager).loadTCRDTargetLevel(event.getIds(), event.getType());
	}
}
