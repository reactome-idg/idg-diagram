package org.reactome.web.fi.legends;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ContinuousControlPanel extends FlowPanel{

	private EventBus eventBus;
	
	public ContinuousControlPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		this.add(new Label("Hello World"));
	}
	
}
