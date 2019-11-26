package org.reactome.web.fi.legends;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayControlLegend extends LegendPanel implements ClickHandler, OverlayDataLoadedHandler, OverlayDataResetHandler{

	private PwpButton closeBtn;
	private FlowPanel innerPanel;
	
	public OverlayControlLegend(EventBus eventBus) {
		super(eventBus);
		
		LegendPanelCSS css = RESOURCES.getCSS();
		
		this.closeBtn = new PwpButton("Close", css.close(), this);
		this.add(this.closeBtn);
		
		this.innerPanel = new FlowPanel();
		innerPanel.setStyleName(IDGRESOURCES.getCSS().innerPanel());
		this.add(innerPanel);
		this.getElement().getStyle().setMarginBottom(10, Unit.PX);
				
		initHandlers();
		
		addStyleName(RESOURCES.getCSS().enrichmentControl());
		this.setVisible(false);
	}

	private void initHandlers() {
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource().equals(this.closeBtn))
			eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.setVisible(false);
		if(event.getDataOverlay().isDiscrete())
			showDiscretePanel(event);
		if(!event.getDataOverlay().isDiscrete())
			showContinuousPanel();
		this.setVisible(true);
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		innerPanel.clear();
		this.setVisible(false);
	}
	
	private void showDiscretePanel(OverlayDataLoadedEvent event) {
		FlowPanel discretePanel = new FlowPanel();
		discretePanel.add(new Label(event.getDataOverlay().getEType()));
		innerPanel.add(discretePanel);
	}
	
	private void showContinuousPanel() {
		// TODO Auto-generated method stub
		
	}
	
	//Below here is for styling
	public static Resources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(Resources.class);
		IDGRESOURCES.getCSS().ensureInjected();
	}
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-OverlayControlLegend")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/legends/OverlayControlLegend.css";
		
		String innerPanel();
	}
}
