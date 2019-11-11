package org.reactome.web.fi.overlay;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import org.reactome.web.diagram.context.ContextDialogPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.overlay.OverlayAnimation;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayDialogPanel extends AbsolutePanel implements OverlayDataLoadedHandler{

	private EventBus eventBus;
	private OverlayInfoPanel infoPanel;
	private OverlayAnimation animation = new OverlayAnimation();


	public OverlayDialogPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		FlowPanel fp = new FlowPanel();
		this.setStyleName(ContextDialogPanel.RESOURCES.getCSS().popup());
		this.addStyleName(ContextDialogPanel.RESOURCES.getCSS().popupSelected());
		this.addStyleName(IDGRESOURCES.getCSS().panel());
		this.getElement().getStyle().setMarginLeft(170, Unit.PX);
		Label lbl = new Label("Reactome Overlay Resources");
		lbl.setStyleName(IDGRESOURCES.getCSS().label());
		fp.add(lbl);
		fp.add(this.infoPanel = new OverlayInfoPanel(this.eventBus));
		this.add(fp);
		
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		
	}

	//position set in IdgViewerContainer
	@SuppressWarnings("unused")
	private void setPosition(int x, int y) {
		this.setPosition(x, y);
		
	}
	
	public void show() {
		this.getElement().getStyle().setDisplay(Display.BLOCK);
		animation.animateOverlay(0, 169, 500, this);
	}
	
	public void hide() {
		animation.animateOverlay(this.getOffsetHeight(), 0, 500, this);
	}
	
	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.setVisible(true);
	}

	public void selectOverlayType() {
		infoPanel.selectOverlayType();
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
		
		String label();
	}
}
