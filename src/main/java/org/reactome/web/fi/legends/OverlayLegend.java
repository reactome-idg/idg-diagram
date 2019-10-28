package org.reactome.web.fi.legends;

import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;

public class OverlayLegend extends LegendPanel{
	
	Canvas gradient;

	public OverlayLegend(EventBus eventBus) {
		super(eventBus);

		this.gradient = createCanvas(30,200);
		this.fillGradient();
		
		addStyleName(RESOURCES.getCSS().enrichmentLegend());
		this.setVisible(true);
		
	}

	private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);
        return canvas;
    }
	
	private void fillGradient(){
        Context2d ctx = this.gradient.getContext2d();
        CanvasGradient grd = ctx.createLinearGradient(0, 0, 30, 200);
        grd.addColorStop(0, AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMin());
        grd.addColorStop(1, AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax());

        ctx.clearRect(0, 0, this.gradient.getCoordinateSpaceWidth(), this.gradient.getCoordinateSpaceHeight());
        ctx.setFillStyle(grd);
        ctx.beginPath();
        ctx.fillRect(0, 0, 30, 200);
        ctx.closePath();
    }

}
