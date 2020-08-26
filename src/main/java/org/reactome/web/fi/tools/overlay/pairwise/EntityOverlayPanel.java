package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.events.HideOverlayLauncherEvent;
import org.reactome.web.fi.events.PairwiseInteractorsResetEvent;
import org.reactome.web.fi.events.RequestPairwiseCountsEvent;
import org.reactome.web.fi.handlers.PairwiseInteractorsResetHandler;
import org.reactome.web.fi.handlers.RequestPairwiseCountsHandler;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author brunsont
 *
 */
public class EntityOverlayPanel extends FlowPanel implements PairwiseFormPanel.Handler, RequestPairwiseCountsHandler,
PairwiseInteractorsResetHandler{
	
	private EventBus eventBus;
	
	private Button overlayButton;
	private FlowPanel existingFilterPanel;
	private PairwiseFormPanel pairwiseFormPanel;
	private InlineLabel infoLabel;
	
	private Map<String, PairwiseOverlayObject> selectedFilters;
	private Map<Button, String> removeToFilterMap;
		
	public EntityOverlayPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		selectedFilters = new HashMap<>();
		removeToFilterMap = new HashMap<>();
		this.getElement().getStyle().setMargin(5, Unit.PX);
		eventBus.addHandler(RequestPairwiseCountsEvent.TYPE, this);
		eventBus.addHandler(PairwiseInteractorsResetEvent.TYPE, this);
		
		initPanel();
	}
	
	private void initPanel() {
		SimplePanel title = new SimplePanel();
		title.add(new InlineLabel("Overlay Pairwise Relationships"));
		title.addStyleName(RESOURCES.getCSS().title());
		this.add(title);
		
		SimplePanel explanation = new SimplePanel();
		explanation.getElement().setInnerHTML(RESOURCES.pairwiseRelationshipInfo().getText());
		explanation.setStyleName(RESOURCES.getCSS().relationshipText());
		this.add(explanation);
		
		FlowPanel outerPanel = new FlowPanel();
		outerPanel.setStyleName(RESOURCES.getCSS().outerPanel());
		
		outerPanel.add(pairwiseFormPanel = new PairwiseFormPanel(this)); //so panel can pass back additions and removals from form
		
		existingFilterPanel = new FlowPanel();
		existingFilterPanel.setStyleName(RESOURCES.getCSS().existingFilterPanel());
		
		FlowPanel bottomContainer = new FlowPanel();
		bottomContainer.add(existingFilterPanel);
		bottomContainer.add(overlayButton = new Button("Overlay!"));
		overlayButton.setStyleName(RESOURCES.getCSS().overlayButton());
		overlayButton.addClickHandler(e -> overlayButtonClicked());
		
		bottomContainer.add(infoLabel = new InlineLabel());
		infoLabel.setStyleName(RESOURCES.getCSS().infoLabel());
		setInfoLabel();
		
		
		outerPanel.add(bottomContainer);
		
		this.add(outerPanel);
	}

	private void setInfoLabel() {
		infoLabel.setText(selectedFilters.size() + " of 6 selected.");
	}

	@Override
	public void onAddClicked(PairwiseOverlayObject obj) {
		if(selectedFilters.size() >= 6) {
			return;
		}
		selectedFilters.put(obj.getId(), obj);
		updateExistingFilterPanel();
		setInfoLabel();
	}
	
	/**
	 * Add button pair for every PairwiseOverlayObject passed in
	 */
	private void updateExistingFilterPanel() {
		existingFilterPanel.clear();
		selectedFilters.keySet().forEach(k -> {
			FlowPanel filterPanel = new FlowPanel();
			filterPanel.setStyleName(RESOURCES.getCSS().filterItemPanel());
			Button labelButton = new Button(k);
			labelButton.addClickHandler(e -> onLabelClicked(e));
			labelButton.setStyleName(RESOURCES.getCSS().filterItemLabelButton());
			filterPanel.add(labelButton);
			Button removeButton = new Button("X");
			removeButton.addClickHandler(e -> onRemoveClicked(e));
			removeButton.setStyleName(RESOURCES.getCSS().filterItemRemoveButton());
			filterPanel.add(removeButton);
			existingFilterPanel.add(filterPanel);
			
			removeToFilterMap.put(removeButton, k);
		});
	}

	private void onLabelClicked(ClickEvent e) {
		Button btn = (Button) e.getSource();
		PairwiseOverlayObject obj = selectedFilters.get(btn.getText());
		pairwiseFormPanel.insertData(obj);
	}

	/**
	 * Removes filter option from panel and selectedFilters map
	 * @param e
	 */
	private void onRemoveClicked(ClickEvent e) {
		for(int i=0; i<existingFilterPanel.getWidgetCount(); i++) {
			FlowPanel widget = (FlowPanel) existingFilterPanel.getWidget(i);
			if(widget.getWidget(1) == e.getSource()) {
				selectedFilters.remove(removeToFilterMap.get(widget.getWidget(1)));
				existingFilterPanel.remove(i);
				break;
			}
		}
		setInfoLabel();
	}

	@Override
	public void onRequestPairwiseCountsHandeler(RequestPairwiseCountsEvent event) {
		selectedFilters.clear();
		event.getPairwiseOverlayObjects().forEach(obj -> {
			selectedFilters.put(obj.getId(), obj);
		});
		updateExistingFilterPanel();
	}
	
	@Override
	public void onPairwiseInteractorsReset(PairwiseInteractorsResetEvent event) {
		this.selectedFilters.clear();
		this.existingFilterPanel.clear();
		this.removeToFilterMap.clear();
	}
	
	/**
	 * Handles agregation of data needed for overlay server call
	 */
	private void overlayButtonClicked() {
		if(selectedFilters.size() > 6) {
			return;
		}
		IDGPopupFactory.get().setCurrentPairwiseProperties(new ArrayList<PairwiseOverlayObject>(selectedFilters.values()));
		
		if(selectedFilters.size() > 0)
			eventBus.fireEventFromSource(new RequestPairwiseCountsEvent(new ArrayList<PairwiseOverlayObject>(selectedFilters.values())), this);
		else
			eventBus.fireEventFromSource(new PairwiseInteractorsResetEvent(), this);
		
		eventBus.fireEventFromSource(new HideOverlayLauncherEvent(), this);
	}

	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("pairwiseRelationshipInfo.html")
		TextResource pairwiseRelationshipInfo();
		
		@Source("images/loader.gif")
		ImageResource loader();
		
	}
	
	@CssResource.ImportedWithPrefix("idg-overlayEntities")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/tools/overlay/EntityOverlayPanel.css";
		
		String title();
		
		String relationshipText();
		
		String outerPanel();
				
		String formPanel();
		
		String overlayButton();
		
		String filterItemPanel();
		
		String filterItemLabelButton();
		
		String filterItemRemoveButton();
		
		String existingFilterPanel();
		
		String infoLabel();
	}
}
