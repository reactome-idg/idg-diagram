package org.reactome.web.fi.data.loader;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.data.loader.SVGLoader;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.fi.common.CytoscapeViewFlag;
import org.reactome.web.fi.data.content.FIViewContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

import net.sourceforge.htmlunit.corejs.javascript.json.JsonParser;

/**
 * 
 * @author brunsont
 *
 */
public class IDGLoaderManager extends LoaderManager implements FIViewLoader.Handler{

	private EventBus eventBus;
	private FIViewLoader fIViewLoader;
	
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
	
	//TODO: check if in context map before performing loading
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
        contextMap.put(context.getContent().getStableId() + ".fi", context);
		super.context = context;
		GraphObjectFactory.content = context.getContent();
		eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
	}

	@Override
	public void onFIViewLoadedError(String stId, Throwable exception) {
		GWT.log("Error loading FIView interaction data");
	}

}
