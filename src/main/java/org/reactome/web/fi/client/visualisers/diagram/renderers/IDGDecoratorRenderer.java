package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.SummaryItem;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.renderers.layout.abs.SummaryItemAbstractRenderer;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.fi.data.layout.ShapeImpl;
import org.reactome.web.fi.data.layout.SummaryItemImpl;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntity;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;

/**
 * 
 * @author brunsont
 *
 */
public class IDGDecoratorRenderer {
		
	
	public IDGDecoratorRenderer() {}
	
	/**
	 * performs render of decorator on a passed in diagramObject in the diagram view.
	 * @param ctx
	 * @param obj
	 * @param factor
	 * @param offset
	 */
	public void doRender(AdvancedContext2d ctx, DiagramObject obj, Double factor, Coordinate offset) {
		SummaryItem summaryItem = makeSummaryItem(obj, factor, offset);
		//dont render if no interactions exist
		if(summaryItem.getNumber() == 0 || summaryItem.getNumber() == null) return;
		
		Node node = (Node) obj;
		
		node.setInteractorsSummary(summaryItem);
		InteractorsSummary summary = new InteractorsSummary("test", obj.getId(), summaryItem.getNumber());
		node.setDiagramEntityInteractorsSummary(summary);
		
		ctx.save();
		ctx.setGlobalAlpha((factor - 0.5) * 2);		
		ctx.setFillStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
		SummaryItemAbstractRenderer.draw(ctx, node.getInteractorsSummary(), factor, offset);
		ctx.restore();
	}

	/**
	 * Gets summary item needed to render decorator
	 * @param obj
	 * @return
	 */
	private SummaryItem makeSummaryItem(DiagramObject obj, Double factor, Coordinate offset) {
		SummaryItem result = new SummaryItemImpl(getShape(obj, factor, offset), getNumber(obj));
		
		return result;
	}

	/**
	 * Makes shape for decorator. Always returns a circle with a radius of 6px.
	 * @param obj
	 * @return
	 */
	private Shape getShape(DiagramObject obj, Double factor, Coordinate offset) {
		Shape result = new ShapeImpl(getCoordinate(obj, factor, offset), new Double(6), "CIRCLE", true);
		return result;
	}
	
	/**
	 * Gets coordinate for decorator
	 * @param obj
	 * @return
	 */
	private Coordinate getCoordinate(DiagramObject obj, Double factor, Coordinate offset) {
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
			String identifier = pe.getIdentifier();
			
			if(identifier == null ) continue; //ensure GraphPhysicalEntity contains an identifier. if not, continue
			if(identifier.contains("ENSG")) {
				int index = pe.getDisplayName() == null ? 0 : pe.getDisplayName().indexOf(" ");
				identifier = PairwiseInfoService.getGeneToUniprotMap().get(pe.getDisplayName().substring(0, index));
			}
			
			for(PairwiseNumberEntity numberEntity : PairwiseOverlayFactory.get().getPairwiseNumberEntities()) {
				if(identifier == numberEntity.getGene()) {
					result += (numberEntity.getPosNum() + numberEntity.getNegNum());
				}
			}
			
		}
		return result;
	}
	
}
