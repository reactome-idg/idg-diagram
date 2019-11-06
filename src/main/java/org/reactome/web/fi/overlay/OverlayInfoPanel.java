package org.reactome.web.fi.overlay;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.diagram.context.ContextInfoPanel;
import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayInfoPanel extends Composite implements ClickHandler,
OverlayDataResetHandler, OverlayDataLoadedHandler{
	
	EventBus eventBus;
	private Button overlayTypes;
	private Button colours;
	private List<Button> btns = new LinkedList<>();
	private OverlayTypePanel overlayTypePanel;
	private ColourChoicePanel colourChoicePanel;
	private String currentOverlayType;
	
	private DeckLayoutPanel container;
	
	public OverlayInfoPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(ContextInfoPanel.RESOURCES.getCSS()
								  .buttonsPanel());
		buttonsPanel.add(this.overlayTypes =  getButton("Overlays", IDGRESOURCES.bwOverlayIcon()));
		buttonsPanel.add(this.colours =  getButton("Colours", IDGRESOURCES.colourPicker()));
		
		this.overlayTypes.addStyleName(ContextInfoPanel.RESOURCES.getCSS()
									   .buttonSelected());
		
		this.container = new DeckLayoutPanel();
		this.container.setStyleName(ContextInfoPanel.RESOURCES.getCSS()
				   					.container());
		
		//adding panels to DeckPanel container
		this.overlayTypePanel = new OverlayTypePanel(eventBus);
		this.container.add(overlayTypePanel);
		this.colourChoicePanel = new ColourChoicePanel(eventBus);
		this.container.add(colourChoicePanel);
		
		this.container.showWidget(0);
		this.container.setAnimationVertical(true);
		this.container.setAnimationDuration(500);
		
		FlowPanel outerPanel = new FlowPanel();
		outerPanel.setStyleName(ContextInfoPanel.RESOURCES.getCSS().outerPanel());
		outerPanel.add(buttonsPanel);
		outerPanel.add(this.container);
		
		initHandlers();
		initWidget(outerPanel);
		
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

	private void initHandlers() {
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
	}

	@Override
	public void onClick(ClickEvent event) {
		for(Button btn: btns) {
			btn.removeStyleName(ContextInfoPanel.RESOURCES.getCSS()
									   .buttonSelected());
		}
		Button btn = (Button) event.getSource();
		btn.addStyleName(ContextInfoPanel.RESOURCES.getCSS()
						 .buttonSelected());
		if(btn.equals(this.overlayTypes))
			this.container.showWidget(0);
		else if(btn.equals(this.colours))
			this.container.showWidget(1);
	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.overlayTypePanel.reset();
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		this.currentOverlayType = event.getEntities().getDataType();
		this.colourChoicePanel.setColourLabels();
		selectOverlayType();
		}
	
	public void selectOverlayType() {
		this.overlayTypePanel.selectType(currentOverlayType);
	}
	
	public static Resources IDGRESOURCES;
	static {
		IDGRESOURCES = GWT.create(Resources.class);
	}
	
	public interface Resources extends ClientBundle{
		@Source("images/bwOverlayIcon.png")
		ImageResource bwOverlayIcon();
		
		@Source("images/Color_Picker.png")
		ImageResource colourPicker();
	}
}
