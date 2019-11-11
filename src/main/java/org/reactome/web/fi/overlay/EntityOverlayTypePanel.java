package org.reactome.web.fi.overlay;

import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayEntityType;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class EntityOverlayTypePanel extends OverlayTypePanel {

	private EventBus eventBus;
	private FlowPanel main;
	private FlowPanel buttonPanel;
	private ScrollPanel scrollPanel;
	
	EntityOverlayTypePanel(EventBus eventBus){
		super(eventBus);
		this.eventBus = eventBus;
		
		main = new FlowPanel();
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("140px");
		scrollPanel.add(getOverlayWidget("Choose Entity Overlay:"));
		main.add(scrollPanel);
		
		initWidget(main);
	}
	
	@Override
	protected Widget getOverlayWidget(String widgetTitle) {
		buttonPanel = new FlowPanel();
		
		Label lbl = new Label(widgetTitle);
		lbl.setStyleName(IDGRESOURCES.getCSS().label());
		buttonPanel.add(lbl);
	
		for(OverlayEntityType type: OverlayEntityType.values()) {
			RadioButton button = new RadioButton("OverlayEntityTypes", type.getName());
			button.getElement().getStyle().setDisplay(Display.BLOCK);
			button.addClickHandler(this);
			buttonPanel.add(button);
		}
		
		return buttonPanel;
	}

	@Override
	public void onClick(ClickEvent event) {
		RadioButton btn = (RadioButton) event.getSource();
		eventBus.fireEventFromSource(new MakeOverlayRequestEvent(
				OverlayEntityType.lookupType(btn.getText())), this);
	}
	
	
	
	@Override
	protected void reset() {
		for(int i=0; i<buttonPanel.getWidgetCount(); i++) {
			Widget widget = buttonPanel.getWidget(i);
			if(widget instanceof RadioButton)
				((RadioButton) widget).setValue(false);
		}
	}

	@Override
	public void selectType(String dataType) {
		for(int i=0; i<buttonPanel.getWidgetCount(); i++) {
			Widget widget = buttonPanel.getWidget(i);
			if(widget instanceof RadioButton) {
				if(OverlayEntityType.lookupType(((RadioButton)widget).getText()) == OverlayEntityType.lookupType(dataType)) {
					((RadioButton)widget).setValue(true);
					return;
				}
			}
		}
	}
}
