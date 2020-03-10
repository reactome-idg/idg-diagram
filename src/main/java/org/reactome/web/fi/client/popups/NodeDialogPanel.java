package org.reactome.web.fi.client.popups;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.events.PairwiseOverlayButtonClickedEvent;
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class NodeDialogPanel extends DialogBox{

	private EventBus eventBus;
	private String name;
	private String id;
	private DataOverlay overlay;
	
	private boolean pinned = false;
	
	private Button pin;
	
	public NodeDialogPanel(EventBus eventBus, String name, String id, DataOverlay overlay) {
		super();
		
		this.eventBus = eventBus;
		this.name = name;
		this.id = id;
		this.overlay = overlay;
		
		initPanel();
	}

	private void initPanel() {
		setAutoHideEnabled(true);
		setModal(false);
		setStyleName(RESOURCES.getCSS().popup());
		
		FlowPanel fp = new FlowPanel();
		fp.add(new PwpButton("Show Pairwise Relationships", RESOURCES.getCSS().pairwiseOverlayButton(), e -> onPairwiseOverlayButtonClicked()));
		fp.add(this.pin = new PwpButton("Keep the panel visible", RESOURCES.getCSS().pin(), e -> onPinButtonClicked()));
		fp.add(new PwpButton("Close", RESOURCES.getCSS().close(), e -> onCloseButtonClicked()));
		fp.add(new NewNodeContextPanel(eventBus, id, name, overlay));
	
		setTitlePanel();
		setWidget(fp);
		show();
	}
	
	private void setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		Image img = new Image(RESOURCES.entity());
		fp.add(img);
		
		InlineLabel title = new InlineLabel(this.name);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
	}

	private void onCloseButtonClicked() {
		this.pinned = false;
		hide();
	}

	private void onPinButtonClicked() {
		this.pinned = !pinned;
		
		if(this.pinned)
			pin.setStyleName(RESOURCES.getCSS().pinActive());
		else
			pin.setStyleName(RESOURCES.getCSS().pin());
	}

	private void onPairwiseOverlayButtonClicked() {
		eventBus.fireEventFromSource(new PairwiseOverlayButtonClickedEvent(id, name), this);
	}
	
	@Override
	public void hide(boolean autoClosed) {
		if(autoClosed && !this.pinned)
			super.hide(autoClosed);
		else if(!autoClosed)
			super.hide(autoClosed);
	}

	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/pairwise_normal.png")
        ImageResource pairwiseNormal();
        
        @Source("images/pairwise_hovered.png")
        ImageResource pairwiseHovered();
        
        @Source("images/pairwise_clicked.png")
        ImageResource pairwiseClicked();
        
        @Source("images/pin_clicked.png")
        ImageResource pinClicked();

        @Source("images/pin_hovered.png")
        ImageResource pinHovered();

        @Source("images/pin_normal.png")
        ImageResource pinNormal();
        
        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
        
        @Source("images/entity.gif")
        ImageResource entity();
	}
	
	@CssResource.ImportedWithPrefix("idg-FINodePpup")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/popups/NodeDialogPanel.css";
		
		String popup();
		
		String pairwiseOverlayButton();
		
		String pin();
		
		String pinActive();
		
		String close();
		
		String header();
	}
}
