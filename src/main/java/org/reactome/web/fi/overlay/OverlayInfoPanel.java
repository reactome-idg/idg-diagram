package org.reactome.web.fi.overlay;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.*;


public class OverlayInfoPanel extends Composite {
	
	EventBus eventBus;
	private Button overlayTypes;
	private Button colours;
	
	private DeckLayoutPanel container;
	
	public OverlayInfoPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(org.reactome.web.diagram.context
								  .ContextInfoPanel.RESOURCES.getCSS()
								  .buttonsPanel());
		buttonsPanel.add(this.overlayTypes = new Button("Overlays"));
		buttonsPanel.add(this.colours = new Button("Colours"));
		
		this.overlayTypes.addStyleName(org.reactome.web.diagram.context
									   .ContextInfoPanel.RESOURCES.getCSS()
									   .buttonSelected());
		
		this.container = new DeckLayoutPanel();
		this.container.setStyleName(org.reactome.web.diagram.context
				   					.ContextInfoPanel.RESOURCES.getCSS()
				   					.container());
		this.container.add(new OverlayColoursPanel(eventBus));
		this.container.showWidget(0);
		this.container.setAnimationVertical(true);
		this.container.setAnimationDuration(500);
		
		FlowPanel outerPanel = new FlowPanel();
		outerPanel.setStyleName(org.reactome.web.diagram.context
				   				.ContextInfoPanel.RESOURCES.getCSS()
				   				.outerPanel());
		outerPanel.add(buttonsPanel);
		outerPanel.add(this.container);
		initWidget(outerPanel);
	}
}
