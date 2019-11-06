package org.reactome.web.fi.overlay;

import java.util.List;

import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayType;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayTypePanel extends Composite implements ClickHandler{
	
	private EventBus eventBus;
	private FlowPanel main;
	private FlowPanel buttonPanel;
	
	public OverlayTypePanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		main = new FlowPanel();
		main.add(getOverlayWidget("Choose Overlays:"));
				
		initWidget(main);
		
	}

	private Widget getOverlayWidget(String title) {
		buttonPanel = new FlowPanel();
		Label lbl = new Label(title);
		lbl.getElement().getStyle().setColor("#FFFFFF");
		buttonPanel.add(lbl);
		
		for(OverlayType type: OverlayType.values()) {
			RadioButton button  = new RadioButton("OverlayTypes", type.getName());
			button.getElement().getStyle().setDisplay(Display.BLOCK);
			button.addClickHandler(this);
			buttonPanel.add(button);
		}		
		return buttonPanel;
	}

	@Override
	public void onClick(ClickEvent event) {
		RadioButton btn = (RadioButton) event.getSource();
		eventBus.fireEventFromSource(
				new MakeOverlayRequestEvent(
						OverlayType.getType(btn.getText())),
						this);
	}
	
	protected void reset() {
		for(int i=0; i<buttonPanel.getWidgetCount(); i++) {
			Widget widget = buttonPanel.getWidget(i);
			if(widget instanceof RadioButton)
				((RadioButton) widget).setValue(false);
		}
	}

	public void selectType(String dataType) {
		for(int i=0; i<buttonPanel.getWidgetCount(); i++) {
			Widget widget = buttonPanel.getWidget(i);
			if(widget instanceof RadioButton && OverlayType.getType(((RadioButton) widget).getText()) == OverlayType.getType(dataType))
				((RadioButton) widget).setValue(true);
		}
	}
}
