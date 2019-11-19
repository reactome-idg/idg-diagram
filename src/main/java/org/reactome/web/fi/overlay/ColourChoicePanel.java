package org.reactome.web.fi.overlay;

import com.google.gwt.user.client.ui.*;

import java.util.List;
import java.util.Map;

import org.reactome.web.fi.overlay.profiles.OverlayColours;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * 
 * @author brunsont
 *
 */
public class ColourChoicePanel extends Composite{
	
	private EventBus eventbus;
	private FlowPanel main;
	private ScrollPanel coloursScrollPanel;
	
	public ColourChoicePanel(EventBus eventBus) {
		this.eventbus = eventBus;
		
		main = new FlowPanel();
		Label lbl = new Label("Change Overlay Colours:");
		lbl.getElement().getStyle().setColor("#005A75");
		
		coloursScrollPanel = new ScrollPanel();
		coloursScrollPanel.setHeight("60px");
		
		main.add(lbl);
		main.add(coloursScrollPanel);
		initWidget(main);
	}

	protected void setColourLabels(List<String> list) {
		FlowPanel result = new FlowPanel();
		Map<Double, String> overlayColours = OverlayColours.get().getColours();
		
		overlayColours.forEach((i,j) ->{
			Label colourLbl = new Label(list.get(i.intValue()));
			colourLbl.getElement().getStyle().setBackgroundColor(j);
			result.add(colourLbl);
		});
		ScrollPanel panel = new ScrollPanel();
		panel.add(result);
		panel.setHeight("128px");
		coloursScrollPanel = panel;
		
		for(int i=1; i<main.getWidgetCount(); i++) 
			main.remove(i);
		main.add(coloursScrollPanel);
	}
	
	protected void resetColours() {
		for(int i=1; i<main.getWidgetCount(); i++) 
			main.remove(i);
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
