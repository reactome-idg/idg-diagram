package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
import org.reactome.web.diagram.data.layout.Shape;
import org.reactome.web.diagram.data.layout.impl.CoordinateFactory;
import org.reactome.web.fi.data.layout.DrugTargetItem;
import org.reactome.web.fi.data.layout.DrugTargetItemImpl;
import org.reactome.web.fi.data.layout.ShapeImpl;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.model.drug.DrugTargetEntity;
import org.reactome.web.fi.events.DrugTargetsLoadedEvent;
import org.reactome.web.fi.handlers.DrugTargetsLoadedHandler;

import com.google.gwt.event.shared.EventBus;

public class DrugTargetRenderer implements DrugTargetsLoadedHandler{

	private EventBus eventBus;
	private Map<String, List<DrugTargetEntity>> uniprotToDrugTargetEntityMap;
	private Set<DrugTargetItem> currentItems;
	
	public DrugTargetRenderer(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.addHandler(DrugTargetsLoadedEvent.TYPE, this);
	}

	public void doRender() {
		
	}
	
	@Override
	public void onDrugTargetsLoaded(DrugTargetsLoadedEvent event) {
		for(DiagramObject obj : event.getContext().getContent().getDiagramObjects()) {
			if(obj.getRenderableClass() != "Complex" || 
			   obj.getRenderableClass() != "EntitySet" ||
			   obj.getRenderableClass() != "Protein") continue;
			
			DrugTargetItem drugTargetItem = makeItem(obj);
			
			if(drugTargetItem.getNumber() == 0) continue;
			currentItems.add(drugTargetItem);
		}
	}

	private DrugTargetItem makeItem(DiagramObject obj) {
		DrugTargetItem result = new DrugTargetItemImpl(getShape(obj), getNumber(obj));
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
