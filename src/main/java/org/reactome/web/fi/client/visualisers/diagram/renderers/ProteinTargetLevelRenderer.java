package org.reactome.web.fi.client.visualisers.diagram.renderers;

import java.util.Collection;
import java.util.Set;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.helper.ItemsDistribution;
import org.reactome.web.diagram.renderers.helper.RenderType;
import org.reactome.web.diagram.renderers.layout.Renderer;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.fi.client.visualisers.OverlayRenderer;
import org.reactome.web.fi.data.overlay.RawOverlayEntities;
import org.reactome.web.fi.data.overlay.RawOverlayEntity;

public class ProteinTargetLevelRenderer implements OverlayRenderer {

	@Override
	public void doRender(Collection<DiagramObject> items, 
						 AdvancedContext2d ctx, 
						 Context context,
						 RendererManager rendererManager, 
						 RawOverlayEntities entities) {
		
		Renderer renderer = rendererManager.getRenderer("Protein");
        Double factor = context.getDiagramStatus().getFactor();
        Coordinate offset = context.getDiagramStatus().getOffset();
        ctx.setFillStyle("#000000");
		
		//check analysis status for items distribution
        AnalysisType analysisType = AnalysisType.NONE;
        if(context.getAnalysisStatus() != null)
        	analysisType = AnalysisType.getType(context.getAnalysisStatus().getAnalysisSummary().getType());

        //get proteins with render type of normal
        ItemsDistribution itemsDistribution = new ItemsDistribution(items, analysisType);
        MapSet<RenderType, DiagramObject> target = itemsDistribution.getItems("Protein");
        Set<DiagramObject> normal = target.getElements(RenderType.NORMAL);
        
        for(DiagramObject item : normal) {
        	renderer.draw(ctx, item, factor, offset);
        }
	}

}
