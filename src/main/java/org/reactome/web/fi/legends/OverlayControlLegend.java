package org.reactome.web.fi.legends;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.events.DataOverlayColumnChangedEvent;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.model.DataOverlay;

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
public class OverlayControlLegend extends LegendPanel implements OverlayDataLoadedHandler, OverlayDataResetHandler{

	private PwpButton closeBtn;
	private FlowPanel innerPanel;
	private PwpButton backButton;
	private PwpButton forwardButton;
	private DataOverlay dataOverlay;
	
	public OverlayControlLegend(EventBus eventBus) {
		super(eventBus);
		
		LegendPanelCSS css = RESOURCES.getCSS();
		
		initPanel(css);
		
		initHandlers();

	}

	private void initPanel(LegendPanelCSS css) {
		backButton = new PwpButton("Show previous", IDGRESOURCES.getCSS().back(), e -> backButtonHandler());
		this.add(backButton);
		forwardButton = new PwpButton("Show next", IDGRESOURCES.getCSS().forward(), e -> forwardButtonHandler());
		this.add(forwardButton);
		
		this.innerPanel = new FlowPanel();
		innerPanel.setStyleName(IDGRESOURCES.getCSS().innerPanel());
		this.add(innerPanel);
		this.getElement().getStyle().setMarginBottom(10, Unit.PX);
		
		this.closeBtn = new PwpButton("Close", css.close(), e -> closeButtonHandler());
		this.add(this.closeBtn);
				
		addStyleName(RESOURCES.getCSS().enrichmentControl());
		this.setVisible(false);
	}

	private void closeButtonHandler() {
		eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
	}

	private void forwardButtonHandler() {
		if(this.dataOverlay.getColumn()+1 == this.dataOverlay.getTissueTypes().size()) {
			this.dataOverlay.setColumn(0);
			eventBus.fireEventFromSource(new DataOverlayColumnChangedEvent(0), this);
		}
		else {
			this.dataOverlay.setColumn(this.dataOverlay.getColumn()+1);
			eventBus.fireEventFromSource(new DataOverlayColumnChangedEvent(this.dataOverlay.getColumn()), this);
		}
		updateUI();
	}

	private void backButtonHandler() {
		if(this.dataOverlay.getColumn() == 0){
			this.dataOverlay.setColumn(this.dataOverlay.getTissueTypes().size()-1);
			eventBus.fireEventFromSource(new DataOverlayColumnChangedEvent(this.dataOverlay.getColumn()), this);
		}
		else {
			this.dataOverlay.setColumn(this.dataOverlay.getColumn()-1);
			eventBus.fireEventFromSource(new DataOverlayColumnChangedEvent(this.dataOverlay.getColumn()), this);
		}
		updateUI();
	}

	private void initHandlers() {
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.setVisible(false);
		innerPanel.clear();
		this.dataOverlay = event.getDataOverlay();
		if(event.getDataOverlay().getUniprotToEntitiesMap() == null) {
			showNoResultsPanel();
			this.setVisible(true);
			return;
		}
		
		updateUI();
	}

	/**
	 * Sets Text on inner panel based on tissueType number and selected tissue
	 */
	private void updateUI() {
		innerPanel.clear();
		if(this.dataOverlay.getTissueTypes().size() <= 1)
			showSingleTissuePanel();
		else if(this.dataOverlay.getTissueTypes().size() > 1)
			showMultipleTissuePanel();
		this.setVisible(true);
	}
	
	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		innerPanel.clear();
		this.setVisible(false);
	}
	
	private void showSingleTissuePanel() {
		forwardButton.setEnabled(false);
		backButton.setEnabled(false);
		String labelString = dataOverlay.getEType();
		InlineLabel label = new InlineLabel(dataOverlay.getEType());
		innerPanel.add(label);
		if(dataOverlay.getTissueTypes().size() > 0)
			labelString = labelString + " - " + dataOverlay.getTissueTypes().get(0);
		label.setText(labelString);
		innerPanel.add(label);

	}
	
	private void showMultipleTissuePanel() {
		forwardButton.setEnabled(true);
		backButton.setEnabled(true);
		FlowPanel infoPanel = new FlowPanel();
		InlineLabel stepLabel = new InlineLabel((dataOverlay.getColumn()+1) + "/" + dataOverlay.getTissueTypes().size() + "  ");
		infoPanel.add(stepLabel);
		InlineLabel typeTissueLabel  = new InlineLabel(dataOverlay.getEType() + " - " + dataOverlay.getTissueTypes().get(dataOverlay.getColumn()));
		infoPanel.add(typeTissueLabel);
		
		innerPanel.add(infoPanel);
	}
	
	private void showNoResultsPanel() {
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
