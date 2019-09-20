package org.reactome.web.fi.data.loader;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.data.content.FIViewContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;

/**
 * 
 * @author brunsont
 *
 */
public class IDGLoaderManager extends LoaderManager implements FIViewLoader.Handler{

	private EventBus eventBus;
	private FIViewLoader fIViewLoader;
	private Context context;
	
	public IDGLoaderManager(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		fIViewLoader = new FIViewLoader(this);
	}
	
	@Override
	public void cancel() {
		fIViewLoader.cancel();
		super.cancel();
	}
	
	@Override
	public void load(String identifier) {
		if(!CytoscapeViewFlag.isCytoscapeViewFlag()) 
			super.load(identifier);
		else if(CytoscapeViewFlag.isCytoscapeViewFlag()) 
			fIViewLoader.load(identifier.substring(identifier.lastIndexOf("-")+1)); //TODO: update to support multiple species
	}

	@Override
	public void onFIViewLoaded(String stId, String fIJsonPathway) {
		Context context = new Context(new FIViewContent(fIJsonPathway));
		context.getContent().setStableId(stId);
		this.context = context;
		eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
	}

	@Override
	public void onFIViewLoadedError(String stId, Throwable exception) {
		GWT.log("Error loading FIView interaction data");
	}

}
