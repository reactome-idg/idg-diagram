package org.reactome.web.fi.overlay;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.shared.EventBus;

public class ColourChoicePanel extends Composite {
	
	private EventBus eventbus;
	
	public ColourChoicePanel(EventBus eventBus) {
		this.eventbus = eventBus;
		
		FlowPanel main = new FlowPanel();
		Label lbl = new Label("Change Overlay Colours:");
		lbl.getElement().getStyle().setColor("#FFFFFF");
		
		main.add(lbl);
		
		initWidget(main);
	}
	
}
