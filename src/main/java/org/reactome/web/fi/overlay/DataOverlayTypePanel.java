package org.reactome.web.fi.overlay;


import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayDataType;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.DeckPanel;
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
public class DataOverlayTypePanel extends OverlayTypePanel implements
OptionsPanel.Handler{
	
	private EventBus eventBus;
	private FlowPanel main;
	private FlowPanel buttonPanel;
	private DeckPanel container;
	private ScrollPanel scrollPanel;
	private OptionsPanel optionsPanel;
	private String selectedOverlayType = null;
	
	public DataOverlayTypePanel(EventBus eventBus) {
		super(eventBus);
		this.eventBus = eventBus;
		
		main = new FlowPanel();
		main.add(getDeckPanel());
				
		initWidget(main);
		
	}

	private DeckPanel getDeckPanel() {
		container = new DeckPanel();
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("120px");
		scrollPanel.add(getOverlayWidget("Choose Data Overlays:"));
		container.add(scrollPanel);
		optionsPanel = new OptionsPanel(this);
		container.add(optionsPanel);
		container.showWidget(0);
		
		container.getElement().getStyle().setHeight(120, Unit.PX);
		
		return container;
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
			button.setStyleName(IDGRESOURCES.getCSS().radioButton());
			button.addClickHandler(this);
			buttonPanel.add(button);
		}		
				
		return buttonPanel;
	}

	@Override
	public void onClick(ClickEvent event) {
		RadioButton btn = (RadioButton) event.getSource();
		selectedOverlayType = btn.getText();
		container.showWidget(1);
//		eventBus.fireEventFromSource(new MakeOverlayRequestEvent(
//				OverlayDataType.lookupType(btn.getText())), this);
	}
	
	@Override
	protected void reset() {
		for(int i=0; i<buttonPanel.getWidgetCount(); i++) {
			Widget widget = buttonPanel.getWidget(i);
			if(widget instanceof RadioButton)
				((RadioButton) widget).setValue(false);
		}
		container.showWidget(0);
	}

	@Override
	public void selectType(String dataType) {
		for(int i=0; i<buttonPanel.getWidgetCount(); i++) {
			Widget widget = buttonPanel.getWidget(i);
			if(widget instanceof RadioButton) {
				if(OverlayDataType.lookupType(((RadioButton) widget).getText()) == OverlayDataType.lookupType(dataType)) {
					((RadioButton) widget).setValue(true);
					selectedOverlayType = dataType;
					return;
				}
			}
		}
	}
	
	@Override
	public void onOverlaySelected() {
		container.showWidget(1);
		eventBus.fireEventFromSource(new MakeOverlayRequestEvent(
				OverlayDataType.lookupType(selectedOverlayType)), this);
	}

	@Override
	public void onCancelSelected() {
		reset();
		container.showWidget(0);
	}
	
	@Override
	public void onBackSelected() {
		container.showWidget(0);
	}
}
