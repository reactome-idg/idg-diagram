package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
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
import org.reactome.web.fi.client.visualisers.diagram.profiles.OverlayColours;
import org.reactome.web.fi.client.visualisers.diagram.helpers.IDGExpressionGradientHelper;
import org.reactome.web.fi.data.overlay.RawOverlayEntities;
import org.reactome.web.fi.data.overlay.RawOverlayEntity;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;

import com.google.gwt.event.shared.EventBus;

/**
 * 
 * @author brunsont
 *
 */
public class ProteinTargetLevelRenderer implements OverlayRenderer, RenderOtherContextDialogInfoHandler, OverlayDataResetHandler {

	private EventBus eventBus;
	private AdvancedContext2d ctx;
	private RendererManager rendererManager;
	private Map<String, String> entitiesMap;
	private Double factor;
	private Coordinate offset;
	private Map<String, String> colourMap;
	private Map<Double, String> doubleColourMap;
	private OverlayContext originalOverlay;
	
	public ProteinTargetLevelRenderer(EventBus eventBus){
		this.eventBus = eventBus;
		eventBus.addHandler(RenderOtherContextDialogInfoEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}
	
	@Override
	public void doRender(Collection<DiagramObject> items, 
						 AdvancedContext2d ctx, 
						 Context context,
						 RendererManager rendererManager, 
						 RawOverlayEntities rawEntities,
						 OverlayContext overlay) {
		
		this.ctx = ctx;
		this.rendererManager = rendererManager;
		this.factor = context.getDiagramStatus().getFactor();
        this.offset = context.getDiagramStatus().getOffset();
        this.colourMap = OverlayColours.get().getColours(rawEntities.getDataType());
        this.doubleColourMap = OverlayColours.get().getDoubleColoursMap(rawEntities.getDataType());
        this.originalOverlay = overlay;
        makeEntitiesMap(rawEntities);
		
		//check analysis status for items distribution
        AnalysisType analysisType = AnalysisType.NONE;
        if(context.getAnalysisStatus() != null)
        	analysisType = AnalysisType.getType(context.getAnalysisStatus().getAnalysisSummary().getType());

        ItemsDistribution itemsDistribution = new ItemsDistribution(items, analysisType);
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
        	ctx.setFillStyle(colourMap
        					 .get(entitiesMap
        					 .get(graphObject.getIdentifier())));
        	renderer.draw(ctx, item, factor, offset);
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
		ThreeColorGradient originalEnrichmentGradient = AnalysisColours.get().enrichmentGradient;
		//set Analysiscolours.get().expressionGradient to my own
		IDGExpressionGradientHelper colourHelper = new IDGExpressionGradientHelper(null,null,null);
		colourHelper.setColourMap(doubleColourMap);
		AnalysisColours.get().expressionGradient = colourHelper;
//		AnalysisColours.get().enrichmentGradient = colourHelper;
		Set<DiagramObject> objectSet = target.values();
		for(DiagramObject item : objectSet) {
			GraphPhysicalEntity entity = (GraphPhysicalEntity) item.getGraphObject();
			Set<GraphPhysicalEntity> obj = entity.getParticipants();
			for(GraphPhysicalEntity participant : obj) {
				participant.setIsHit(participant.getIdentifier(),
									 mapColourToDouble(participant.getIdentifier()));
			}
			//renderer.drawExpression for each diagram object here
			renderer.drawExpression(ctx, overlay, item, 0, 0, 0, factor, offset);
		}
		//Last thing: restore AnalysisColours.get().expressionGradient
		AnalysisColours.get().expressionGradient = originalExpressionGradient;
	}

	private List<Double> mapColourToDouble(String identifier) {
		String value = entitiesMap.get(identifier);
		List<Double> rtn = new ArrayList<>();
		switch(value) {
			case "Tclin+":	rtn.add((double) 0); 	break;
			case "Tclin":	rtn.add((double) 1); 	break;
			case "Tchem+":	rtn.add((double) 2); 	break;
			case "Tchem":	rtn.add((double) 3);	break;
			case "Tbio":	rtn.add((double) 4); 	break;
			case "Tgray":	rtn.add((double) 5); 	break;
			case "Tdark":	rtn.add((double) 6); 	break;
			default: 		rtn.add((double) 7);	break;
		}
		return rtn;
	}

	private void makeEntitiesMap(RawOverlayEntities rawEntities) {
		if(entitiesMap ==null)
			entitiesMap = new HashMap<>();
		for(RawOverlayEntity entity : rawEntities.getEntities()) {
			entitiesMap.put(entity.getIdentifier(), entity.getValue());
		}
	}

	@Override
	public void onRenderOtherContextDialogInfo(RenderOtherContextDialogInfoEvent event) {
		if(entitiesMap==null)
			return;
		
		List<GraphPhysicalEntity> data = event.getTable().getDataProvider().getList();
		for(int i=0; i<data.size(); i++) {
			GraphPhysicalEntity entity = data.get(i);
			event.getTable().getRowElement(i).getCells().getItem(0).getStyle().setBackgroundColor(
					colourMap.get(entitiesMap.get(entity.getIdentifier())));
		}
	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.entitiesMap = null;
		this.colourMap = null;
		this.ctx = null;
		this.doubleColourMap = null;
		this.factor = null;
		this.offset = null;
		this.rendererManager = null;
		this.originalOverlay = null;
		
	}

}
