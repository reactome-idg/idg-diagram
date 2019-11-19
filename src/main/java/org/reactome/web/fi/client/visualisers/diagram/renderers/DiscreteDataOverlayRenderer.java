package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.data.overlay.OverlayEntity;
import org.reactome.web.fi.data.overlay.TargetLevelEntity;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.overlay.profiles.IDGExpressionGradient;
import org.reactome.web.fi.overlay.profiles.OverlayColours;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class DiscreteDataOverlayRenderer implements OverlayRenderer, RenderOtherContextDialogInfoHandler, OverlayDataResetHandler {

	private EventBus eventBus;
	private AdvancedContext2d ctx;
	private RendererManager rendererManager;
	private Double factor;
	private Coordinate offset;
	private Map<Double, String> colourMap;
	private OverlayContext originalOverlay;
	private DataOverlay dataOverlay;
	
	public DiscreteDataOverlayRenderer(EventBus eventBus){
		this.eventBus = eventBus;
		eventBus.addHandler(RenderOtherContextDialogInfoEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}
	
	@Override
	public void doRender(Collection<DiagramObject> items, 
						 AdvancedContext2d ctx, 
						 Context context,
						 RendererManager rendererManager, 
						 DataOverlay dataOverlay,
						 OverlayContext overlay) {
		
		if(!dataOverlay.isDiscrete())
			return;
		
		this.ctx = ctx;
		this.rendererManager = rendererManager;
		this.factor = context.getDiagramStatus().getFactor();
        this.offset = context.getDiagramStatus().getOffset();
        this.colourMap = OverlayColours.get().getColours();
        this.originalOverlay = overlay;
        this.dataOverlay = dataOverlay;

        ItemsDistribution itemsDistribution = new ItemsDistribution(items, AnalysisType.NONE);
        renderProteins(itemsDistribution.getItems("Protein"));
        renderComplexes(itemsDistribution.getItems("Complex"));
	}

	private void renderProteins(MapSet<RenderType, DiagramObject> target) {
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
        		String colour = colourMap.get(new Double(dataOverlay.getIdentifierValueMap().get(graphObject.getIdentifier()
        																						.substring(0, index))));
	        	ctx.setFillStyle(colour);
	        	renderer.draw(ctx, item, factor, offset);
        	}
        }
	}
	private void renderComplexes(MapSet<RenderType, DiagramObject> target) {
		//return if there are no complexes in the visible DiagramObject set
		if(target == null) {
			return;
		}
		
		Renderer renderer = rendererManager.getRenderer("Complex");
		OverlayContext overlay = this.originalOverlay;
		//Store AnalysisColours.get().expressionGradient for restore
		ThreeColorGradient originalExpressionGradient = AnalysisColours.get().expressionGradient;
		//set Analysiscolours.get().expressionGradient to my own
		IDGExpressionGradient colourHelper = new IDGExpressionGradient(null,null,null);
		colourHelper.setColourMap(colourMap);
		AnalysisColours.get().expressionGradient = colourHelper;
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
					//renderer.drawExpression for each diagram object here
					renderer.drawExpression(ctx, overlay, item, 0, 0, 0, factor, offset);
			}
		}
		//Last thing: restore AnalysisColours.get().expressionGradient
		AnalysisColours.get().expressionGradient = originalExpressionGradient;
	}
	
	private List<Double> getDataOverlayValue(String identifier){
		List<Double> result = new ArrayList<>();
		int index = identifier.length();
		if(identifier.contains("-"))
			index = identifier.indexOf("-");
		result.add(dataOverlay.getIdentifierValueMap().get(identifier.substring(0,index)));
		return result;
	}

	@Override
	public void onRenderOtherContextDialogInfo(RenderOtherContextDialogInfoEvent event) {
		if(dataOverlay.getIdentifierValueMap()==null)
			return;
		
		List<GraphPhysicalEntity> data = event.getTable().getDataProvider().getList();
		for(int i=0; i<data.size(); i++) {
			GraphPhysicalEntity entity = data.get(i);
			int index = entity.getIdentifier().length();
			if(entity.getIdentifier().contains("-"))
				index = entity.getIdentifier().indexOf("-");
			event.getTable().getRowElement(i).getCells().getItem(0).getStyle().setBackgroundColor(
					colourMap.get(new Double(dataOverlay.getIdentifierValueMap().get(entity.getIdentifier().substring(0, index)))));
		}
	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.colourMap = null;
		this.ctx = null;
		this.factor = null;
		this.offset = null;
		this.rendererManager = null;
		this.originalOverlay = null;
	}

}
