package org.reactome.web.fi.overlay;


import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.model.OverlayEntityType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author brunsont
 *
 */
public class DataOverlayTypePanel extends OverlayTypePanel{
	
	private EventBus eventBus;
	private FlowPanel main;
	private FlowPanel buttonPanel;
	private ScrollPanel scrollPanel;
	
	public DataOverlayTypePanel(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		main = new FlowPanel();
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("140px");
		scrollPanel.add(getOverlayWidget("Choose Data Overlays:"));
		main.add(scrollPanel);
				
		initWidget(main);
		
	}

	@Override
	protected Widget getOverlayWidget(String dataTitle) {
		buttonPanel = new FlowPanel();
		
		//add data overlay types that overlay onto current entities
		Label lbl = new Label(dataTitle);
		lbl.setStyleName(IDGRESOURCES.getCSS().label());
		buttonPanel.add(lbl);
		
		for(OverlayDataType type: OverlayDataType.values()) {
			RadioButton button  = new RadioButton("OverlayDataTypes", type.getName());
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
				OverlayDataType.lookupType(btn.getText())), this);
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
				if(OverlayDataType.lookupType(((RadioButton) widget).getText()) == OverlayDataType.lookupType(dataType)) {
					((RadioButton) widget).setValue(true);
					return;
				}
			}
		}
	}

}
