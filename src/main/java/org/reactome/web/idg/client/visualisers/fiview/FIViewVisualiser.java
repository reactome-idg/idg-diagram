package org.reactome.web.idg.client.visualisers.fiview;

import java.util.Set;

import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.idg.events.EdgeClickedEvent;
import org.reactome.web.idg.events.EdgeHoveredEvent;
import org.reactome.web.idg.events.EdgeMouseOutEvent;
import org.reactome.web.idg.events.NodeClickedEvent;
import org.reactome.web.idg.handlers.EdgeClickedHandler;
import org.reactome.web.idg.handlers.EdgeHoveredHandler;
import org.reactome.web.idg.handlers.EdgeMouseOutHandler;
import org.reactome.web.idg.handlers.NodeClickedHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class FIViewVisualiser extends AbsolutePanel implements Visualiser,
	EdgeClickedHandler, EdgeHoveredHandler, EdgeMouseOutHandler, NodeClickedHandler{
	
	private EventBus eventBus;
	private CytoscapeEntity cy;
	
	private Context context;
	
	private boolean initialised;
    private int viewportWidth = 0;
    private int viewportHeight = 0;
	
	public FIViewVisualiser(EventBus eventBus) {
		super();
		this.getElement().addClassName("pwp-FIViz");
		this.eventBus = eventBus;
		
		initHandlers();
	}
	
	protected void initialise() {
		if(!initialised) {
			this.initialised = true;
			
			cy = new CytoscapeEntity(this.eventBus);
			
			SimplePanel cyView =  new SimplePanel();
			cyView.getElement().setId("cy");
			cyView.setSize("100%", "100%");
			
			this.add(cyView);
			
			this.viewportWidth = getParent().getOffsetWidth();
			this.viewportHeight = getParent().getOffsetHeight();
			this.setWidth(viewportWidth + "px");
			this.setHeight(viewportHeight+ "px");
			
		}
	}
	
	private void initHandlers() {
		eventBus.addHandler(EdgeClickedEvent.TYPE, this);
		eventBus.addHandler(EdgeHoveredEvent.TYPE, this);
		eventBus.addHandler(EdgeMouseOutEvent.TYPE, this);
		eventBus.addHandler(NodeClickedEvent.TYPE, this);
	}

	@Override
	public void fitDiagram(boolean animation) { 
		cy.fitCytoscape();
	}

	@Override
	public void zoomDelta(double deltaFactor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomIn() {
		cy.zoomCytoscape(1);
	}

	@Override
	public void zoomOut() {
		cy.zoomCytoscape(-1);
	}

	@Override
	public void padding(int dX, int dY) { 
		
	}

	@Override
	public void exportView() {
		// TODO Auto-generated method stub
		
	}

  @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(() -> initialise());
    }
	
	@Override
	public void setContext(Context context) {
		this.context = context;
		
	}

	@Override
	public void resetContext() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void contentLoaded(Context context) {
		setContext(context);
	}

	@Override
	public void contentRequested() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void highlightInteractor(DiagramInteractor diagramInteractor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean resetHighlight(boolean notify) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resetSelection(boolean notify) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean selectGraphObject(GraphObject graphObject, boolean notify) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GraphObject getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadAnalysis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetAnalysis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expressionColumnChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsCollapsed(String resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsFiltered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsLayoutUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsLoaded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsResourceChanged(OverlayResource resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flagItems(Set<DiagramObject> flaggedItems, Boolean includeInteractors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetFlag() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeClicked(NodeClickedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeMouseOut(EdgeMouseOutEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeHovered(EdgeHoveredEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeClicked(EdgeClickedEvent event) {
		// TODO Auto-generated method stub
		
	}

}
