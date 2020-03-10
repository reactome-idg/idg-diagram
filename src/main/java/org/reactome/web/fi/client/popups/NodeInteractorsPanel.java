package org.reactome.web.fi.client.popups;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class NodeInteractorsPanel extends Composite{

	public NodeInteractorsPanel(EventBus eventBus, String id, String name) {
		FlowPanel outerPanel = new FlowPanel();
		
		initWidget(outerPanel);
	}
	
}
