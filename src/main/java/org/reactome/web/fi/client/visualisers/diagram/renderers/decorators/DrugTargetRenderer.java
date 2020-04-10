package org.reactome.web.fi.client.visualisers.diagram.renderers.decorators;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
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
import org.reactome.web.fi.data.model.drug.DrugTargetEntity;
import org.reactome.web.fi.events.DrugTargetsLoadedEvent;

import com.google.gwt.event.shared.EventBus;

public class DrugTargetRenderer{

	private EventBus eventBus;
	private Map<String, List<DrugTargetEntity>> uniprotToDrugTargetEntityMap;
	private Set<SummaryItem> currentItems;
	
	public DrugTargetRenderer(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void doRender(AdvancedContext2d ctx, Double factor, Coordinate offset) {
		if(currentItems == null) return;
		currentItems.forEach(x -> {
			ctx.save();
			ctx.setGlobalAlpha((factor-.5)*2);
			ctx.setFillStyle(DiagramColours.get().PROFILE.getProperties().getSelection());
			SummaryItemAbstractRenderer.draw(ctx, x, factor, offset);
			ctx.restore();
		});
	}
	
	public void onDrugTargetsLoaded(DrugTargetsLoadedEvent event) {
		currentItems = new HashSet<>();
		this.uniprotToDrugTargetEntityMap = event.getDrugTaretEntityMap();
		for(DiagramObject obj : event.getContext().getContent().getDiagramObjects()) {
			if(obj.getRenderableClass() != "Complex" && 
			   obj.getRenderableClass() != "EntitySet" &&
			   obj.getRenderableClass() != "Protein") continue;
			
			SummaryItem drugTargetItem = makeItem(obj);
			
			if(drugTargetItem.getNumber() == 0) continue;
			currentItems.add(drugTargetItem);
			
			Node node = (Node) obj;
			List<SummaryItem> otherInteractorList = node.getOtherDecoratorsList();
			if(otherInteractorList == null) otherInteractorList = new ArrayList<>(); 
			otherInteractorList.add(drugTargetItem);
			node.setOtherDecoratorsList(otherInteractorList);
		}
	}

	private SummaryItem makeItem(DiagramObject obj) {
		SummaryItem result = new SummaryItemImpl(getShape(obj), getNumber(obj));
		((SummaryItemImpl)result).setType("DG");
		return result;
	}

	private Shape getShape(DiagramObject obj) {
		Shape result = new ShapeImpl(getCoordinate(obj), new Double(6), "CIRCLE", true);
		return result;
	}

	/**
	 * return coordinate that is top left corner of diagram object
	 * @param obj
	 * @return
	 */
	private Coordinate getCoordinate(DiagramObject obj) {
		Node node = (Node) obj;
		double x = node.getProp().getX();
		double y = node.getProp().getY();
		Coordinate result = CoordinateFactory.get(x, y);
		return result;
	}
	
	private Integer getNumber(DiagramObject obj) {
		int result = 0;
		Map<String, String> geneToUniprotMap = PairwiseInfoService.getGeneToUniprotMap();
		
		GraphPhysicalEntity entity = obj.getGraphObject();
		for(GraphPhysicalEntity pe : entity.getParticipants()) {
			String identifier = pe.getIdentifier();
			if(identifier == null) continue;
			if(identifier.contains("ENSG")) {
				int index = pe.getDisplayName() == null ? 0 : pe.getDisplayName().indexOf(" ");
				identifier = geneToUniprotMap.get(pe.getDisplayName().substring(0, index));
			}
			result += uniprotToDrugTargetEntityMap.get(identifier) != null 
					? uniprotToDrugTargetEntityMap.get(identifier).size() : 0;
		}
		
		return result;
	}
}
