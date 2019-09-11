package org.reactome.web.idg.client.visualisers.fiview;

import org.reactome.web.idg.events.EdgeClickedEvent;
import org.reactome.web.idg.events.EdgeHoveredEvent;
import org.reactome.web.idg.events.EdgeMouseOutEvent;
import org.reactome.web.idg.events.NodeClickedEvent;
import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper;

import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class CytoscapeEntity extends CytoscapeWrapper{

	EventBus eventBus;
	
	public CytoscapeEntity(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
	}

	@Override
	protected native void nodeSelected() /*-{
		var that = this;
		$wnd.cy.elements('node').on('tap', function(evt){
			var id = evt.target.id();
			var shortName = evt.target.json().data.shortName;
			that.@org.reactome.web.idg.client.visualisers.fiview.CytoscapeEntity::fireNodeClickedEvent(*)(id, shortName);
		});
	}-*/;
	
	@Override
	protected native void edgeSelected() /*-{
		var that = this;
		$wnd.cy.elements('edge').on('tap', function(evt){
			var sourceNode = $wnd.cy.elements('node#' + evt.target.json().data.source);
			var sourceName = sourceNode.json().data.shortName;
			var targetNode = $wnd.cy.elements('node#' +evt.target.json().data.target);
			var targetName = targetNode.json().data.shortName
			var direction = evt.target.json().data.direction;
			var reactomeSources = evt.target.json().data.reactomeSources.reactomeId; //TODO: check if this returns multiple if reactomeSources is an array
			that.@org.reactome.web.idg.client.visualisers.fiview.CytoscapeEntity::fireEdgeClickedEvent(*)(sourceName, targetName, direction, reactomeSources);
		});
	}-*/;
	
	/**
	 * passes data to edge hovered event
	 */
	@Override
	protected native void edgeHovered() /*-{
		var that = this;
		$wnd.cy.elements('edge').on('mouseover', function(evt){
			var sourceNode = $wnd.cy.elements('node#' + evt.target.json().data.source);
			var sourceName = sourceNode.json().data.shortName;
			var targetNode = $wnd.cy.elements('node#' + evt.target.json().data.target);
			var targetName = targetNode.json().data.shortName
			var direction = evt.target.json().data.direction;
			var interactionDirection = sourceName + ' ' + direction + ' ' + targetName;
			that.@org.reactome.web.idg.client.visualisers.fiview.CytoscapeEntity::fireEdgeHoveredEvent(*)(interactionDirection);
		});
		$wnd.cy.elements('edge').on('mouseout', function(evt){
			that.@org.reactome.web.idg.client.visualisers.fiview.CytoscapeEntity::fireEdgeMouseOutEvent(*)();
		});
	}-*/;

	private void fireNodeClickedEvent(String id, String shortName) {
		eventBus.fireEventFromSource(new NodeClickedEvent(id, shortName), this);
	}
	
	private void fireEdgeClickedEvent(String sourceName, String targetName, String direction, String reactomeSources) {
		eventBus.fireEventFromSource(new EdgeClickedEvent(sourceName, targetName, direction, reactomeSources), this);
	}
	
	private void fireEdgeHoveredEvent(String interactionDirection) {
		eventBus.fireEventFromSource(new EdgeHoveredEvent(interactionDirection), this);
	}
	
	private void fireEdgeMouseOutEvent() {
		eventBus.fireEventFromSource(new EdgeMouseOutEvent(), this);
	}
	

	
}
