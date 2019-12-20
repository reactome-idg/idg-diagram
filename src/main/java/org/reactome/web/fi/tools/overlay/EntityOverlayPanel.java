package org.reactome.web.fi.tools.overlay;


import org.reactome.web.fi.tools.overlay.pairwise.PairwisePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author brunsont
 *
 */
public class EntityOverlayPanel extends FlowPanel {
	
	private EventBus eventBus;
	
	private ScrollPanel relationshipsPanel;
	private Button overlayButton;
	
	public EntityOverlayPanel(EventBus eventBus) {
		this.eventBus = eventBus;
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
		
		relationshipsPanel = new ScrollPanel();
		relationshipsPanel.setStyleName(RESOURCES.getCSS().relationshipsPanel());
		getRelationshipTypePanels();
		outerPanel.add(relationshipsPanel);
		
		FlowPanel bottomContainer = new FlowPanel();
		bottomContainer.add(overlayButton = new Button("Overlay!"));
		overlayButton.addClickHandler(e -> overlayButtonClicked());
		
		outerPanel.add(bottomContainer);
		
		this.add(outerPanel);
	}
	
	private void getRelationshipTypePanels() {
		FlowPanel relationshipsFlowPanel = new FlowPanel();
		relationshipsFlowPanel.add(new PairwisePanel("Gene Coexpression"));
		relationshipsFlowPanel.add(new PairwisePanel("Protein Interactions"));
		relationshipsPanel.add(relationshipsFlowPanel);
	}

	/**
	 * Handles agregation of data needed for overlay server call
	 */
	private void overlayButtonClicked() {
		// TODO Auto-generated method stub
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
		
	}
	
	@CssResource.ImportedWithPrefix("idg-overlayEntities")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/tools/overlay/EntityOverlayPanel.css";
		
		String title();
		
		String relationshipText();
		
		String outerPanel();
		
		String relationshipsPanel();
	}
}
