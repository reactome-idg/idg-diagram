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
		main.add(getOverlayWidget("Choose Data Overlays:", "Choose Entity Overlay"));
				
		initWidget(main);
		
	}

	private Widget getOverlayWidget(String dataTitle, String entityTitle) {
		buttonPanel = new FlowPanel();
		Label lbl = new Label(dataTitle);
		lbl.setStyleName(IDGRESOURCES.getCSS().label());
		buttonPanel.add(lbl);
		
		for(OverlayDataType type: OverlayDataType.values()) {
			RadioButton button  = new RadioButton("OverlayDataTypes", type.getName());
			button.getElement().getStyle().setDisplay(Display.BLOCK);
			button.addClickHandler(this);
			buttonPanel.add(button);
		}		
		
		Label entityLbl = new Label(dataTitle);
		entityLbl.setStyleName(IDGRESOURCES.getCSS().label());
		buttonPanel.add(entityLbl);
		
		for(OverlayEntityType type: OverlayEntityType.values()) {
			RadioButton button = new RadioButton("OverlayEntityTypes", type.getName());
			button.getElement().getStyle().setDisplay(Display.BLOCK);
			button.addClickHandler(this);
			buttonPanel.add(button);
		}
		
		//TODO: add entity overlay types
		
		return buttonPanel;
	}

	@Override
	public void onClick(ClickEvent event) {
		RadioButton btn = (RadioButton) event.getSource();
		if(btn.getName() == "OverlayDataTypes") {
			eventBus.fireEventFromSource(
					new MakeOverlayRequestEvent(
							OverlayDataType.lookupType(btn.getText())),
							this);
		} else {
			eventBus.fireEventFromSource(
					new MakeOverlayRequestEvent(OverlayEntityType.lookupType(btn.getText())), this);
		}
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
			if(widget instanceof RadioButton) {
				if(OverlayDataType.lookupType(((RadioButton) widget).getText()) == OverlayDataType.lookupType(dataType)) {
					((RadioButton) widget).setValue(true);
					return;
				}
				else if(OverlayEntityType.lookupType(((RadioButton)widget).getText()) == OverlayEntityType.lookupType(dataType)) {
					((RadioButton)widget).setValue(true);
					return;
				}
			}
		}
	}
	
	public static Resources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(Resources.class);
		IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-OverlayTypePanel")
	public interface ResourceCSS extends CssResource  {
		String CSS = "org/reactome/web/fi/overlay/OverlayTypePanel.css";
		
		String label();
	}
}
