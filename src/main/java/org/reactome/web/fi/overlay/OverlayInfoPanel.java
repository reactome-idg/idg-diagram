package org.reactome.web.fi.overlay;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.*;


public class OverlayInfoPanel extends Composite implements ClickHandler{
	
	EventBus eventBus;
	private Button overlayTypes;
	private Button colours;
	private List<Button> btns = new LinkedList<>();
	
	private DeckLayoutPanel container;
	
	public OverlayInfoPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(org.reactome.web.diagram.context
								  .ContextInfoPanel.RESOURCES.getCSS()
								  .buttonsPanel());
		buttonsPanel.add(this.overlayTypes = new Button("Overlays", this));
		buttonsPanel.add(this.colours = new Button("Colours", this));
		btns.add(this.overlayTypes);
		btns.add(this.colours);
		
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

	@Override
	public void onClick(ClickEvent event) {
		for(Button btn: btns) {
			btn.removeStyleName(org.reactome.web.diagram.context
									   .ContextInfoPanel.RESOURCES.getCSS()
									   .buttonSelected());
		}
		Button btn = (Button) event.getSource();
		btn.addStyleName(org.reactome.web.diagram.context
						 .ContextInfoPanel.RESOURCES.getCSS()
						 .buttonSelected());
		if(btn.equals(this.overlayTypes))
			this.container.showWidget(0);
		else if(btn.equals(this.colours))
			this.container.showWidget(1);
	}
}
