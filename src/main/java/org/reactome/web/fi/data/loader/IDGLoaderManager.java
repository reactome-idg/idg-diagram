package org.reactome.web.fi.data.loader;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.data.loader.SVGLoader;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.data.content.FIViewContent;
import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class IDGLoaderManager extends LoaderManager implements FIViewLoader.Handler,
TCRDLoader.Handler{

	private EventBus eventBus;
	private FIViewLoader fIViewLoader;
	private TCRDLoader tcrdLoader;
	
	private final String SPECIES = "Homo sapiens";
			
	
	public IDGLoaderManager(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		fIViewLoader = new FIViewLoader(this);
		tcrdLoader = new TCRDLoader(this);
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
			if(context != null) 
				eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
			else
				fIViewLoader.load(identifier, identifier.substring(identifier.lastIndexOf("-")+1));
		}
		else
			super.load(identifier);
	}
	
	/**
	 * Directs loading of TCRD target level data for an input of ids.
	 * Multiple ids should be passed in as a String separated by commas.
	 * @param postData
	 */
	public void loadTCRDTargetLevel(String postData, OverlayDataType type) { 
		tcrdLoader.load(postData, type);
	}
	
	private boolean isFIViewNeeded(String identifier) {
		if (SVGLoader.isSVGAvailable(identifier))
			return false;
		if (!CytoscapeViewFlag.isCytoscapeViewFlag())
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
		GraphObjectFactory.content = context.getContent();
		eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
	}

	@Override
	public void onFIViewLoadedError(String stId, Throwable exception) {
		GWT.log("Error loading FIView interaction data: " + exception.getMessage());
	}
	
	@Override 
	public void onTargetLevelLoaded(OverlayEntities entities, DataOverlay dataOverlay) {
		eventBus.fireEventFromSource(new OverlayDataLoadedEvent(entities), this);
	}
	
	@Override
	public void onTargetLevelLoadedError(Throwable exception) {
		GWT.log("onTargetLevelLoadedError: " + exception.getMessage());
	}

}
