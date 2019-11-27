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
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.event.shared.EventBus;

public class ContinuousDataOverlayRenderer implements OverlayRenderer{

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
	        		String colour = "";
	    			colour = gradient.getColor(identifierDouble, dataOverlay.getMinValue(), dataOverlay.getMaxValue());
		        	ctx.setFillStyle(colour);
		        	renderer.draw(ctx, item, factor, offset);
	        	}
	        }
	}

	private void renderContinuousComplexData(MapSet<RenderType, DiagramObject> items) {
		// TODO Auto-generated method stub
		
	}

}
