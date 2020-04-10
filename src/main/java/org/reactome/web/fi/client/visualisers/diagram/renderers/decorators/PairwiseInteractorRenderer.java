package org.reactome.web.fi.client.visualisers.diagram.renderers.decorators;

import java.util.Map;
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
import org.reactome.web.fi.events.PairwiseNumbersLoadedEvent;

import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseInteractorRenderer {
		
	private EventBus eventBus;
	private Map<String,Integer> currentTotalsMap;
	
	public PairwiseInteractorRenderer(EventBus eventBus) {
		this.eventBus = eventBus;	
	}
	
	/**
	 * performs render of decorator on a passed in diagramObject in the diagram view.
	 * @param ctx
	 * @param obj
	 * @param factor
	 * @param offset
	 */
	public void doRender(AdvancedContext2d ctx, DiagramObject obj, Double factor, Coordinate offset) {
		Node node = (Node) obj;
		ctx.save();
		ctx.setGlobalAlpha((factor - 0.5) * 2);		
		ctx.setFillStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
		SummaryItemAbstractRenderer.draw(ctx, node.getInteractorsSummary(), factor, offset);
		ctx.restore();
	}

	/**
	 * Called when new set of interactors are chosen and loads correct summary items for
	 * complexes and entity sets
	 */
	public void onPairwiseNumbersLoaded(PairwiseNumbersLoadedEvent event) {
		this.currentTotalsMap = event.getGeneToTotalMap();

		for(DiagramObject obj : event.getContext().getContent().getDiagramObjects()) {
			if(obj.getRenderableClass() != "Complex" && obj.getRenderableClass() != "EntitySet") continue;
			Node node = (Node) obj;
			SummaryItem summaryItem = makeSummaryItem(obj);
			
			if(summaryItem.getNumber() == 0) continue;
			node.setInteractorsSummary(summaryItem);
			InteractorsSummary summary = new InteractorsSummary("test", obj.getId(), summaryItem.getNumber());
			node.setDiagramEntityInteractorsSummary(summary);
		}
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
			String identifier = pe.getIdentifier();
			
			if(identifier == null) continue; //ensure GraphPhysicalEntity contains an identifier. if not, continue
			if(identifier.contains("ENSG")) {
				int index = pe.getDisplayName() == null ? 0 : pe.getDisplayName().indexOf(" ");
				identifier = PairwiseInfoService.getGeneToUniprotMap().get(pe.getDisplayName().substring(0, index));
			}
			
			result += currentTotalsMap.get(identifier) != null ? currentTotalsMap.get(identifier): 0;
			
		}
		return result;
	}
}
