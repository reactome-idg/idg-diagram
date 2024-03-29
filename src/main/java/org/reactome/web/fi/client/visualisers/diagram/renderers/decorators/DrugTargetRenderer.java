package org.reactome.web.fi.client.visualisers.diagram.renderers.decorators;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.reactome.web.fi.data.model.drug.Drug;
import org.reactome.web.fi.events.DrugTargetsLoadedEvent;

import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetRenderer{

	private EventBus eventBus;
	private Set<SummaryItem> currentItems;
	private Map<String, Integer> uniprotToInteractionNumber;
	
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
		if(uniprotToInteractionNumber == null) uniprotToInteractionNumber = new HashMap<>();
		processDrugNumbers(event.getDrugTargets());
		for(DiagramObject obj : event.getContext().getContent().getDiagramObjects()) {
			if(obj.getRenderableClass() != "Complex" && 
			   obj.getRenderableClass() != "EntitySet" &&
			   obj.getRenderableClass() != "Protein") continue;
			
			SummaryItem drugTargetItem = makeItem(obj);
			
			if(drugTargetItem.getNumber() == 0) continue;
			drugTargetItem.setLabel(drugTargetItem.getNumber() + " drug interactions");
			currentItems.add(drugTargetItem);
			
			Node node = (Node) obj;
			List<SummaryItem> otherInteractorList = node.getOtherDecoratorsList();
			if(otherInteractorList == null) otherInteractorList = new ArrayList<>(); 
			otherInteractorList.add(drugTargetItem);
			node.setOtherDecoratorsList(otherInteractorList);
		}
	}

	private void processDrugNumbers(Collection<Drug> drugTargets) {
		drugTargets.forEach(drug -> {
			drug.getDrugInteractions().keySet().forEach(k -> {
				//for each drug Interaction, put uniprot mapped to 1 
				//or 1+ the current number for that uniprot if it exists on the map already.
				uniprotToInteractionNumber.put(k,
						uniprotToInteractionNumber.get(k) == null ? 1 :
						uniprotToInteractionNumber.get(k) + 1);
			});
		});
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
		if(entity == null) return 0; //sometimes DiagramObject doesn't have an associated graphObject so return 0
		for(GraphPhysicalEntity pe : entity.getParticipants()) {
			String identifier = pe.getIdentifier();
			if(identifier == null) continue;
			if(identifier.contains("ENSG")) {
				int index = pe.getDisplayName() == null ? 0 : pe.getDisplayName().indexOf(" ");
				identifier = geneToUniprotMap.get(pe.getDisplayName().substring(0, index));
			}
			result += uniprotToInteractionNumber.get(identifier) != null 
					? uniprotToInteractionNumber.get(identifier) : 0;
		}
		
		return result;
	}

	public void contentRequested() {
		currentItems = null;
		uniprotToInteractionNumber = null;
	}
}
