package org.reactome.web.fi.client.visualisers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.data.overlay.OverlayEntity;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayDataHandler {

	private static OverlayDataHandler handler;
	
	private List<OverlayRenderer> renderers;
	
	private OverlayDataHandler() {/*Nothing Here */}
	
	public static OverlayDataHandler getHandler() {
		if(handler == null)
			handler = new OverlayDataHandler();
		return handler;
	}
	
	public void registerHelper(OverlayRenderer renderer) {
		if(renderers == null)
			renderers = new ArrayList<>();
		renderers.add(renderer);
	}
	
	public void overlayData(Collection<DiagramObject> items, 
							AdvancedContext2d ctx, 
							Context context, 
							RendererManager rendererManager, 
							OverlayEntities entities,
							OverlayContext overlay) {
		
		if(renderers == null)
			return;
		renderers.forEach(renderer -> renderer.doRender(items, ctx, context, rendererManager, entities, overlay));
	}
	
	public int overlayRenderersCount() {
		return renderers.size();
	}
}
