package org.reactome.web.fi.data.loader;

import java.util.Collection;
import java.util.Map;

import org.reactome.web.diagram.data.ContentFactory;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.data.loader.SVGLoader;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.DiagramInternalErrorEvent;
import org.reactome.web.diagram.events.InteractorsLoadedEvent;
import org.reactome.web.fi.data.content.FIViewContent;
import org.reactome.web.fi.data.manager.StateTokenHelper;
import org.reactome.web.fi.data.model.drug.Drug;
import org.reactome.web.fi.data.model.interactors.RawInteractorsImpl;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.events.DrugTargetsLoadedEvent;
import org.reactome.web.fi.events.FIViewMessageEvent;
import org.reactome.web.fi.events.NoFIsAvailableEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.PairwiseNumbersLoadedEvent;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author brunsont
 *
 */
public class IDGLoaderManager extends LoaderManager implements FIViewLoader.Handler,
TCRDDataLoader.Handler{

	private EventBus eventBus;
	private FIViewLoader fIViewLoader;
	private TCRDDataLoader overlayLoader;
	
	private final String SPECIES = "Homo sapiens";
			
	
	public IDGLoaderManager(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		fIViewLoader = new FIViewLoader(this);
		layoutLoader = new IDGLayoutLoader(this);
		overlayLoader = new TCRDDataLoader();
	}
	
	@Override
	public void cancel() {
		fIViewLoader.cancel();
		super.cancel();
	}
	
	@Override
	public void load(String identifier) {
		if (isFIViewNeeded(identifier)) {
			context = contextMap.get(identifier + ".fi");
			eventBus.fireEventFromSource(new FIViewMessageEvent(true), this);
			if(context != null) {
				GraphObjectFactory.content = context.getContent();
				eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
			}
			else {
				fIViewLoader.load(identifier, identifier.substring(identifier.lastIndexOf("-")+1));
			}
		}
		else
			super.load(identifier);
	}
	
	/**
	 * Directs loading of TCRD data.
	 * @param postData
	 */
	public void loadTCRDData(DataOverlayProperties properties) { 
		overlayLoader.load(properties, this);
	}
	
	private boolean isFIViewNeeded(String identifier) {
		StateTokenHelper helper = new StateTokenHelper();
		
		if(SVGLoader.isSVGAvailable(identifier))
			return false;
		if(!helper.buildTokenMap(History.getToken()).containsKey("FIVIZ"))
			return false;
		return true;
	}

	@Override
	public void onFIViewLoaded(String stId, String dbId, String fIJsonPathway) {

		//ensure json string recieved from corews server is not null
		//if null, set CytoscapeViewFlag to false and load the normal diagram view
		if(fIJsonPathway == "null" || fIJsonPathway == null){
//			CytoscapeViewFlag.ensureCytoscapeViewFlagFalse();
			
			//remove FI viz from URL
			StateTokenHelper helper = new StateTokenHelper();
			Map<String, String> tokenMap = helper.buildTokenMap(History.getToken());
			tokenMap.remove("FIVIZ");
			History.newItem(helper.buildToken(tokenMap));
			
			
			eventBus.fireEventFromSource(new FIViewMessageEvent(false), this);
			eventBus.fireEventFromSource(new NoFIsAvailableEvent(), this);
			load(stId);
			return;
		}
		
		Context context = new Context(new FIViewContent(fIJsonPathway));
		context.getContent().setStableId(stId);
		context.getContent().setDbId(Long.parseLong(dbId));
		context.getContent().setSpeciesName(SPECIES); //TODO: make species flexible
        contextMap.put(context.getContent().getStableId() + ".fi", context);
		super.context = context;
		eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
	}

	/**
	 * Defaulting back to diagram view if FI service is down for some reason.
	 */
	@Override
	public void onFIViewLoadedError(String stId, Throwable exception) {
		eventBus.fireEventFromSource(new FIViewMessageEvent(false), this);
		eventBus.fireEventFromSource(new NoFIsAvailableEvent(), this);
		load(stId);
	}
	
	@Override 
	public void onDataOverlayLoaded(DataOverlay dataOverlay) {
		if(dataOverlay == null) return;
		eventBus.fireEventFromSource(new OverlayDataLoadedEvent(dataOverlay), this);
	}
	
	@Override
	public void onOverlayLoadedError(Throwable exception) {
		GWT.log("onTargetLevelLoadedError: " + exception.getMessage());
	}

	public void loadPairwiseOverlayCounts(PairwiseOverlayProperties pairwiseOverlayProperties) {
		PairwiseDataLoader loader = new PairwiseDataLoader();
		loader.loadPairwiseData(pairwiseOverlayProperties, true, new PairwiseDataLoader.Handler() {
			@Override
			public void onPairwiseNumbersLoaded(RawInteractors result, PairwiseNumberEntities entities, Map<String, Integer> geneToTotalMap) {
				ContentFactory.fillInteractorsContent(context, result);
				
				INTERACTORS_RESOURCE = new OverlayResource(result.getResource(), "Pairwise/Interactors", OverlayResource.ResourceType.CUSTOM);
				
				eventBus.fireEventFromSource(new InteractorsLoadedEvent(result, new Long(1)), this);
				IDGPopupFactory.get().setupNewOverlay(result, entities.getPairwiseNumberEntities(), geneToTotalMap);
				eventBus.fireEventFromSource(new PairwiseNumbersLoadedEvent(context, geneToTotalMap), this);
			}
			@Override
			public void onPairwiseLoaderError(Throwable exception) {
				Console.error(exception.getMessage());
			}
		});
	}
	
	@Override
	public void interactorsLoaded(RawInteractors interactors, long time) {
		if(IDGPopupFactory.get().getRawInteractors() !=null && 
		   IDGPopupFactory.get().getRawInteractors().getEntities().size() > 0) {
			ContentFactory.fillInteractorsContent(context, IDGPopupFactory.get().getRawInteractors());
			eventBus.fireEventFromSource(new InteractorsLoadedEvent(IDGPopupFactory.get().getRawInteractors(), new Long(1)), this);
			IDGPopupFactory.get().setInteractorEntities(new RawInteractorsImpl("Empty", null));
		}
		return;
	}

	public void loadDrugTargets(String uniprots) {
		overlayLoader.loadDrugTargetsForUniprots(uniprots, new AsyncCallback<Collection<Drug>>() {
			@Override
			public void onSuccess(Collection<Drug> result) {
				IDGPopupFactory.get().setDrugTargets(result);
				eventBus.fireEventFromSource(new DrugTargetsLoadedEvent(context,result), this);			
			}
			@Override
			public void onFailure(Throwable caught) {
				Console.error(caught.getMessage());
			}
		});
	}
}
