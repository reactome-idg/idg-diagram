package org.reactome.web.fi.legends;


import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.fi.legends.OverlayColourLegend.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * @author brunsont
 *
 */
public class ContinuousControlPanel extends FlowPanel implements ClickHandler{

	private EventBus eventBus;
	private PwpButton backButton;
	private PwpButton forwardButton;
	
	public ContinuousControlPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	//Below here is for styling
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	@CssResource.ImportedWithPrefix("idgDiagram-ContinuousControlPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/legends/ContinuousControlPanel";
	}
}
