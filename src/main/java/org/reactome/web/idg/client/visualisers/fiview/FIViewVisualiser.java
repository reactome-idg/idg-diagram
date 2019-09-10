package org.reactome.web.idg.client.visualisers.fiview;

import java.util.Set;

import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.fiview.client.PathwayFIViewerImpl;
import org.reactome.web.fiview.events.CyControlActionEvent;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

public class FIViewVisualiser extends AbsolutePanel implements Visualiser{
	
	EventBus eventBus;
	PathwayFIViewerImpl fIView;
	
	public FIViewVisualiser(EventBus eventBus) {
		this.eventBus = eventBus;
		fIView = new PathwayFIViewerImpl(eventBus);
		
		this.add(new Label("Hello world"));
	}

	@Override
	public void fitDiagram(boolean animation) { /* Nothing Here */}
	
	public void fitDiagram() {
		eventBus.fireEventFromSource(new CyControlActionEvent(), this);
	}

	@Override
	public void zoomDelta(double deltaFactor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void padding(int dX, int dY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contentLoaded(Context context) {
		// TODO Auto-generated method stub
		
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
	public void setContext(Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetContext() {
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

}
