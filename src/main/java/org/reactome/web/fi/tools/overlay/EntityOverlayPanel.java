package org.reactome.web.fi.tools.overlay;


import java.util.HashMap;
import java.util.Map;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseFormPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author brunsont
 *
 */
public class EntityOverlayPanel extends FlowPanel implements PairwiseFormPanel.Handler{
	
	private EventBus eventBus;
	
	private ScrollPanel relationshipsPanel;
	private Button overlayButton;
	private FlowPanel existingFilterPanel;
	
	private Map<String, PairwiseOverlayObject> selectedFilters;
	
	private Image loader;
	
	public EntityOverlayPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		selectedFilters = new HashMap<>();
		this.getElement().getStyle().setMargin(5, Unit.PX);
		
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
		
		outerPanel.add(new PairwiseFormPanel(this)); //so panel can pass back additions and removals from form
		
		existingFilterPanel = new FlowPanel();
		existingFilterPanel.setStyleName(RESOURCES.getCSS().existingFilterPanel());
		
		FlowPanel bottomContainer = new FlowPanel();
		bottomContainer.add(existingFilterPanel);
		bottomContainer.add(overlayButton = new Button("Overlay!"));
		overlayButton.setStyleName(RESOURCES.getCSS().overlayButton());
		overlayButton.addClickHandler(e -> overlayButtonClicked());
		bottomContainer.add(loader = new Image(RESOURCES.loader()));
		loader.setStyleName(RESOURCES.getCSS().tissuesLoading());
		loader.setVisible(true);
		
		outerPanel.add(bottomContainer);
		
		this.add(outerPanel);
	}

	@Override
	public void onAddClicked(PairwiseOverlayObject obj) {
		selectedFilters.put(obj.getId(), obj);
		updateExistingFilterPanel();
	}
	
	private void updateExistingFilterPanel() {
		existingFilterPanel.clear();
		selectedFilters.keySet().forEach(k -> {
			FlowPanel filterPanel = new FlowPanel();
			filterPanel.setStyleName(RESOURCES.getCSS().filterItemPanel());
			Button labelButton = new Button(k);
			labelButton.setStyleName(RESOURCES.getCSS().filterItemLabelButton());
			filterPanel.add(labelButton);
			Button removeButton = new Button("X");
			removeButton.setStyleName(RESOURCES.getCSS().filterItemRemoveButton());
			filterPanel.add(removeButton);
			existingFilterPanel.add(filterPanel);
		});
	}

	/**
	 * Handles agregation of data needed for overlay server call
	 */
	private void overlayButtonClicked() {
		loader.setVisible(true);
	}

	/**
	 * Callable method to hide loading pinwheel
	 */
	public void hideLoader() {
		loader.setVisible(false);
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
				
		String tissuesLoading();
		
		String formPanel();
		
		String overlayButton();
		
		String filterItemPanel();
		
		String filterItemLabelButton();
		
		String filterItemRemoveButton();
		
		String existingFilterPanel();
	}
}
