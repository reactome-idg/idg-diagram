package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphProteinDrug;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.RenderOtherContextDialogInfoEvent;
import org.reactome.web.diagram.handlers.RenderOtherContextDialogInfoHandler;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.helper.ItemsDistribution;
import org.reactome.web.diagram.renderers.helper.RenderType;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;
import org.reactome.web.fi.client.visualisers.OverlayRenderer;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

public class ContinuousDataOverlayRenderer implements OverlayRenderer, RenderOtherContextDialogInfoHandler, 
	OverlayDataResetHandler{

	private EventBus eventBus;
	private AdvancedContext2d ctx;
	private RendererManager rendererManager;
	private Double factor;
	private Coordinate offset;
	private ThreeColorGradient gradient;
	private OverlayContext originalOverlay;
	private DataOverlay dataOverlay;
	
	public ContinuousDataOverlayRenderer(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.addHandler(RenderOtherContextDialogInfoEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}
	
	@Override
	public void doRender(Collection<DiagramObject> items, AdvancedContext2d ctx, Context context,
			RendererManager rendererManager, DataOverlay dataOverlay, OverlayContext overlay) {
		
		//this render is for continuous data
		if(dataOverlay.isDiscrete() || dataOverlay.getDataOverlayEntities() == null)
			return;
		
		this.ctx = ctx;
		this.rendererManager = rendererManager;
		this.factor = context.getDiagramStatus().getFactor();
        this.offset = context.getDiagramStatus().getOffset();
        this.gradient = AnalysisColours.get().expressionGradient;
        this.originalOverlay = overlay;
        this.dataOverlay = dataOverlay;
        if(dataOverlay.getTissueTypes() != null && dataOverlay.getTissueTypes().size()>1) {
        	Map<String, Double> identifierValueMap = new HashMap<>();
        	for(DataOverlayEntity entity : dataOverlay.getDataOverlayEntities()) {
        		if(entity.getTissue() == dataOverlay.getTissueTypes().get(dataOverlay.getColumn()))
        			identifierValueMap.put(entity.getIdentifier(), entity.getValue());
        	}
            this.dataOverlay.setIdentifierValueMap(identifierValueMap);
        }
        ItemsDistribution itemsDistribution = new ItemsDistribution(items, AnalysisType.NONE);
        renderContinuousProteinData(itemsDistribution.getItems("Protein"));
        renderContinuousComplexData(itemsDistribution.getItems("Complex"));
	}

	private void renderContinuousProteinData(MapSet<RenderType, DiagramObject> target) {
	//return if there are no proteins in the visible DiagramObject set
			if(target == null)
				return;
			
			Renderer renderer = rendererManager.getRenderer("Protein");
	        Set<DiagramObject> objectSet = target.values();
	        for(DiagramObject item : objectSet) {
	        	GraphPhysicalEntity graphObject = (GraphPhysicalEntity) item.getGraphObject();
	        	if(graphObject instanceof GraphEntityWithAccessionedSequence || graphObject instanceof GraphProteinDrug) {
	        		int index = graphObject.getIdentifier().length();
	        		if(graphObject.getIdentifier().contains("-"))
	        			index = graphObject.getIdentifier().indexOf("-");

	        		Double identifierDouble = dataOverlay.getIdentifierValueMap().get(graphObject.getIdentifier().substring(0, index));
	        		if(identifierDouble == null) continue;
	        		String colour = gradient.getColor(identifierDouble, dataOverlay.getMinValue(), dataOverlay.getMaxValue());
		        	ctx.setFillStyle(colour);
		        	renderer.draw(ctx, item, factor, offset);
	        	}
	        }
	}

	private void renderContinuousComplexData(MapSet<RenderType, DiagramObject> target) {
		if(target == null)
			return;
		
		Renderer renderer = rendererManager.getRenderer("Complex");
		OverlayContext overlay = this.originalOverlay;
		Set<DiagramObject> objectSet = target.values();
		for(DiagramObject item : objectSet) {
			GraphPhysicalEntity entity = (GraphPhysicalEntity) item.getGraphObject();
			if(entity != null) {
				Set<GraphPhysicalEntity> obj = entity.getParticipants();
				for(GraphPhysicalEntity participant : obj) {
					if(participant instanceof GraphEntityWithAccessionedSequence || participant instanceof GraphProteinDrug) {
						participant.setIsHit(participant.getIdentifier(), 
								getDataOverlayValue(participant.getIdentifier()));
					}
				}
				if(entity.getParticipantsExpression(0).size() > 0)
					renderer.drawExpression(ctx, overlay, item, 0, dataOverlay.getMinValue(), dataOverlay.getMaxValue(),factor, offset);
			}
		}
	}
	
	private List<Double> getDataOverlayValue(String identifier){
		List<Double> result = new ArrayList<>();
		int index = identifier.length();
		if(identifier.contains("-"))
			index = identifier.indexOf("0");
		result.add(dataOverlay.getIdentifierValueMap().get(identifier.substring(0, index)));
		return result;
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.gradient = null;
		this.ctx = null;
		this.factor = null;
		this.offset = null;
		this.rendererManager = null;
		this.originalOverlay = null;
		this.dataOverlay = null;
	}

	@Override
	public void onRenderOtherContextDialogInfo(RenderOtherContextDialogInfoEvent event) {
		if(dataOverlay == null || dataOverlay.isDiscrete() || dataOverlay.getIdentifierValueMap() == null)
			return;
		
		List<GraphPhysicalEntity> data = event.getTable().getDataProvider().getList();
		for(int i=0; i<data.size(); i++) {
			GraphPhysicalEntity entity = data.get(i);
			int index = entity.getIdentifier().length();
			if(entity.getIdentifier().contains("-"))
				index = entity.getIdentifier().indexOf("-");
			Double overlayVal = getDataOverlayValue(entity.getIdentifier().substring(0, index)).get(0);
			if (overlayVal == null) continue;
			String color = gradient.getColor(overlayVal, dataOverlay.getMinValue(), dataOverlay.getMaxValue());
			event.getTable().getRowElement(i).getCells().getItem(0).getStyle().setBackgroundColor(
					color);
//			event.getTable().addExpressionColumns(expression, min, max, sel);
			//TODO: add overlay value next to the identifier
		}
	}

}
