package org.reactome.web.fi.legends;

import java.util.Map;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.client.visualisers.diagram.profiles.OverlayColours;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

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
		
		this.closeBtn = new PwpButton("Close", css.close(), this);
		this.add(this.closeBtn);
		
		this.colourMapPanel = new FlowPanel();
		this.add(colourMapPanel);
		this.getElement().getStyle().setMarginBottom(10, Unit.PX);
		
		initHandlers();
		
		addStyleName(RESOURCES.getCSS().enrichmentControl());
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
		Map<String, String> map = OverlayColours.get().getColours(event.getEntities().getDataType());
		map.forEach((k, v) -> {				
			InlineLabel lbl = new InlineLabel(k);
			lbl.getElement().getStyle().setBackgroundColor(v);
			lbl.getElement().getStyle().setPadding(3, Unit.PX);
			lbl.getElement().getStyle().setMargin(0, Unit.PX);
			colourMapPanel.add(lbl);
		});
		
		this.setVisible(true);

	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		colourMapPanel = new FlowPanel();
		this.setVisible(false);
	}
}
