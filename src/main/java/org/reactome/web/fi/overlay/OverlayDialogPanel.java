package org.reactome.web.fi.overlay;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;

import org.reactome.web.diagram.context.ContextDialogPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayDialogPanel extends AbsolutePanel implements OverlayDataLoadedHandler{

	private EventBus eventBus;
	private OverlayInfoPanel infoPanel;
	
	public OverlayDialogPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		FlowPanel fp = new FlowPanel();
		fp.setStyleName(ContextDialogPanel.RESOURCES.getCSS().popup());
		fp.addStyleName(ContextDialogPanel.RESOURCES.getCSS().popupSelected());
		fp.addStyleName(IDGRESOURCES.getCSS().panel());
		fp.add(this.infoPanel = new OverlayInfoPanel(this.eventBus));
		this.add(fp);
		
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
				
		this.setVisible(false);
		
	}

	//position set in IdgViewerContainer
	@SuppressWarnings("unused")
	private void setPosition(int x, int y) {
		this.setPosition(x, y);
		
	}
	
	
	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.setVisible(true);
	}

	public static Resources IDGRESOURCES;
	static {
			IDGRESOURCES = GWT.create(Resources.class);
			IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-diagram-OverlayDialogPanel")
	public interface ResourceCSS extends CssResource{
		
		String CSS = "org/reactome/web/fi/overlay/OverlayDialogPanel.css";
				
		String panel();
	}
}
