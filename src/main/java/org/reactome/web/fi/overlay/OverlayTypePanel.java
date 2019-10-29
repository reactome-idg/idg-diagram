package org.reactome.web.fi.overlay;

import java.util.List;

import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

public class OverlayTypePanel extends Composite implements ClickHandler{
	
	private EventBus eventBus;
	private FlowPanel main;
	private FlowPanel buttonPanel;
	
	public OverlayTypePanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		main = new FlowPanel();
		main.add(getOverlayWidget("Choose Overlays:", OverlayType.getTypes()));
				
		initWidget(main);
		
	}

	private Widget getOverlayWidget(String title, List<String> types) {
		buttonPanel = new FlowPanel();
		Label lbl = new Label(title);
		lbl.getElement().getStyle().setColor("#FFFFFF");
		buttonPanel.add(lbl);
		
		for(String type: types) {
			RadioButton button  = new RadioButton(type, type);
			button.addClickHandler(this);
			buttonPanel.add(button);
		}		
		return buttonPanel;
	}

	@Override
	public void onClick(ClickEvent event) {
		RadioButton btn = (RadioButton) event.getSource();
		eventBus.fireEventFromSource(
				new MakeOverlayRequestEvent(
						OverlayType.getType(btn.getName())),
						this);
	}
}
