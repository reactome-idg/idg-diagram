	package org.reactome.web.fi.client.visualisers;

import java.util.Collection;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.renderers.layout.RendererManager;
import org.reactome.web.diagram.renderers.common.OverlayContext;
import org.reactome.web.diagram.util.AdvancedContext2d;
import org.reactome.web.fi.model.DataOverlay;

/**
 * 
 * @author brunsont
 *
 */
public interface OverlayRenderer {
	public void doRender(Collection<DiagramObject> items, 
						 AdvancedContext2d overlay, 
						 Context context, 
						 RendererManager rendererManager,
						 OverlayContext overlayContext);
}
