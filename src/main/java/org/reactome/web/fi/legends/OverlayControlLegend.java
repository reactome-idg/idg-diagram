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
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayControlLegend extends LegendPanel implements ClickHandler, OverlayDataLoadedHandler, OverlayDataResetHandler{

	private PwpButton closeBtn;
	private FlowPanel innerPanel;
	private PwpButton backButton;
	private PwpButton forwardButton;
	
	public OverlayControlLegend(EventBus eventBus) {
		super(eventBus);
		
		LegendPanelCSS css = RESOURCES.getCSS();
		
		backButton = new PwpButton("Show previous", IDGRESOURCES.getCSS().back(), this);
		this.add(backButton);
		forwardButton = new PwpButton("Show next", IDGRESOURCES.getCSS().forward(), this);
		this.add(forwardButton);
		
		this.innerPanel = new FlowPanel();
		innerPanel.setStyleName(IDGRESOURCES.getCSS().innerPanel());
		this.add(innerPanel);
		this.getElement().getStyle().setMarginBottom(10, Unit.PX);
		
		this.closeBtn = new PwpButton("Close", css.close(), this);
		this.add(this.closeBtn);
		
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
		innerPanel.clear();
		
		if(event.getDataOverlay().getDataOverlayEntities() == null) {
			showNoResultsPanel();
			this.setVisible(true);
			return;
		}
		
		if(event.getDataOverlay().getTissueTypes().size() <= 1)
			showSingleTissuePanel(event);
		else if(event.getDataOverlay().getTissueTypes().size() > 1)
			showMultipleTissuePanel(event);
		this.setVisible(true);
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		innerPanel.clear();
		this.setVisible(false);
	}
	
	private void showSingleTissuePanel(OverlayDataLoadedEvent event) {
		innerPanel.clear();
		forwardButton.setEnabled(false);
		backButton.setEnabled(false);
		InlineLabel label = new InlineLabel(event.getDataOverlay().getEType());
		innerPanel.add(label);
	}
	
	private void showMultipleTissuePanel(OverlayDataLoadedEvent event) {
		innerPanel.clear();
		forwardButton.setEnabled(true);
		backButton.setEnabled(true);
		FlowPanel infoPanel = new FlowPanel();
		InlineLabel stepLabel = new InlineLabel((event.getDataOverlay().getColumn()+1) + "/" + event.getDataOverlay().getTissueTypes().size() + "  ");
		infoPanel.add(stepLabel);
		InlineLabel typeTissueLabel  = new InlineLabel(event.getDataOverlay().getEType() + " - " + event.getDataOverlay().getTissueTypes().get(event.getDataOverlay().getColumn()));
		infoPanel.add(typeTissueLabel);
		
		innerPanel.add(infoPanel);
	}
	
	private void showNoResultsPanel() {
		innerPanel.clear();
		forwardButton.setEnabled(false);
		backButton.setEnabled(false);
		FlowPanel noResults = new FlowPanel();
		noResults.add(new Label("No Results. Please try different options!"));
		innerPanel.add(noResults);
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
		
		@Source("images/forward_clicked.png")
		ImageResource forwardClicked();
		
		@Source("images/forward_disabled.png")
		ImageResource forwardDisabled();
		
		@Source("images/forward_hovered.png")
		ImageResource forwardHovered();
		
		@Source("images/forward_normal.png")
		ImageResource forwardNormal();
		
		@Source("images/rewind_clicked.png")
		ImageResource backClicked();
		
		@Source("images/rewind_disabled.png")
		ImageResource backDisabled();
		
		@Source("images/rewind_hovered.png")
		ImageResource backHovered();
		
		@Source("images/rewind_normal.png")
		ImageResource backNormal();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-OverlayControlLegend")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/legends/OverlayControlLegend.css";
		
		String innerPanel();
		
		String forward();
		
		String back();
	}
}
