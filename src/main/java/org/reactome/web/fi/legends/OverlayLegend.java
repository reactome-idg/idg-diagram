package org.reactome.web.fi.legends;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;

public class OverlayLegend extends LegendPanel implements ClickHandler,
OverlayDataLoadedHandler{
	
	InlineLabel message;
	private PwpButton closeBtn;

	public OverlayLegend(EventBus eventBus) {
		super(eventBus);
		
		LegendPanelCSS css = RESOURCES.getCSS();

		this.message = new InlineLabel();
		this.message.setText("Overlay Data Types:");
		this.add(this.message);
		
		this.closeBtn = new PwpButton("Close", css.close(), this);
		this.add(this.closeBtn);
		
		initHandlers();
		
		addStyleName(RESOURCES.getCSS().enrichmentControl());
		this.setHeight("63px");
		this.setVisible(false);
		
	}

	private void initHandlers() {
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource().equals(this.closeBtn)) {
			this.setVisible(false);
			eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
		}
		
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.setVisible(true);
		//get color map here, make grid, and display on legend
	}


}
