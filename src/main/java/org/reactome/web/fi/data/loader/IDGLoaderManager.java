package org.reactome.web.fi.data.loader;


import java.util.List;

import org.reactome.web.diagram.data.ContentFactory;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.data.loader.SVGLoader;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.diagram.events.DiagramInternalErrorEvent;
import org.reactome.web.diagram.events.InteractorsLoadedEvent;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.data.content.FIViewContent;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.events.FIViewMessageEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;
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
OverlayLoader.Handler{

	private EventBus eventBus;
	private FIViewLoader fIViewLoader;
	private OverlayLoader overlayLoader;
	
	private final String SPECIES = "Homo sapiens";
			
	
	public IDGLoaderManager(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		fIViewLoader = new FIViewLoader(this);
		overlayLoader = new OverlayLoader();
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
		if(SVGLoader.isSVGAvailable(identifier))
			return false;
		if(!CytoscapeViewFlag.isCytoscapeViewFlag())
			return false;
		return true;
	}

	@Override
	public void onFIViewLoaded(String stId, String dbId, String fIJsonPathway) {

		//ensure json string recieved from corews server is not null
		//if null, set CytoscapeViewFlag to false and load the normal diagram view
		if(fIJsonPathway == "null"){
			CytoscapeViewFlag.ensureCytoscapeViewFlagFalse();
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

	@Override
	public void onFIViewLoadedError(String stId, Throwable exception) {
		eventBus.fireEventFromSource(new FIViewMessageEvent(false), this);
		eventBus.fireEventFromSource(
                new DiagramInternalErrorEvent("There was a problem while loading the Functional Interactions",
                        "Layout content error: " + exception.getMessage()
                ), this);
	}
	
	@Override 
	public void onDataOverlayLoaded(DataOverlay dataOverlay) {
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
			public void onPairwiseNumbersLoaded(RawInteractors result) {
				ContentFactory.fillInteractorsContent(context, result);
				eventBus.fireEventFromSource(new InteractorsLoadedEvent(result, new Long(1)), this);
				PairwiseOverlayFactory.get().setInteractorEntities(result);
			}
			@Override
			public void onPairwiseLoaderError(Throwable exception) {
				Console.error(exception.getMessage());

			}
		});
	}
	
	@Override
	public void interactorsLoaded(RawInteractors interactors, long time) {
		if(PairwiseOverlayFactory.get().getRawInteractors() !=null && 
		   PairwiseOverlayFactory.get().getRawInteractors().getEntities().size() > 0) {
			ContentFactory.fillInteractorsContent(context, PairwiseOverlayFactory.get().getRawInteractors());
			eventBus.fireEventFromSource(new InteractorsLoadedEvent(PairwiseOverlayFactory.get().getRawInteractors(), new Long(1)), this);
		}
		return;
	}
}
