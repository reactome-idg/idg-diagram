package org.reactome.web.fi.client.popups;

import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class NodeOverlayPanel extends Composite{

	public NodeOverlayPanel(EventBus eventBus, String id, String name, DataOverlay overlay) {
		FlowPanel outerPanel = new FlowPanel();
		
		initWidget(outerPanel);
	}
	
}
