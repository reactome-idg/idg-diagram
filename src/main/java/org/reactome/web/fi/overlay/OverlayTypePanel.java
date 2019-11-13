package org.reactome.web.fi.overlay;

import org.reactome.web.fi.model.OverlayDataType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("unused")
public abstract class OverlayTypePanel extends Composite implements ClickHandler{

	private EventBus eventBus;
	private FlowPanel main;
	private FlowPanel buttonPanel;
	private ScrollPanel scrollPanel;
	
	public OverlayTypePanel(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	protected Widget getOverlayWidget(String widgetTitle) {
		buttonPanel = new FlowPanel();
		
		//add data overlay types that overlay onto current entities
		Label lbl = new Label(widgetTitle);
		lbl.setStyleName(IDGRESOURCES.getCSS().label());
		buttonPanel.add(lbl);	
				
		return buttonPanel;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
	}
	
	protected abstract void reset();

	public abstract void selectType();

	public static Resources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(Resources.class);
		IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-DataOverlayTypePanel")
	public interface ResourceCSS extends CssResource  {
		String CSS = "org/reactome/web/fi/overlay/OverlayTypePanel.css";
		
		String radioButton();
		
		String label();
	}
	
}
