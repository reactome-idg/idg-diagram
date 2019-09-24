package org.reactome.web.fi.client.visualisers.fiview;

import org.reactome.web.fi.events.EdgeClickedEvent;
import org.reactome.web.fi.events.EdgeHoveredEvent;
import org.reactome.web.fi.events.EdgeMouseOutEvent;
import org.reactome.web.fi.events.NodeClickedEvent;
import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper;

import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class CytoscapeEntity extends CytoscapeWrapper{

	EventBus eventBus;
	String baseStyle;
	
	public CytoscapeEntity(EventBus eventBus, String baseStyle) {
		super(eventBus);
		this.eventBus = eventBus;
		this.baseStyle = baseStyle;
	}

	@Override
	protected native void nodeSelected() /*-{
		var that = this;
		$wnd.cy.elements('node').on('tap', function(evt){
			var id = evt.target.id();
			var shortName = evt.target.json().data.shortName;
			var ele = evt.target;
			
			$wnd.cy.style().selector('edge[target = "'+id+'"], edge[source="'+id+'"]').style({'line-color': 'red'}).update();
			
			that.@org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity::fireNodeClickedEvent(*)(id, shortName);
		});
		
		$wnd.cy.elements('node').on('tapunselect', function(evt){
			var id = evt.target.id();
			$wnd.cy.style().selector('edge[target = "'+id+'"], edge[source="'+id+'"]').style({'line-color': 'black'}).update();
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
			var reactomeSources = JSON.stringify(evt.target.json().data.reactomeSources);
			that.@org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity::fireEdgeClickedEvent(*)(sourceName, targetName, direction, reactomeSources);
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
			var reactomeSources = JSON.stringify(evt.target.json().data.reactomeSources);
			that.@org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity::fireEdgeHoveredEvent(*)(interactionDirection, reactomeSources);
		});
		$wnd.cy.elements('edge').on('mouseout', function(evt){
			that.@org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity::fireEdgeMouseOutEvent(*)();
		});
	}-*/; 
	
	//reset so no nodes are selected
	public native void resetSelection() /*-{
		var styleJSON = $wnd.JSON.parse(this.@org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity::baseStyle);
		$wnd.cy.style().fromJson(styleJSON).update();
	}-*/;

	public native void hierarchySelect(String id) /*-{
		$wnd.cy.edges().unselect();
		$wnd.cy.edges('[reactomeId = "' + id +'"]').select();
		
	}-*/;
	
	private void fireNodeClickedEvent(String id, String shortName) {
		eventBus.fireEventFromSource(new NodeClickedEvent(id, shortName), this);
	}
	
	private void fireEdgeClickedEvent(String sourceName, String targetName, String direction, String reactomeSources) {
		eventBus.fireEventFromSource(new EdgeClickedEvent(sourceName, targetName, direction, reactomeSources), this);
	}
	
	private void fireEdgeHoveredEvent(String interactionDirection, String reactomeSources) {
		eventBus.fireEventFromSource(new EdgeHoveredEvent(interactionDirection, reactomeSources), this);
	}
	
	private void fireEdgeMouseOutEvent() {
		eventBus.fireEventFromSource(new EdgeMouseOutEvent(), this);
	}	
}
