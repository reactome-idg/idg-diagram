package org.reactome.web.fi.legends;

import java.util.Map;

import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.overlay.profiles.OverlayColours;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayColourLegend extends LegendPanel implements
OverlayDataLoadedHandler, OverlayDataResetHandler{
	
	private FlowPanel innerPanel;
	private DataOverlay dataOverlay;
	private ContinuousColorOverlayPanel continuousPanel;


	public OverlayColourLegend(EventBus eventBus) {
		super(eventBus);
				
		initPanel(eventBus);
		
		initHandlers();
	}

	/**
	 * Initialize gui
	 * @param eventBus
	 */
	private void initPanel(EventBus eventBus) {
		this.innerPanel = new FlowPanel();
		this.innerPanel.setStyleName(IDGRESOURCES.getCSS().colourMapPanel());
		this.add(innerPanel);
		
		continuousPanel = new ContinuousColorOverlayPanel(eventBus);
		continuousPanel.getElement().getStyle().setMarginLeft(20, Unit.PX);
		
		addStyleName(IDGRESOURCES.getCSS().outerPanel());
		this.getElement().getStyle().setWidth(100, Unit.PX);
		this.getElement().getStyle().setHeight(270, Unit.PX);
		this.setVisible(false);
	}

	private void initHandlers() {
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.setVisible(false);
		
		if(event.getDataOverlay().getUniprotToEntitiesMap() == null || event.getDataOverlay().getUniprotToEntitiesMap().size() == 0)
			return;
		
		innerPanel.clear();
		this.dataOverlay = event.getDataOverlay();
		
		if(event.getDataOverlay().getUniprotToEntitiesMap() == null) {
			showNoResultsMessage();
			this.setVisible(true);
			return;
		}
		
		if(event.getDataOverlay().isDiscrete())		
			showDiscretePanel();
		else if(!event.getDataOverlay().isDiscrete())
			showContinuousPanel();
		
		this.setVisible(true);

	}

	private void showDiscretePanel() {
		FlowPanel colourMapPanel = new FlowPanel();
		colourMapPanel.setStyleName(IDGRESOURCES.getCSS().colourMapPanel());
		Map<Double, String> colourMap = OverlayColours.get().getColours();
		dataOverlay.getLegendTypes().forEach((i) ->{
			Label lbl = new Label(i);
			String colour = colourMap.get(new Double(dataOverlay.getLegendTypes().indexOf(i)));
			lbl.getElement().getStyle().setBackgroundColor(colour);
			lbl.setStyleName(IDGRESOURCES.getCSS().legendLabel());
			colourMapPanel.add(lbl);
		});
		
		if(colourMapPanel.getWidgetCount() == 0) 
			showNoResultsMessage();
		else
			innerPanel.add(colourMapPanel);
	}
	
	private void showContinuousPanel() {
		innerPanel.add(continuousPanel);
	}

	private void showNoResultsMessage() {
		FlowPanel result = new FlowPanel();
		Label lbl = new Label("No hits in this diagram.");
		lbl.getElement().getStyle().setPadding(3, Unit.PX);
		lbl.getElement().getStyle().setMargin(0, Unit.PX);
		result.add(lbl);
		innerPanel.add(result);
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		innerPanel.clear();
		this.setVisible(false);
	}
	
	public void setUnit(String unit) {
		continuousPanel.setUnit(unit);
	}
	
	public static Resources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(Resources.class);
		IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-colorChoicePanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/legends/OverlayColourLegend.css";
		
		String colourMapPanel();
		
		String outerPanel();
		
		String legendLabel();
	}
}
