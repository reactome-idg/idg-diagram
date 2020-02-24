package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.renderers.layout.abs.SummaryItemAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.fi.data.layout.ShapeImpl;
import org.reactome.web.fi.data.layout.SummaryItemImpl;

/**
 * 
 * @author brunsont
 *
 */
public class IDGDecoratorRenderer {

	public IDGDecoratorRenderer() {/* Nothing Here */}
	
	/**
	 * performs render of decorator on a passed in diagramObject in the diagram view.
	 * @param ctx
	 * @param obj
	 * @param factor
	 * @param offset
	 */
	public void doRender(AdvancedContext2d ctx, DiagramObject obj, Double factor, Coordinate offset) {
		SummaryItem summaryItem = makeSummaryItem(obj);
		//dont render if no interactions exist
		if(summaryItem.getNumber() == 0 || summaryItem.getNumber() == null) return;
		SummaryItemAbstractRenderer.draw(ctx, summaryItem, factor, offset);
	}

	/**
	 * Gets summary item needed to render decorator
	 * @param obj
	 * @return
	 */
	private SummaryItem makeSummaryItem(DiagramObject obj) {
		SummaryItem result = new SummaryItemImpl(getShape(obj), getNumber(obj));
		
		return result;
	}

	/**
	 * Makes shape for decorator. Always returns a circle with a radius of 6px.
	 * @param obj
	 * @return
	 */
	private Shape getShape(DiagramObject obj) {
		Shape result = new ShapeImpl(getCoordinate(obj), new Double(6), "CIRCLE", true);
		return result;
	}
	
	/**
	 * Gets coordinate for decorator
	 * @param obj
	 * @return
	 */
	private Coordinate getCoordinate(DiagramObject obj) {
		Node node = (Node) obj;
		double x = node.getProp().getX() + node.getProp().getWidth();
		double y = node.getProp().getY();
		Coordinate result = CoordinateFactory.get(x, y);
		return result;
	}
	
	/**
	 * Get number of interactors for all protein participants of a given complex or Entity Set
	 */
	private Integer getNumber(DiagramObject obj) {
		int result = 0;
		
		GraphPhysicalEntity entity = obj.getGraphObject(); //should always be GraphPhysicalEntity
		Set<GraphPhysicalEntity> peSet = entity.getParticipants();
		for(GraphPhysicalEntity pe : peSet) {
			for(DiagramObject diagramObject : pe.getDiagramObjects()) {
				Node node = (Node)diagramObject;
				if(node.getDiagramEntityInteractorsSummary() != null) {
					result += node.getDiagramEntityInteractorsSummary().getNumber();
				}
			}
		}
		
		return result;
	}
	
}
