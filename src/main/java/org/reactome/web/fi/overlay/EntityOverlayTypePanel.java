package org.reactome.web.fi.overlay;

import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayEntityType;

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

public class EntityOverlayTypePanel extends OverlayTypePanel implements
OptionsPanel.Handler{

	private EventBus eventBus;
	private FlowPanel main;
	private FlowPanel buttonPanel;
	private DeckPanel container;
	private ScrollPanel scrollPanel;
	private OptionsPanel optionsPanel;
	private String selectedOverlayType = null;
	
	EntityOverlayTypePanel(EventBus eventBus){
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
		scrollPanel.add(getOverlayWidget("Choose Entity Overlay:"));
		container.add(scrollPanel);
		optionsPanel = new OptionsPanel(this);
		container.add(optionsPanel);
		container.showWidget(0);
		
		container.getElement().getStyle().setHeight(120, Unit.PX);
		return container;
	}
	
	@Override
	protected Widget getOverlayWidget(String widgetTitle) {
		buttonPanel = new FlowPanel();
		
		Label lbl = new Label(widgetTitle);
		lbl.setStyleName(IDGRESOURCES.getCSS().label());
		buttonPanel.add(lbl);
	
		for(OverlayEntityType type: OverlayEntityType.values()) {
			RadioButton button = new RadioButton("OverlayEntityTypes", type.getName());
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
//				OverlayEntityType.lookupType(btn.getText())), this);
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
				OverlayEntityType.lookupType(selectedOverlayType)), this);
		
	}

	@Override
	public void onBackSelected() {
		container.showWidget(0);
	}

	@Override
	public void onCancelSelected() {
		reset();
		container.showWidget(0);
	}
}
