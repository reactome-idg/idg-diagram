package org.reactome.web.fi.client;

import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.EntityDecoratorSelectedEvent;
import org.reactome.web.diagram.events.PairwiseOverlayButtonClickedEvent;
import org.reactome.web.diagram.handlers.EntityDecoratorSelectedHandler;
import org.reactome.web.diagram.handlers.PairwiseOverlayButtonClickedHandler;
import org.reactome.web.fi.data.loader.IDGLoaderManager;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.OverlayRequestedEvent;
import org.reactome.web.fi.events.PairwiseCountsRequestedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.CytoscapeToggledHandler;
import org.reactome.web.fi.handlers.OverlayDataRequestedHandler;
import org.reactome.web.fi.handlers.PairwiseCountsRequestedHandler;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwisePopupFactory;

/**
 * 
 * @author brunsont
 *
 */
public class IdgDiagramViewerImpl extends DiagramViewerImpl implements CytoscapeToggledHandler,
OverlayDataRequestedHandler, PairwiseOverlayButtonClickedHandler, PairwiseCountsRequestedHandler,
EntityDecoratorSelectedHandler{
	
	public IdgDiagramViewerImpl() {
		super();
		eventBus.addHandler(CytoscapeToggledEvent.TYPE, this);
		eventBus.addHandler(OverlayRequestedEvent.TYPE, this);
		eventBus.addHandler(PairwiseOverlayButtonClickedEvent.TYPE, this);
		eventBus.addHandler(PairwiseCountsRequestedEvent.TYPE, this);
		eventBus.addHandler(EntityDecoratorSelectedEvent.TYPE, this);
		
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
	public void onAnalysisResultLoaded(AnalysisResultLoadedEvent event) {
		eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
		super.onAnalysisResultLoaded(event);
	}

	@Override
	public void onDataOverlayRequested(OverlayRequestedEvent event) {
		((IDGLoaderManager)loaderManager).loadTCRDData(event.getDataOverlayProperties());
	}

	@Override
	public void onPairwiseOverlayButtonClicked(PairwiseOverlayButtonClickedEvent event) {
		if(event.getGraphObject() != null)
			PairwisePopupFactory.get().openPopup(event.getGraphObject());
		else
			PairwisePopupFactory.get().openPopup(event.getUniprot(), event.getGeneName());
	}

	@Override
	public void onPairwiseCountsRequested(PairwiseCountsRequestedEvent event) {
		((IDGLoaderManager)loaderManager).loadPairwiseOverlayCounts(event.getPairwiseOverlayProperties());
	}

	@Override
	public void onEntityDecoratorSelected(EntityDecoratorSelectedEvent event) {
		if(event.getSummaryItem() != null)
			PairwisePopupFactory.get().openPopup(event.getGraphObject());
	}
}
