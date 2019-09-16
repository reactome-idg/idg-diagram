package org.reactome.web.fi.client;

import org.reactome.web.diagram.client.DiagramViewerImpl;
import org.reactome.web.diagram.client.ViewerContainer;
import org.reactome.web.diagram.data.loader.LoaderManager;
import org.reactome.web.diagram.events.ContentRequestedEvent;
import org.reactome.web.fi.client.flag.CytoscapeViewFlag;
import org.reactome.web.fi.data.loader.IDGLoaderManager;
import org.reactome.web.fi.events.CytoscapeToggledEvent;
import org.reactome.web.fi.handlers.CytoscapeToggledHandler;

/**
 * 
 * @author brunsont
 *
 */
public class IdgDiagramViewerImpl extends DiagramViewerImpl implements CytoscapeToggledHandler{
	
	public IdgDiagramViewerImpl() {
		super();
		
		eventBus.addHandler(CytoscapeToggledEvent.TYPE, this);
		
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
		if (CytoscapeViewFlag.isCytoscapeViewFlag())
			load(event.getContext().getContent().getStableId());
		else {
			eventBus.fireEventFromSource(new ContentRequestedEvent(event.getContext().getContent().getStableId()), this);
		}
	}
}
