package org.reactome.web.fi.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reactome.web.diagram.client.DiagramFactory;
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
import org.reactome.web.diagram.handlers.EntityDecoratorSelectedHandler;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.search.results.data.model.Occurrences;
import org.reactome.web.fi.client.visualisers.fiview.FIViewVisualizer;
import org.reactome.web.fi.data.loader.IDGLoaderManager;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.loader.PairwiseInfoService.PEFlagHandler;
import org.reactome.web.fi.data.loader.PairwiseInfoService.PathwayEnrichmentHandler;
import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fi.data.model.PathwayEnrichmentResult;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.events.DrugTargetsRequestedEvent;
import org.reactome.web.fi.events.FIDiagramObjectsFlaggedEvent;
import org.reactome.web.fi.events.OverlayRequestedEvent;
import org.reactome.web.fi.events.PairwiseCountsRequestedEvent;
import org.reactome.web.fi.events.PairwiseOverlayButtonClickedEvent;
import org.reactome.web.fi.events.SetFIFlagDataDescsEvent;
import org.reactome.web.fi.events.UpdateIDGFlagFDREvent;
import org.reactome.web.fi.events.UpdateIDGFlagPRDEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.CytoscapeToggledHandler;
import org.reactome.web.fi.handlers.DrugTargetsRequestedHandler;
import org.reactome.web.fi.handlers.OverlayDataRequestedHandler;
import org.reactome.web.fi.handlers.PairwiseCountsRequestedHandler;
import org.reactome.web.fi.handlers.PairwiseOverlayButtonClickedHandler;
import org.reactome.web.fi.handlers.UpdateIDGFlagFDRHandler;
import org.reactome.web.fi.handlers.UpdateIDGFlagPRDHandler;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;
import org.reactome.web.fi.tools.popup.PopupTypes;

import com.google.gwt.user.client.History;

/**
 * 
 * @author brunsont
 *
 */
public class IdgDiagramViewerImpl extends DiagramViewerImpl implements CytoscapeToggledHandler,
OverlayDataRequestedHandler, PairwiseOverlayButtonClickedHandler, PairwiseCountsRequestedHandler,
EntityDecoratorSelectedHandler, DrugTargetsRequestedHandler, UpdateIDGFlagFDRHandler, UpdateIDGFlagPRDHandler{
	
	private StateTokenHelper stHelper;
	
	private List<Long> flaggedPhysicalEntities;
	private List<PathwayEnrichmentResult> flaggedPathways;	
	private List<Integer> dataDescKeys;
	private String flagTerm;
	private Double prd;
	private Double flagFDR;
	
	public IdgDiagramViewerImpl() {
		super();
		stHelper = new StateTokenHelper();
		
		flaggedPhysicalEntities = new ArrayList<>();
		flaggedPathways = new ArrayList<>();
		dataDescKeys = new ArrayList<>();
		
		eventBus.fireEventFromSource(new DiagramProfileChangedEvent(DiagramColours.ProfileType.PROFILE_01.getDiagramProfile()), this);
		eventBus.addHandler(CytoscapeToggledEvent.TYPE, this);
		eventBus.addHandler(OverlayRequestedEvent.TYPE, this);
		eventBus.addHandler(PairwiseOverlayButtonClickedEvent.TYPE, this);
		eventBus.addHandler(PairwiseCountsRequestedEvent.TYPE, this);
		eventBus.addHandler(EntityDecoratorSelectedEvent.TYPE, this);
		eventBus.addHandler(DrugTargetsRequestedEvent.TYPE, this);
		eventBus.addHandler(UpdateIDGFlagFDREvent.TYPE, this);
		eventBus.addHandler(UpdateIDGFlagPRDEvent.TYPE, this);
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
		super.onContentLoaded(event);
	}

	@Override
	public void onContentRequested(ContentRequestedEvent event) {
		//resets pairwise number entities to 0 for new diagrams because the correct ones need to be loaded
		IDGPopupFactory.get().onContentRequested();
		clearFlagging();
		super.onContentRequested(event);
	}

	@Override
	public void flagItems(String identifier, Boolean includeInteractors) {
		if (context != null && identifier != null) {
            if(!identifier.equalsIgnoreCase(context.getFlagTerm()) || !this.includeInteractors.equals(includeInteractors)) {
                eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier, includeInteractors), this);
            }
            else eventBus.fireEventFromSource(new DiagramObjectsFlagRequestedEvent(identifier, includeInteractors), this); //TODO: why is this the same?
        }
	}

	@Override
	public void onDiagramObjectsFlagReset(DiagramObjectsFlagResetEvent event) {
		IDGPopupFactory.get().setFlagTerm(null); //remove this so any pairwise interactors aren't filtered
		IDGPopupFactory.get().setFlagInteractors(null);
		clearFlagging();
		super.onDiagramObjectsFlagReset(event);
		
		if(!DiagramFactory.WIDGET_JS) {
			Map<String, String> tokenMap = stHelper.buildTokenMap(History.getToken());
			tokenMap.remove("DSKEYS");
			tokenMap.remove("SIGCUTOFF");
			tokenMap.remove("FLGFDR");
			History.newItem(stHelper.buildToken(tokenMap));
		}
	}
	
	private void clearFlagging() {
		flagTerm = null;
		prd = null;
		flagFDR = 0.05;
		dataDescKeys.clear();
		flaggedPathways.clear();
		flaggedPhysicalEntities.clear();
	}

	@Override
	public void onDiagramObjectsFlagRequested(DiagramObjectsFlagRequestedEvent event) {
		if(context.getFlagTerm() == event.getTerm()) return;
		if(!event.getIncludeInteractors()) {
			super.onDiagramObjectsFlagRequested(event);
			return;
		}
		
		Map<String, String> tokenMap = stHelper.buildTokenMap(History.getToken());
		if(!doFlag(tokenMap)) { 
			
			//if not doFlag it means FDR changed so update and reflag objects based on new FDR
			if(tokenMap.containsKey("FLGFDR")) this.flagFDR = Double.parseDouble(tokenMap.get("FLGFDR"));
			flagObjects();
			return;
		}

		//get dataDescription keys from "DSKEYS"
		//if SIGCUTOFF is not on map, add to prd, otherwise will be null
		fetchFlagInteractors(Arrays.stream(tokenMap.get("DSKEYS").split(",")).map(num -> Integer.parseInt(num)).collect(Collectors.toList()),
							 true,
							 tokenMap.get("SIGCUTOFF") != null ? Double.parseDouble(tokenMap.get("SIGCUTOFF")):null,
							 tokenMap.get("FLGFDR") != null ? Double.parseDouble(tokenMap.get("FLGFDR")):0.05d,
							 event.getTerm()
							 );
		
	}
	
	private void fetchFlagInteractors(List<Integer> keys, Boolean includeInteractors, Double prd, Double fdr, String term) {
		dataDescKeys = keys;
		this.prd = prd;
		this.flagFDR = fdr;
		this.flagTerm = term;
		
		context.setFlagTerm(flagTerm);
		super.includeInteractors = includeInteractors;
		IDGPopupFactory.get().setFlagTerm(term);
		
		PairwiseInfoService.loadPEFlags(context.getContent().getDbId(), term, dataDescKeys, prd, new PEFlagHandler() {
			@Override
			public void onPEFlagsLoaded(List<Long> pes, List<String> flagInteractors, List<String> dataDescs) {
				IDGPopupFactory.get().setFlagInteractors(flagInteractors);
				flaggedPhysicalEntities = pes;
				eventBus.fireEventFromSource(new SetFIFlagDataDescsEvent(dataDescs, (context != null ? context.getContent().containsEncapsulatedPathways():false)), this); //sets up flagged items control/legend with correct information
				//if pathway contains encapsulated pathways, need to load hit pathways too
				if(context.getContent().containsEncapsulatedPathways()) {
					requestPathwayFlags(term, dataDescKeys, prd);
					return;
				}
				flagObjects();
			}
			@Override
			public void onPEFlagsLoadedError(Throwable exception) {
				flaggedPhysicalEntities.clear();
				flagObjects();
			}
		});
	}
	
	private void requestPathwayFlags(String term, List<Integer> dataDescKeys, Double prd) {
		PairwiseInfoService.findPathwaysToFlag(term, dataDescKeys, prd, new PathwayEnrichmentHandler() {

			@Override
			public void onPathwaysToFlag(List<PathwayEnrichmentResult> stIds) {
				flaggedPathways = stIds;
				flagObjects();
			}

			//do nothing. still want to flag PEs
			@Override
			public void onPathwaysToFlagError() {
				flaggedPathways.clear();
				flagObjects();
			}
		});
	}

	/**
	 * Want to fire FI version of event if FI visualizer
	 */
	@Override
	public void flaggedElementsLoaded(String term, Occurrences toFlag, boolean notify) {
		Visualiser viz = ((IdgViewerContainer)viewerContainer).getActiveVisualizer();
		if(viz instanceof DiagramVisualiser || viz instanceof SVGVisualiser)
			super.flaggedElementsLoaded(term, toFlag, notify);
		else if(viz instanceof FIViewVisualizer)
			eventBus.fireEventFromSource(new FIDiagramObjectsFlaggedEvent(term, this.includeInteractors, Collections.singletonList(term), notify), this);
	}

	private void flagObjects() {
		List<Long> pes = new ArrayList<>();
		pes.addAll(this.flaggedPhysicalEntities); //want to flagg all PEs
		
		//if there are flagged encapsualted pathways, want to flag these as well. Must filter by fdr
		List<Long> dbIds = this.flaggedPathways.stream().filter(pw -> pw.getFdr() < this.flagFDR)
												.map(pw -> Long.parseLong(pw.getStId().split("R-HSA-")[1]))
												.collect(Collectors.toList());
		
		pes.addAll(dbIds);
		
		
		((IdgViewerContainer)viewerContainer).flagObjects(flagTerm, pes, this.includeInteractors);	
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
	
	private boolean doFlag(Map<String,String> tokenMap) {
		List<Integer> newDataDescKeys = Arrays.stream(tokenMap.get("DSKEYS").split(","))
				 .map(num -> Integer.parseInt(num))
				 .collect(Collectors.toList());
		Collections.sort(newDataDescKeys);
		
		if(!tokenMap.get("FLG").equals(flagTerm)
				|| (tokenMap.containsKey("SIGCUTOFF") && prd != Double.parseDouble(tokenMap.get("SIGCUTOFF"))) 
				|| !this.dataDescKeys.equals(newDataDescKeys)){
			return true;
		}
		return false;
	}

	@Override
	public void onUpdateIDGFlagPRD(UpdateIDGFlagPRDEvent event) {
		if(event.getPrd() == this.prd) return;
		
		//if widget, not handled through URL bar.
		//Need to fetch new results in this case so call fetcher
		if(DiagramFactory.WIDGET_JS) {
			this.fetchFlagInteractors(dataDescKeys, includeInteractors, event.getPrd(), flagFDR, flagTerm);
			return;
		}
		
		StateTokenHelper helper = new StateTokenHelper();
		Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
		tokenMap.put("SIGCUTOFF", event.getPrd()+"");
		History.newItem(helper.buildToken(tokenMap));
	}

	@Override
	public void onUpdateIDGFlagFDR(UpdateIDGFlagFDREvent event) {
		if(event.getFdr() == this.flagFDR) return;
		
		//if widget, not handled through URL bar.
		//just update flagFDR and return
		if(DiagramFactory.WIDGET_JS) {
			this.flagFDR = event.getFdr();
			flagObjects();
			return;
		}
		
		StateTokenHelper helper = new StateTokenHelper();
		Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
		tokenMap.put("FLGFDR", event.getFdr()+"");
		History.newItem(helper.buildToken(tokenMap));
	}
}
