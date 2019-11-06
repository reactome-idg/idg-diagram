package org.reactome.web.fi.legends;

import java.util.Map;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.legends.LegendPanel;
import org.reactome.web.fi.client.visualisers.diagram.profiles.OverlayColours;
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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayLegend extends LegendPanel implements ClickHandler,
OverlayDataLoadedHandler, OverlayDataResetHandler{
	
	private PwpButton closeBtn;
	private FlowPanel colourMapPanel;

	public OverlayLegend(EventBus eventBus) {
		super(eventBus);
		
		LegendPanelCSS css = RESOURCES.getCSS();
		
		this.closeBtn = new PwpButton("Close", css.close(), this);
		this.add(this.closeBtn);
		
		this.colourMapPanel = new FlowPanel();
		this.add(colourMapPanel);
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
		if(event.getSource().equals(this.closeBtn)) {
			eventBus.fireEventFromSource(new OverlayDataResetEvent(), this);
		}
		
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.remove(colourMapPanel);
		colourMapPanel = new FlowPanel();
		this.add(colourMapPanel);
		colourMapPanel.setStyleName(IDGRESOURCES.getCSS().colourMapPanel());
		Map<String, String> map = OverlayColours.get().getColours(event.getEntities().getDataType());
		Label title = new Label("Overlay Value Types: ");
		colourMapPanel.add(title);
		map.forEach((k, v) -> {
			if(k != "default") {
				InlineLabel lbl = new InlineLabel(k);
				lbl.getElement().getStyle().setBackgroundColor(v);
				lbl.getElement().getStyle().setPadding(3, Unit.PX);
				lbl.getElement().getStyle().setMargin(0, Unit.PX);
				colourMapPanel.add(lbl);
			}
		});
		
		this.setVisible(true);

	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.remove(colourMapPanel);
		colourMapPanel = new FlowPanel();
		this.add(colourMapPanel);
		this.setVisible(false);
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
	
	@CssResource.ImportedWithPrefix("idgDiagram-colorChoicePanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/legends/OverlayLegend.css";
		
		String colourMapPanel();
	}
	
}
