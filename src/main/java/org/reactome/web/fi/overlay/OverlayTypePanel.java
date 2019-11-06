package org.reactome.web.fi.overlay;

import java.util.List;

import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import org.reactome.web.fi.model.OverlayType;
import org.reactome.web.fi.overlay.ColourChoicePanel.Resources;

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
		main.add(getOverlayWidget("Choose Overlays:"));
				
		initWidget(main);
		
	}

	private Widget getOverlayWidget(String title) {
		buttonPanel = new FlowPanel();
		Label lbl = new Label(title);
		lbl.setStyleName(IDGRESOURCES.getCSS().label());
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
						OverlayType.getTypeToString(btn.getText())),
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
			if(widget instanceof RadioButton && OverlayType.lookupType(((RadioButton) widget).getText()) == OverlayType.getTypeToString(dataType))
				((RadioButton) widget).setValue(true);
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
