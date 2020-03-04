package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Collection;
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
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.helper.ItemsDistribution;
import org.reactome.web.diagram.renderers.helper.RenderType;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.fi.client.visualisers.OverlayRenderer;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;

import com.google.gwt.event.shared.EventBus;

public class ContinuousDataOverlayRenderer implements OverlayRenderer, RenderOtherContextDialogInfoHandler, 
	OverlayDataResetHandler{

	private EventBus eventBus;
	private AdvancedContext2d overlay;
	private RendererManager rendererManager;
	private Double factor;
	private Coordinate offset;
	private OverlayContext originalOverlayContext;
	private DataOverlay dataOverlay;
	private IDGDecoratorRenderer decoratorRenderer;
	
	public ContinuousDataOverlayRenderer(EventBus eventBus) {
		this.eventBus = eventBus;
		this.decoratorRenderer = new IDGDecoratorRenderer();
		eventBus.addHandler(RenderOtherContextDialogInfoEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}
	
	@Override
	public void doRender(Collection<DiagramObject> items, AdvancedContext2d overlay, Context context,
			RendererManager rendererManager, DataOverlay dataOverlay, OverlayContext overlayContext) {
		
		//this render is for continuous data only
		if(dataOverlay.isDiscrete() || dataOverlay.getUniprotToEntitiesMap() == null)
			return;
		
		this.overlay = overlay;
		this.rendererManager = rendererManager;
		this.factor = context.getDiagramStatus().getFactor();
        this.offset = context.getDiagramStatus().getOffset();
        this.originalOverlayContext = overlayContext;
        this.dataOverlay = dataOverlay;
        this.dataOverlay.updateIdentifierValueMap(); //reset map to just entities in a specific tissue if tissueTypes isnt null
        ItemsDistribution itemsDistribution = new ItemsDistribution(items, AnalysisType.NONE);
        renderContinuousProteinData(itemsDistribution.getItems("Protein"));
        renderContinuousComplexData(itemsDistribution.getItems("Complex"), "Complex");
        renderContinuousComplexData(itemsDistribution.getItems("EntitySet"), "EntitySet");
	}

	/**
	 * Re-renders proteins on diagram based on expression values from TCRD server
	 * @param target
	 */
	private void renderContinuousProteinData(MapSet<RenderType, DiagramObject> target) {
		//return if there are no proteins in the visible DiagramObject set
		if(target == null)
			return;
		
		Renderer renderer = rendererManager.getRenderer("Protein");
        Set<DiagramObject> objectSet = target.values();
        for(DiagramObject item : objectSet) {
        	GraphPhysicalEntity graphObject = (GraphPhysicalEntity) item.getGraphObject();
        	if(graphObject instanceof GraphEntityWithAccessionedSequence || graphObject instanceof GraphProteinDrug) {
        		String identifier = graphObject.getIdentifier();
        		if(identifier.contains("-"))
        			identifier = identifier.substring(0, identifier.indexOf("-"));
        		else if(identifier.contains("ENSG")) {
        			for(Map.Entry<String,String> entry: PairwiseInfoService.getUniprotToGeneMap().entrySet()) {	//Iterate over map. Check value vs. display name
						if(graphObject.getDisplayName().contains(entry.getValue())) {									//If equal, replace with key (uniprot)
							identifier = entry.getKey();
							break;
						}
					}
        		}
        		Double renderValue = dataOverlay.getIdentifierValueMap().get(identifier);
        		if(renderValue == null) continue;
	        	renderer.drawExpression(overlay, this.originalOverlayContext, item, dataOverlay.getColumn(), dataOverlay.getMinValue(), dataOverlay.getMaxValue(), factor, offset);
        	}
        }
	}

	/**
	 * Re-renders complexes on diagram based on expression values from TCRD server
	 * Also used to render EntitySet and can be used for like entities.
	 * @param target
	 */
	private void renderContinuousComplexData(MapSet<RenderType, DiagramObject> target, String renderableClass) {
		//return if there are no Complexes in the visible DiagramObject set
		if(target == null)
			return;
		
		Renderer renderer = rendererManager.getRenderer(renderableClass);
		OverlayContext overlayContext = this.originalOverlayContext;
		Set<DiagramObject> objectSet = target.values();
		for(DiagramObject item : objectSet) {
			GraphPhysicalEntity entity = (GraphPhysicalEntity) item.getGraphObject();
			if(entity != null && entity.getParticipantsExpression(dataOverlay.getColumn()).size() > 0) {
				renderer.drawExpression(overlay, overlayContext, item, dataOverlay.getColumn(), dataOverlay.getMinValue(), dataOverlay.getMaxValue(),factor, offset);
			}
		}
		//render decorators for pairwisePopups if exists
		if(PairwiseOverlayFactory.get().getPairwiseNumberEntities() != null) {
			for(DiagramObject item : objectSet)
				decoratorRenderer.doRender(overlayContext.getOverlay(), item, factor, offset);
		}
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.overlay = null;
		this.factor = null;
		this.offset = null;
		this.rendererManager = null;
		this.originalOverlayContext = null;
		this.dataOverlay = null;
	}

	/**
	 * Used to re-color and add expression columns to diagram context popups
	 */
	@Override
	public void onRenderOtherContextDialogInfo(RenderOtherContextDialogInfoEvent event) {
		if(dataOverlay == null || dataOverlay.isDiscrete() || dataOverlay.getIdentifierValueMap() == null)
			return;
		
		event.getTable().addExpressionColumns(dataOverlay.getTissueTypes(), dataOverlay.getMinValue(), dataOverlay.getMaxValue(), dataOverlay.getColumn());
	}
}
