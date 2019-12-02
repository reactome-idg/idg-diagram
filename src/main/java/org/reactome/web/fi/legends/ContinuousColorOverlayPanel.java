package org.reactome.web.fi.legends;

import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;

public class ContinuousColorOverlayPanel extends AbsolutePanel {
	
	private EventBus eventBus;
	
	private Canvas gradient;
	private Canvas flag;
	
	private InlineLabel topLabel;
	private InlineLabel bottomLabel;

	public ContinuousColorOverlayPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		this.gradient = createCanvas(30,200);
		this.flag = createCanvas(50,210);
		
		this.getElement().getStyle().setHeight(280, Unit.PX);
		fillGradient();
		
		this.topLabel = new InlineLabel("");
		this.topLabel.setSize("40px", "15px");
		this.topLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(this.topLabel, 5, 5);
		
		this.add(this.gradient, 10, 25);
		this.add(this.flag, 0, 20);
		
		this.bottomLabel = new InlineLabel("");
		this.bottomLabel.setSize("40px", "15px");
		this.bottomLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		this.add(this.bottomLabel, 5, 230);
		
		this.setVisible(true);
	}

	private void fillGradient() {
		Context2d ctx = this.gradient.getContext2d();
		CanvasGradient grd = ctx.createLinearGradient(0, 0, 30, 200);
		
		ThreeColorGradient gradient = new ThreeColorGradient(AnalysisColours.get().PROFILE.getExpression().getGradient());
		grd.addColorStop(0, gradient.getColor(0));
        grd.addColorStop(0.5, gradient.getColor(0.5));
        grd.addColorStop(1, gradient.getColor(1));
        
        ctx.clearRect(0, 0, this.gradient.getCoordinateSpaceWidth(), this.gradient.getCoordinateSpaceHeight());
        ctx.setFillStyle(grd);
        ctx.beginPath();
        ctx.fillRect(0, 0, 30, 200);
        ctx.closePath();
	}

	private Canvas createCanvas(int width, int height) {
		Canvas result = Canvas.createIfSupported();
		result.setCoordinateSpaceWidth(width);
		result.setCoordinateSpaceHeight(height);
		result.setPixelSize(width, height);
		return result;
	}
	
}
