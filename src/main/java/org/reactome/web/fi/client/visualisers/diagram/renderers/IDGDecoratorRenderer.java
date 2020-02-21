package org.reactome.web.fi.client.visualisers.diagram.renderers;

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

public class IDGDecoratorRenderer {

	public IDGDecoratorRenderer() {
		
	}
	
	public void doRender(AdvancedContext2d ctx, DiagramObject obj, Double factor, Coordinate offset) {
		SummaryItem summaryItem = makeSummaryItem(obj);
		//dont render if no interactions exist
		if(summaryItem.getNumber() == 0 || summaryItem.getNumber() == null) return;
		SummaryItemAbstractRenderer.draw(ctx, summaryItem, factor, offset);
	}

	private SummaryItem makeSummaryItem(DiagramObject obj) {
		SummaryItem result = new SummaryItemImpl(getShape(obj), getNumber(obj));
		
		return result;
	}

	private Shape getShape(DiagramObject obj) {
		Shape result = new ShapeImpl(getCoordinate(obj), new Double(6), "CIRCLE", true);
		return result;
	}
	
	private Coordinate getCoordinate(DiagramObject obj) {
		Node node = (Node) obj;
		double x = node.getProp().getX() + node.getProp().getWidth();
		double y = node.getProp().getY();
		Coordinate result = CoordinateFactory.get(x, y);
		return result;
	}

	private Integer getNumber(DiagramObject obj) {
		return 100; //test value for now
	}
	
}
