package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphProteinDrug;
import org.reactome.web.diagram.data.interactors.common.InteractorsSummary;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.Node;
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
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.overlay.profiles.IDGExpressionGradient;
import org.reactome.web.fi.overlay.profiles.OverlayColours;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwisePopupFactory;

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
	private IDGDecoratorRenderer decoratorRenderer;
	
	public DiscreteDataOverlayRenderer(EventBus eventBus){
		this.eventBus = eventBus;
		this.decoratorRenderer = new IDGDecoratorRenderer();
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
		
		//this renderer is for discrete data
		if(!dataOverlay.isDiscrete() || dataOverlay.getUniprotToEntitiesMap() == null)
			return;
		
		this.ctx = ctx;
		this.rendererManager = rendererManager;
		this.factor = context.getDiagramStatus().getFactor();
        this.offset = context.getDiagramStatus().getOffset();

        this.colourMap = OverlayColours.get().getColours();
        this.originalOverlay = overlay;
        
        this.dataOverlay = dataOverlay;
        
        //reset map to just entities in a specific tissue if tissueTypes isn't null
        this.dataOverlay.updateIdentifierValueMap();

        ItemsDistribution itemsDistribution = new ItemsDistribution(items, AnalysisType.NONE);
        renderDiscreteProteinData(itemsDistribution.getItems("Protein"));
        renderDiscreteComplexData(itemsDistribution.getItems("Complex"), "Complex");
        renderDiscreteComplexData(itemsDistribution.getItems("EntitySet"), "EntitySet");
	}

	/**
	 * Re-render proteins given a discrete set of data from the TCRD server
	 * @param target
	 */
	private void renderDiscreteProteinData(MapSet<RenderType, DiagramObject> target) {
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

        		Double identifierDouble = dataOverlay.getIdentifierValueMap().get(identifier);
        		if(identifierDouble == null) continue;
        		String colour = "";
    			colour = colourMap.get(identifierDouble);
	        	ctx.setFillStyle(colour);
	        	renderer.draw(ctx, item, factor, offset);
        	}
        }
	}
	
	/**
	 * Re-render complexes given a discrete set of data from the TCRD server.
	 * Also used to render EntitySet and can be used for like entities.
	 * @param target
	 */
	private void renderDiscreteComplexData(MapSet<RenderType, DiagramObject> target, String renderableClass) {
		//return if there are no complexes in the visible DiagramObject set
		if(target == null) {
			return;
		}
		
		Renderer renderer = rendererManager.getRenderer(renderableClass);
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
			if(entity != null && entity.getParticipantsExpression(dataOverlay.getColumn()).size() > 0) {
				//renderer.drawExpression for each diagram object here
				renderer.drawExpression(ctx, overlay, item, dataOverlay.getColumn(), dataOverlay.getMinValue(), dataOverlay.getMaxValue(), factor, offset);
				//render decorators for pairwisePopups if exists
//				if(PairwisePopupFactory.get().getCurrentPairwiseProperties().size()!=0)
//					decoratorRenderer.doRender(ctx, item, factor, offset);
			}
		}
		//Last thing: restore AnalysisColours.get().expressionGradient
		AnalysisColours.get().expressionGradient = originalExpressionGradient;
	}
	
	/**
	 * Not used but keeping for future logic repurpose
	 * @param item
	 * @return
	 */
	private InteractorsSummary getInteractorSummary(DiagramObject item) {		
		int total = 0;
		
		GraphPhysicalEntity obj = item.getGraphObject();
		Set<GraphPhysicalEntity> objSet = obj.getParticipants();
		for(GraphPhysicalEntity entity : objSet) {
			for(DiagramObject diagramObject : entity.getDiagramObjects()) {
				Node node = (Node)diagramObject;
				if(node.getDiagramEntityInteractorsSummary()!= null) {
					total += node.getDiagramEntityInteractorsSummary().getNumber();
				}
			}
		}
		InteractorsSummary result = new InteractorsSummary(obj.getIdentifier(), item.getId(), total);
		return result;
	}

	@Override
	public void onRenderOtherContextDialogInfo(RenderOtherContextDialogInfoEvent event) {
		if(dataOverlay == null || !dataOverlay.isDiscrete() || dataOverlay.getIdentifierValueMap()==null)
			return;
		
		List<GraphPhysicalEntity> data = event.getTable().getDataProvider().getList();
		for(int i=0; i<data.size(); i++) {
			GraphPhysicalEntity entity = data.get(i);
			int index = entity.getIdentifier().length();
			if(entity.getIdentifier().contains("-"))
				index = entity.getIdentifier().indexOf("-");
			event.getTable().getRowElement(i).getCells().getItem(0).getStyle().setBackgroundColor(
					colourMap.get(dataOverlay.getIdentifierValueMap().get(entity.getIdentifier().substring(0, index))));
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
		this.dataOverlay = null;
	}

}
