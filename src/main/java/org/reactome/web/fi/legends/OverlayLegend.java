package org.reactome.web.fi.legends;

import java.util.Map;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.overlay.profiles.OverlayColours;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayLegend extends LegendPanel implements ClickHandler,
OverlayDataLoadedHandler, OverlayDataResetHandler{
	
	private PwpButton closeBtn;
	private FlowPanel colourMapPanel;

	public OverlayLegend(EventBus eventBus) {
		super(eventBus);
		
		LegendPanelCSS css = RESOURCES.getCSS();
		
		this.colourMapPanel = new FlowPanel();
		this.add(colourMapPanel);
		this.getElement().getStyle().setMarginBottom(10, Unit.PX);
		
		this.closeBtn = new PwpButton("Close", css.close(), this);
		this.add(this.closeBtn);
		
		initHandlers();
		
		addStyleName(RESOURCES.getCSS().enrichmentLegend());
		this.getElement().getStyle().setWidth(100, Unit.PX);
		this.setVisible(false);
		
	}

	private void initHandlers() {
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource().equals(this.closeBtn)) {
			eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
		}
		
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.setVisible(false);
		
		if(colourMapPanel.getWidgetCount()>0)
			for(int i=0; i<colourMapPanel.getWidgetCount(); i++)
				colourMapPanel.remove(i);
		
		//stops loading of new colours during continuous value overlay rendering
		if(!event.getDataOverlay().isDiscrete())
			return;
		
		colourMapPanel.setStyleName(IDGRESOURCES.getCSS().colourMapPanel());
		Map<Double, String> colourMap = OverlayColours.get().getColours();
		event.getDataOverlay().getLegendTypes().forEach((i) ->{
			Label lbl = new Label(i);
			String colour = colourMap.get(new Double(event.getDataOverlay().getLegendTypes().indexOf(i)));
			lbl.getElement().getStyle().setBackgroundColor(colour);
			lbl.getElement().getStyle().setPadding(3, Unit.PX);
			lbl.getElement().getStyle().setMargin(0, Unit.PX);
			colourMapPanel.add(lbl);
		});
		
		this.setVisible(true);

	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.remove(colourMapPanel);
		colourMapPanel = new FlowPanel();
		this.add(colourMapPanel);
		this.setVisible(false);
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
		String CSS = "org/reactome/web/fi/legends/OverlayLegend.css";
		
		String colourMapPanel();
	}
	
}
