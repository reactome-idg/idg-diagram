package org.reactome.web.fi.overlay;

import com.google.gwt.user.client.ui.*;

import java.util.Map;

import org.reactome.web.fi.client.visualisers.diagram.profiles.OverlayColours;

import com.google.gwt.cell.client.ButtonCellBase.DefaultAppearance.Resources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * 
 * @author brunsont
 *
 */
public class ColourChoicePanel extends Composite {
	
	private EventBus eventbus;
	private FlowPanel main;
	private ScrollPanel coloursScrollPanel;
	
	public ColourChoicePanel(EventBus eventBus) {
		this.eventbus = eventBus;
		
		main = new FlowPanel();
		Label lbl = new Label("Change Overlay Colours:");
		lbl.getElement().getStyle().setColor("#FFFFFF");
		
		coloursScrollPanel = new ScrollPanel();
		coloursScrollPanel.setHeight("50px");
		
		main.add(lbl);
		main.add(coloursScrollPanel);
		initWidget(main);
	}

	protected void setColourLabels() {
		FlowPanel result = new FlowPanel();
		Map<String, Map<String, String>> overlayColours = OverlayColours.get().getOverlayColoursMap();
		
		overlayColours.forEach((k, v) -> {
			FlowPanel overlayTypePanel = new FlowPanel();
			Label overlayTypeLabel = new Label(k);
			overlayTypeLabel.setStyleName(RESOURCES.getCSS().overlayTypeLabel());
			overlayTypePanel.add(overlayTypeLabel);
			v.forEach((i,j) ->{
				Label colorLbl = new Label(i);
				colorLbl.getElement().getStyle().setBackgroundColor(j);
				overlayTypePanel.add(colorLbl);
			});
			result.add(overlayTypePanel);
		});
		ScrollPanel panel = new ScrollPanel();
		panel.add(result);
		panel.setHeight("128px");
		coloursScrollPanel = panel;
		
		for(int i=1; i<main.getWidgetCount(); i++) 
			main.remove(i);
		main.add(coloursScrollPanel);
	}
	
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-colorChoicePanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/overlay/ColourChoicePanel.css";
		
		String overlayTypeLabel();
	}
	
}
