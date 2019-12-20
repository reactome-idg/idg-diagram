package org.reactome.web.fi.tools.overlay;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayLauncherDisplay extends PopupPanel implements ResizeHandler,
	ClickHandler, CloseHandler<PopupPanel>{

	EventBus eventBus;
	private Button dataOverlayTypes;
	private Button entityOverlayTypes;
	
	private DataOverlayPanel dataOverlayPanel;
	private EntityOverlayPanel entityOverlayPanel;
	
	private List<Button> btns = new LinkedList<>();
	
	private DeckLayoutPanel container;
	
	public OverlayLauncherDisplay(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		initPanel();
	}

	private void initPanel() {
		this.setAutoHideEnabled(true);
		this.setModal(true);
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setAutoHideOnHistoryEventsEnabled(false);
		this.addStyleName(RESOURCES.getCSS().popupPanel());
		Window.addResizeHandler(this);
		
		int width = (int) Math.round(Window.getClientWidth() * 0.5);
		int height = (int) Math.round(Window.getClientHeight() * 0.5);
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		
		FlowPanel panel = new FlowPanel(); //Main panel
		panel.addStyleName(RESOURCES.getCSS().overlayPanel());
		panel.add(setTitlePanel());
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
        buttonsPanel.addStyleName(RESOURCES.getCSS().unselectable());
        buttonsPanel.add(this.dataOverlayTypes = getButton("Overlay Data", RESOURCES.dataOverlayIcon()));
        buttonsPanel.add(this.entityOverlayTypes = getButton("Overlay Relationships", RESOURCES.overlayIcon()));
        this.dataOverlayTypes.addStyleName(RESOURCES.getCSS()
				   .buttonSelected());
        
        this.container = new DeckLayoutPanel();
        this.container.addStyleName(RESOURCES.getCSS().container());
        this.container.add(dataOverlayPanel = new DataOverlayPanel(eventBus));
        this.container.add(entityOverlayPanel = new EntityOverlayPanel(eventBus));
        this.container.add(new Label("Relationship overlay!"));
        this.container.showWidget(0);
        this.container.setAnimationVertical(true);
        this.container.setAnimationDuration(500);
        
        FlowPanel innerPanel = new FlowPanel();
        innerPanel.setStyleName(RESOURCES.getCSS().innerPanel());
        innerPanel.add(buttonsPanel);
        innerPanel.add(this.container);
        
        panel.add(innerPanel);
        this.addCloseHandler(this);
        this.add(panel);
	}

	private Button getButton(String text, ImageResource imageResource) {
		Image btnImg = new Image(imageResource);
		Label btnLbl = new Label(text);
		
		FlowPanel fp = new FlowPanel();
		fp.add(btnImg);
		fp.add(btnLbl);
		SafeHtml safe = SafeHtmlUtils.fromSafeConstant(fp.toString());
		Button btn = new Button(safe, this);
		this.btns.add(btn);
		return btn;
	}
	
	private FlowPanel setTitlePanel() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().header());
		result.addStyleName(RESOURCES.getCSS().unselectable());
		Image headerImg = new Image(RESOURCES.overlayIcon());
		headerImg.setStyleName(RESOURCES.getCSS().headerIcon());
		headerImg.addStyleName(RESOURCES.getCSS().undraggable());
		result.add(headerImg);
		Label title = new Label("Overlay Tool");
		title.setStyleName(RESOURCES.getCSS().headerText());
		result.add(title);
		result.add(getCloseButton());
		return result;
	}

	private Button getCloseButton() {
		Button closeBtn = new Button();
		closeBtn.setStyleName(RESOURCES.getCSS().close());
		closeBtn.setTitle("Close Overlay Tool");
		closeBtn.addClickHandler(clickEvent -> OverlayLauncherDisplay.this.hide());
		return closeBtn;
	}
	
	@Override
	public void hide() {
		dataOverlayPanel.hideLoader();
		super.hide();
	}

	@Override
	public void onResize(ResizeEvent event) {
		if(isVisible()){
            int width = (int) Math.round(RootLayoutPanel.get().getOffsetWidth() * 0.5);
            int height = (int) Math.round(RootLayoutPanel.get().getOffsetHeight() * 0.5);
            this.setWidth(width + "px");
            this.setHeight(height + "px");
        }
	}
	
	@Override
	public void onClick(ClickEvent event) {
		for(Button btn : btns)
			btn.removeStyleName(RESOURCES.getCSS().buttonSelected());
		Button btn = (Button) event.getSource();
		btn.addStyleName(RESOURCES.getCSS().buttonSelected());
		if(btn.equals(this.dataOverlayTypes))
			this.container.showWidget(0);
		else if(btn.equals(this.entityOverlayTypes))
			this.container.showWidget(1);
	}

	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		this.hide();
	}
	
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/bwOverlayIcon.png")
		ImageResource overlayIcon();
		
		@Source("images/DataOverlayIcon.png")
		ImageResource dataOverlayIcon();
		
		@Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
	}

	@CssResource.ImportedWithPrefix("idg-OverlayLauncher")
	public interface ResourceCSS extends CssResource{
		
		String CSS = "org/reactome/web/fi/tools/overlay/OverlayLauncher.css";
		
		String popupPanel();
		
		String overlayPanel();
		
		String header();
		
		String unselectable();
		
		String headerIcon();
		
		String undraggable();
		
		String headerText();
		
		String close();
		
		String buttonsPanel();
		
		String container();
		
		String innerPanel();
		
		String buttonSelected();
				
	}
	
}
