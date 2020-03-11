package org.reactome.web.fi.client.popups;

import java.util.HashSet;
import java.util.Set;

import org.reactome.web.fi.events.OverlayDataLoadedEvent;
import org.reactome.web.fi.events.OverlayDataResetEvent;
import org.reactome.web.fi.events.PairwiseInteractorsResetEvent;
import org.reactome.web.fi.events.PairwiseNumbersLoadedEvent;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.handlers.OverlayDataResetHandler;
import org.reactome.web.fi.handlers.PairwiseInteractorsResetHandler;
import org.reactome.web.fi.handlers.PairwiseNumbersLoadedHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class NodeContextPanel extends Composite implements OverlayDataLoadedHandler, OverlayDataResetHandler,
PairwiseNumbersLoadedHandler, PairwiseInteractorsResetHandler{

	private EventBus eventBus;
	
	private Set<Button> btns = new HashSet<>();
	private Button infoButton;
	private Button dataOverlayButton;
	private Button interactorsButton;
	
	private DeckLayoutPanel container;
	private NodeOverlayPanel nodeOverlayPanel;
	private NodeInteractorsPanel nodeInteractorsPanel;
	
	public NodeContextPanel(EventBus eventBus, String id, String name, DataOverlay overlay) {
		this.eventBus = eventBus;
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(RESOURCES.getCSS().buttonsPanel());
		buttonsPanel.add(this.infoButton = getButton("Info", RESOURCES.info()));
		buttonsPanel.add(this.dataOverlayButton = getButton("Overlay", RESOURCES.overlay()));
		buttonsPanel.add(this.interactorsButton = getButton("Interactors", RESOURCES.interactors()));
	
		//set Interactors button to enabled or disabled
		boolean enabled = PairwiseOverlayFactory.get().getCurrentPairwiseProperties() != null && PairwiseOverlayFactory.get().getCurrentPairwiseProperties().size() > 0;
		this.interactorsButton.setEnabled(enabled);
		
		//set dialog button to enabled only if overlay is other than Target Dev Level
		enabled = overlay != null && !overlay.getEType().equals("Target Development Level");
		this.dataOverlayButton.setEnabled(enabled);
		
		this.infoButton.addStyleName(RESOURCES.getCSS().buttonSelected());
		
		this.container = new DeckLayoutPanel();
		this.container.setStyleName(RESOURCES.getCSS().container());
		NodeInfoPanel nodeInfoPanel = new NodeInfoPanel(eventBus, id, name);
		nodeOverlayPanel = new NodeOverlayPanel(eventBus, id, overlay);
		nodeInteractorsPanel = new NodeInteractorsPanel(eventBus, id, name);
		this.container.add(nodeInfoPanel);
		this.container.add(nodeOverlayPanel);
		this.container.add(nodeInteractorsPanel);
		this.container.showWidget(0);
		this.container.setAnimationVertical(true);
		this.container.setAnimationDuration(500);
		
		FlowPanel outerPanel = new FlowPanel();
		outerPanel.setStyleName(RESOURCES.getCSS().outerPanel());
		outerPanel.add(buttonsPanel);
		outerPanel.add(container);
		
		initHandlers();
		
		initWidget(outerPanel);
	}
	
	private void initHandlers() {
		eventBus.addHandler(OverlayDataLoadedEvent.TYPE, this);
		eventBus.addHandler(OverlayDataResetEvent.TYPE, this);
		eventBus.addHandler(PairwiseNumbersLoadedEvent.TYPE, this);
		eventBus.addHandler(PairwiseInteractorsResetEvent.TYPE, this);
	}

	private Button getButton(String text, ImageResource imageResource) {
		Image buttonImg = new Image(imageResource);
		Label buttonLbl = new Label(text);
		
		FlowPanel fp = new FlowPanel();
		fp.add(buttonImg);
		fp.add(buttonLbl);
		
		SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
		Button btn = new Button(safeHtml);
		btn.addClickHandler(e -> onClick(e));
		this.btns.add(btn);
		
		return btn;
	}

	private void onClick(ClickEvent e) {
		for(Button btn : btns)
			btn.removeStyleName(RESOURCES.getCSS().buttonSelected());
		
		Button btn = (Button) e.getSource();
		btn.addStyleName(RESOURCES.getCSS().buttonSelected());
		if(btn.equals(this.infoButton))
			this.container.showWidget(0);
		else if(btn.equals(this.dataOverlayButton))
			this.container.showWidget(1);
		else if(btn.equals(this.interactorsButton))
			this.container.showWidget(2);
	}
	
	@Override
	public void onPairwiseNumbersLoaded(PairwiseNumbersLoadedEvent event) {
		this.interactorsButton.setEnabled(true);
		nodeInteractorsPanel.updateWidget();
	}
	
	@Override
	public void onPairwiseInteractorsReset(PairwiseInteractorsResetEvent event) {
		if(this.container.getVisibleWidget() == nodeInteractorsPanel) {
			this.container.showWidget(0);
		}
		this.interactorsButton.setEnabled(false);
		nodeInteractorsPanel.updateWidget();

	}

	@Override
	public void onOverlayDataReset(OverlayDataResetEvent event) {
		this.container.showWidget(0);
		this.dataOverlayButton.setEnabled(false);
		nodeOverlayPanel.updateOverlay(null);
	}

	@Override
	public void onOverlayDataLoaded(OverlayDataLoadedEvent event) {
		nodeOverlayPanel.updateOverlay(event.getDataOverlay());
		this.container.showWidget(1);
		this.dataOverlayButton.setEnabled(true);
	}
	
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/info_icon.png")
		ImageResource info();
		
		@Source("images/overlay_icon.png")
		ImageResource overlay();
		
		@Source("images/interactors.png")
		ImageResource interactors();
		
	}
	
	@CssResource.ImportedWithPrefix("idg-NodeInfoPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/popups/NodeContextPanel.css";
		
		String buttonsPanel();
		
		String buttonSelected();
		
		String container();
		
		String outerPanel();
	}
}
