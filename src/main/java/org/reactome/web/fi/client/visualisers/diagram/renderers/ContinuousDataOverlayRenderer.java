package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Collection;
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
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.event.shared.EventBus;

public class ContinuousDataOverlayRenderer implements OverlayRenderer, RenderOtherContextDialogInfoHandler, 
	OverlayDataResetHandler{

	private EventBus eventBus;
	private AdvancedContext2d ctx;
	private RendererManager rendererManager;
	private Double factor;
	private Coordinate offset;
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
		
		//this render is for continuous data only
		if(dataOverlay.isDiscrete() || dataOverlay.getUniprotToEntitiesMap() == null)
			return;
		
		this.ctx = ctx;
		this.rendererManager = rendererManager;
		this.factor = context.getDiagramStatus().getFactor();
        this.offset = context.getDiagramStatus().getOffset();
        this.originalOverlay = overlay;
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
        		int index = graphObject.getIdentifier().length();
        		if(graphObject.getIdentifier().contains("-"))
        			index = graphObject.getIdentifier().indexOf("-");

        		Double renderValue = dataOverlay.getIdentifierValueMap().get(graphObject.getIdentifier().substring(0, index));
        		if(renderValue == null) continue;
	        	renderer.drawExpression(ctx, this.originalOverlay, item, dataOverlay.getColumn(), dataOverlay.getMinValue(), dataOverlay.getMaxValue(), factor, offset);
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
		OverlayContext overlay = this.originalOverlay;
		Set<DiagramObject> objectSet = target.values();
		for(DiagramObject item : objectSet) {
			GraphPhysicalEntity entity = (GraphPhysicalEntity) item.getGraphObject();
			if(entity != null && entity.getParticipantsExpression(dataOverlay.getColumn()).size() > 0) {
					renderer.drawExpression(ctx, overlay, item, dataOverlay.getColumn(), dataOverlay.getMinValue(), dataOverlay.getMaxValue(),factor, offset);
			}
		}
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.ctx = null;
		this.factor = null;
		this.offset = null;
		this.rendererManager = null;
		this.originalOverlay = null;
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
