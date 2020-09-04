package org.reactome.web.fi.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.client.visualisers.diagram.DiagramVisualiser;
import org.reactome.web.diagram.client.visualisers.ehld.SVGVisualiser;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.AnalysisResultLoadedEvent;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlagRequestedEvent;
import org.reactome.web.diagram.events.DiagramObjectsFlagResetEvent;
import org.reactome.web.diagram.events.DiagramProfileChangedEvent;
import org.reactome.web.diagram.events.EntityDecoratorSelectedEvent;
import org.reactome.web.diagram.events.PairwiseOverlayButtonClickedEvent;
import org.reactome.web.diagram.handlers.EntityDecoratorSelectedHandler;
import org.reactome.web.diagram.handlers.PairwiseOverlayButtonClickedHandler;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.fi.client.visualisers.fiview.FIViewVisualizer;
import org.reactome.web.fi.data.loader.IDGLoaderManager;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.loader.PairwiseInfoService.peFlagHandler;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.DrugTargetsRequestedEvent;
import org.reactome.web.fi.events.FIDiagramObjectsFlaggedEvent;
import org.reactome.web.fi.events.OverlayRequestedEvent;
import org.reactome.web.fi.events.PairwiseCountsRequestedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.CytoscapeToggledHandler;
import org.reactome.web.fi.handlers.DrugTargetsRequestedHandler;
import org.reactome.web.fi.handlers.OverlayDataRequestedHandler;
import org.reactome.web.fi.handlers.PairwiseCountsRequestedHandler;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;
import org.reactome.web.fi.tools.popup.PopupTypes;

/**
 * 
 * @author brunsont
 *
 */
public class IdgDiagramViewerImpl extends DiagramViewerImpl implements CytoscapeToggledHandler,
OverlayDataRequestedHandler, PairwiseOverlayButtonClickedHandler, PairwiseCountsRequestedHandler,
EntityDecoratorSelectedHandler, DrugTargetsRequestedHandler{
		
	public IdgDiagramViewerImpl() {
		super();
		eventBus.fireEventFromSource(new DiagramProfileChangedEvent(DiagramColours.ProfileType.PROFILE_01.getDiagramProfile()), this);
		eventBus.addHandler(CytoscapeToggledEvent.TYPE, this);
		eventBus.addHandler(OverlayRequestedEvent.TYPE, this);
		eventBus.addHandler(PairwiseOverlayButtonClickedEvent.TYPE, this);
		eventBus.addHandler(PairwiseCountsRequestedEvent.TYPE, this);
		eventBus.addHandler(EntityDecoratorSelectedEvent.TYPE, this);
		eventBus.addHandler(DrugTargetsRequestedEvent.TYPE, this);
		
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
	public void onDrugTargetsRequested(DrugTargetsRequestedEvent event) {
		((IDGLoaderManager)loaderManager).loadDrugTargets(event.getUniprots());
	}

	@Override
	public void onContentLoaded(ContentLoadedEvent event) {
		// TODO Auto-generated method stub
		super.onContentLoaded(event);
	}

	@Override
	public void onContentRequested(ContentRequestedEvent event) {
		//resets pairwise number entities to 0 for new diagrams because the correct ones need to be loaded
		IDGPopupFactory.get().onContentRequested();
		super.onContentRequested(event);
	}

	@Override
	public void flagItems(String identifier, Boolean includeInteractors) {
		if (context != null && identifier != null) {
            if(!identifier.equalsIgnoreCase(context.getFlagTerm()) || !this.includeInteractors.equals(includeInteractors)) {
                eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier, includeInteractors), this);
            }
            else eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier, includeInteractors), this);
        }
	}

	@Override
	public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
		IDGPopupFactory.get().setFlagTerm(null); //remove this so any pairwise interactors aren't filtered
		IDGPopupFactory.get().setFlagInteractors(null);
		super.onDiagramObjectsFlagReset(event);
	}

	@Override
	public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
		if(context.getFlagTerm() == event.getTerm()) return;
		if(!event.getIncludeInteractors()) {
			super.onDiagramObjectsFlagRequested(event);
			return;
		}
		
		event.getTerm().replaceAll("%7C", "|");
		String[] tokens = event.getTerm().split(",");	
		String term = tokens[0];
		List<String> dataDescs = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
		
		context.setFlagTerm(term);
		this.includeInteractors = event.getIncludeInteractors();
		IDGPopupFactory.get().setFlagTerm(term);
		
		PairwiseInfoService.loadPEFlags(context.getContent().getDbId(), term, dataDescs, new peFlagHandler() {
			@Override
			public void onPEFlagsLoaded(List<Long> pes, List<String> flagInteractors) {
				IDGPopupFactory.get().setFlagInteractors(flagInteractors);
				flagObjects(event.getTerm(), pes);
			}
			@Override
			public void onPEFlagsLoadedError(Throwable exception) {
				context.setFlagTerm(null);
				eventBus.fireEventFromSource(new DiagramObjectsFlagResetEvent(), this);
			}
		});
		
	}

	/**
	 * Want to fire FI version of event if FI visualiser
	 */
	@Override
	public void flaggedElementsLoaded(String term, Occurrences toFlag, boolean notify) {
		Visualiser viz = ((IdgViewerContainer)viewerContainer).getActiveVisualizer();
		if(viz instanceof DiagramVisualiser || viz instanceof SVGVisualiser)
			super.flaggedElementsLoaded(term, toFlag, notify);
		else if(viz instanceof FIViewVisualizer)
			eventBus.fireEventFromSource(new FIDiagramObjectsFlaggedEvent(term, this.includeInteractors, Collections.singletonList(term), false), this);
	}

	private void flagObjects(String term, List<Long> pes) {
		if(pes.size() == 0)
			eventBus.fireEventFromSource(new DiagramObjectsFlagResetEvent(), this);
		
		((IdgViewerContainer)viewerContainer).flagObjects(term, pes, this.includeInteractors);
		
		}

	@Override
	public void onCytoscapeToggled(CytoscapeToggledEvent event) {
		IDGPopupFactory.get().onContentRequested();
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
			IDGPopupFactory.get().openPopup(event.getGraphObject(), PopupTypes.fromString("TR")); //hard coded "TR" to minimize changes to the diagram project
		else
			IDGPopupFactory.get().openPopup(event.getUniprot(), event.getGeneName(), PopupTypes.fromString(event.getType()));
	}

	@Override
	public void onPairwiseCountsRequested(PairwiseCountsRequestedEvent event) {
		((IDGLoaderManager)loaderManager).loadPairwiseOverlayCounts(event.getPairwiseOverlayProperties());
	}

	@Override
	public void onEntityDecoratorSelected(EntityDecoratorSelectedEvent event) {
		if(event.getSummaryItem() == null) return;
		IDGPopupFactory.get().openPopup(event.getGraphObject(), PopupTypes.fromString(event.getSummaryItem().getType()));
	}
}
