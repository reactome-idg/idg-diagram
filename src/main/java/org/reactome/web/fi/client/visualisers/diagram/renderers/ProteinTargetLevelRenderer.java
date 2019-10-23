package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.helper.ItemsDistribution;
import org.reactome.web.diagram.renderers.helper.RenderType;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.fi.client.visualisers.OverlayRenderer;
import org.reactome.web.fi.client.visualisers.diagram.profiles.OverlayColours;
import org.reactome.web.fi.data.overlay.RawOverlayEntities;
import org.reactome.web.fi.data.overlay.RawOverlayEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class ProteinTargetLevelRenderer implements OverlayRenderer {

	private AdvancedContext2d ctx;
	private RendererManager rendererManager;
	private Map<String, String> entitiesMap;
	private Double factor;
	private Coordinate offset;
	private Map<String, String> colourMap;
	
	@Override
	public void doRender(Collection<DiagramObject> items, 
						 AdvancedContext2d ctx, 
						 Context context,
						 RendererManager rendererManager, 
						 RawOverlayEntities rawEntities) {
		
		this.ctx = ctx;
		this.rendererManager = rendererManager;
		factor = context.getDiagramStatus().getFactor();
        offset = context.getDiagramStatus().getOffset();
        colourMap = OverlayColours.get().getColours("targetlevel");

        makeEntitiesMap(rawEntities);
		
		//check analysis status for items distribution
        AnalysisType analysisType = AnalysisType.NONE;
        if(context.getAnalysisStatus() != null)
        	analysisType = AnalysisType.getType(context.getAnalysisStatus().getAnalysisSummary().getType());

        //get proteins with render type of normal
        ItemsDistribution itemsDistribution = new ItemsDistribution(items, analysisType);
        renderProteins(itemsDistribution.getItems("Protein"));
        renderComplexes(itemsDistribution.getItems("Complex"));
	}

	private void renderProteins(MapSet<RenderType, DiagramObject> target) {
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
	private void renderComplexes(MapSet<RenderType, DiagramObject> items) {
		// TODO Auto-generated method stub
		
	}
	
	private void makeEntitiesMap(RawOverlayEntities rawEntities) {
		if(entitiesMap ==null)
			entitiesMap = new HashMap<>();
		for(RawOverlayEntity entity : rawEntities.getEntities()) {
			entitiesMap.put(entity.getIdentifier(), entity.getDataValue());
		}
	}

}
