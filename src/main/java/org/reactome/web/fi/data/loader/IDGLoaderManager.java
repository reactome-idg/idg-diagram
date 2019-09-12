package org.reactome.web.fi.data.loader;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentLoadedEvent;
import org.reactome.web.fi.data.content.FIViewContent;

import com.google.gwt.event.shared.EventBus;


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
		super.load(identifier);
		fIViewLoader.load(identifier);
	}

	@Override
	public void onFIViewLoaded(String stId, String fIJsonPathway) {
		Context context = new Context(new FIViewContent(stId, fIJsonPathway));
		this.context = context;
		eventBus.fireEventFromSource(new ContentLoadedEvent(context), this);
	}

	@Override
	public void onFIViewLoadedError(String stId, Throwable exception) {
		// TODO Auto-generated method stub
		
	}

}
