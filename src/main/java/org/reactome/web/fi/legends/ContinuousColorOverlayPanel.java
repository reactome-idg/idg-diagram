package org.reactome.web.fi.legends;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.GraphEntitySet;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.GraphObjectHoveredHandler;
import org.reactome.web.diagram.handlers.GraphObjectSelectedHandler;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.diagram.DiagramColours;
import org.reactome.web.diagram.util.Console;
import org.reactome.web.diagram.util.ExpressionUtil;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;
import org.reactome.web.fi.events.DataOverlayColumnChangedEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.handlers.DataOverlayColumnChangedHandler;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.DataOverlayEntity;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;

public class ContinuousColorOverlayPanel extends AbsolutePanel implements 
GraphObjectSelectedHandler, GraphObjectHoveredHandler, OverlayDataLoadedHandler,
DataOverlayColumnChangedHandler{
	
	private EventBus eventBus;
	
	private Canvas gradient;
	private Canvas flag;
	
	private InlineLabel topLabel;
	private InlineLabel bottomLabel;
	
	private GraphObject hovered;
	private GraphObject selected;

	private double min;
	private double max;
	
	private DataOverlay dataOverlay;
	
	public ContinuousColorOverlayPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		this.gradient = createCanvas(30,200);
		this.flag = createCanvas(50,210);
		
		this.getElement().getStyle().setHeight(280, Unit.PX);
//		fillGradient();
		
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
		
		initHandlers();
		
		this.setVisible(true);
	}

	private void initHandlers() {
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(GraphObjectSelectedEvent.TYPE, this);
		eventBus.addHandler(GraphObjectHoveredEvent.TYPE, this);
		eventBus.addHandler(DataOverlayColumnChangedEvent.TYPE, this);
	}

	private void fillGradient() {
		Context2d ctx = this.gradient.getContext2d();
		CanvasGradient grd = ctx.createLinearGradient(0, 0, 30, 200);
		
		ThreeColorGradient gradient = AnalysisColours.get().expressionGradient;
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

	@Override
	public void onGraphObjectHovered(GraphObjectHoveredEvent event) {
		if(dataOverlay == null) return;
		List<DiagramObject> hoveredObjects = event.getHoveredObjects();
        DiagramObject item = hoveredObjects != null && !hoveredObjects.isEmpty() ? hoveredObjects.get(0) : null;
        this.hovered = item != null ? item.getGraphObject() : null;
        draw();
	}

	@Override
	public void onGraphObjectSelected(GraphObjectSelectedEvent event) {
		if(dataOverlay == null) return;
		this.selected = event.getGraphObject();
		draw();
		
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.dataOverlay = event.getDataOverlay();
		this.min = event.getDataOverlay().getMinValue();
		this.max = event.getDataOverlay().getMaxValue();
		this.topLabel.setText(min+"");
		this.bottomLabel.setText(max+"");
		fillGradient();
		updateIdentifierValueMap();
	}
	
    private void draw() {
    	if(!this.isVisible()) return;
    	
    	try {
    		Context2d ctx = this.flag.getContext2d();
    		ctx.clearRect(0, 0, this.flag.getOffsetWidth(), this.flag.getOffsetHeight());
    		
    		List<Double>hoveredValues = getExpressionValues(this.hovered, 0);
    		if (!hoveredValues.isEmpty()) {
                String colour = DiagramColours.get().PROFILE.getProperties().getHovering();
                for (Double value : hoveredValues) {
                    double p = ThreeColorGradient.getPercentage(value, this.min, this.max);
                    drawLeftPin(ctx, p, colour, colour);
                }
                if (hoveredValues.size() > 1) {
                    Double median = ExpressionUtil.median(hoveredValues);
                    double p = ThreeColorGradient.getPercentage(median, this.min, this.max);
                    colour = AnalysisColours.get().PROFILE.getExpression().getLegend().getMedian();
                    drawLeftPin(ctx, p, colour, colour);
                }
            }
    		List<Double> selectedValues = getExpressionValues(this.selected, 0);
            if (!selectedValues.isEmpty()) {
                String colour = DiagramColours.get().PROFILE.getProperties().getSelection();
                for (Double value : selectedValues) {
                    double p = ThreeColorGradient.getPercentage(value, this.min, this.max);
                    drawRightPin(ctx, p, colour, colour);
                }
                if (selectedValues.size() > 1) {
                    Double median = ExpressionUtil.median(selectedValues);
                    double p = ThreeColorGradient.getPercentage(median, this.min, this.max);
                    colour = AnalysisColours.get().PROFILE.getExpression().getLegend().getMedian();
                    drawRightPin(ctx, p, colour, colour);
                }
            }
    		
    	}catch(Exception e) {
    		Console.error(e.getMessage(), this);
    	}
    }

    private List<Double> getExpressionValues(GraphObject graphObject, int column) {
    	List<Double> expression = new LinkedList<>();
    		
    	if (graphObject != null) {
            if (graphObject instanceof GraphComplex) {
                GraphComplex complex = (GraphComplex) graphObject;
                expression = new LinkedList<>(complex.getParticipantsExpression(column).values());
            } else if (graphObject instanceof GraphEntitySet) {
                GraphEntitySet set = (GraphEntitySet) graphObject;
                expression = new LinkedList<>(set.getParticipantsExpression(column).values());
            } else {
                GraphPhysicalEntity pe = (GraphPhysicalEntity)graphObject;
                Double exp = dataOverlay.getIdentifierValueMap().get(pe.getIdentifier());
                if(exp != null && exp != new Double(0))
                	expression.add(dataOverlay.getIdentifierValueMap().get(pe.getIdentifier()));
            }
        }
        return expression;
    	}

	private void drawLeftPin(Context2d ctx, double p, String stroke, String fill) {
        int y = (int) Math.round(200 * p) + 5;
        ctx.setFillStyle(fill);
        ctx.setStrokeStyle(stroke);
        ctx.beginPath();
        ctx.moveTo(5, y - 5);
        ctx.lineTo(10, y);
        ctx.lineTo(5, y + 5);
        ctx.lineTo(5, y - 5);
        ctx.fill();
        ctx.stroke();
        ctx.closePath();

        ctx.beginPath();
        ctx.moveTo(10, y);
        ctx.lineTo(40, y);
        ctx.stroke();
        ctx.closePath();
    }

    private void drawRightPin(Context2d ctx, double p, String stroke, String fill) {
        int y = (int) Math.round(200 * p) + 5;
        ctx.setFillStyle(fill);
        ctx.setStrokeStyle(stroke);
        ctx.beginPath();
        ctx.moveTo(45, y - 5);
        ctx.lineTo(40, y);
        ctx.lineTo(45, y + 5);
        ctx.lineTo(45, y - 5);
        ctx.fill();
        ctx.stroke();
        ctx.closePath();

        ctx.beginPath();
        ctx.moveTo(10, y);
        ctx.lineTo(40, y);
        ctx.stroke();
        ctx.closePath();
    }

	private void updateIdentifierValueMap() {
		if(dataOverlay.getTissueTypes() != null && dataOverlay.getTissueTypes().size()>1) {
        	Map<String, Double> identifierValueMap = new HashMap<>();
        	for(DataOverlayEntity entity : dataOverlay.getDataOverlayEntities()) {
        		if(entity.getTissue() == dataOverlay.getTissueTypes().get(dataOverlay.getColumn()))
        			identifierValueMap.put(entity.getIdentifier(), entity.getValue());
        	}
            this.dataOverlay.setIdentifierValueMap(identifierValueMap);
        }
	}

	@Override
	public void onDataOverlayColumnChanged(DataOverlayColumnChangedEvent event) {
		dataOverlay.setColumn(event.getColumn());
		updateIdentifierValueMap();
	}
	
}
