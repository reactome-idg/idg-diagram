package org.reactome.web.fi.overlay;

import org.reactome.web.diagram.common.PwpButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.diagram.context.ContextDialogPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayDialogPanel extends DialogBox implements ClickHandler,
OverlayDataLoadedHandler{

	private EventBus eventBus;
	private Button close;
	private Button minimize;
	private OverlayInfoPanel infoPanel;
	
	public OverlayDialogPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		setAutoHideEnabled(false);
		setModal(false);
		setStyleName(ContextDialogPanel.RESOURCES.getCSS().popup());
		
		FlowPanel fp = new FlowPanel();
		fp.add(this.minimize = new PwpButton("Minimize", IDGRESOURCES.getCSS().minimize(), this));
		fp.add(this.close = new PwpButton("Close Overlays",
										  ContextDialogPanel.RESOURCES.getCSS()
										  .close(),
										  this));
		fp.add(this.infoPanel = new OverlayInfoPanel(eventBus));
		setTitlePanel();
		setWidget(fp);
		this.addStyleName(ContextDialogPanel.RESOURCES.getCSS()
						  .popupSelected());
		
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		
		show();

	}

	private void setTitlePanel() {
		InlineLabel title = new InlineLabel("Overlay Resources");
		SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(title.toString());
		getCaption().setHTML(safeHtml);
		getCaption().asWidget().setStyleName(ContextDialogPanel.RESOURCES.getCSS()
											 .header());
	}

	//position set in IdgViewerContainer
	@SuppressWarnings("unused")
	private void setPosition(int x, int y) {
		setPopupPosition(x,y);
		
	}
	
	public void show() {
		super.show();
	}
	
	public void hide() {
		super.hide();
	}

	@Override
	public void onClick(ClickEvent event) {
		Button btn = (Button) event.getSource();
		if(btn.equals(close)) {
			hide();
		}
		if(btn.equals(minimize)) {
			if(infoPanel.isVisible()) {
				infoPanel.setVisible(false);
			}
			else {infoPanel.setVisible(true);}
		}
	}
	
	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.show();
	}
	
	public static Resources IDGRESOURCES;
	static {
			IDGRESOURCES = GWT.create(Resources.class);
			IDGRESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/Minimize_normal.png")
		ImageResource minimizeNormal();
		
		@Source("images/Minimize_hovered.png")
		ImageResource minimizeHovered();
		
		@Source("images/Minimize_clicked.png")
		ImageResource minimizeClicked();
	}
	
	@CssResource.ImportedWithPrefix("idg-diagram-OverlayDialogPanel")
	public interface ResourceCSS extends CssResource{
		
		String CSS = "org/reactome/web/fi/overlay/OverlayDialogPanel.css";
		
		String minimize();
	}
}
