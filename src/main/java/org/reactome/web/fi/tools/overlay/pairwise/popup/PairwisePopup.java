package org.reactome.web.fi.tools.overlay.pairwise.popup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphProteinDrug;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.popup.PairwisePopupTablePanel.PairwiseTableHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopup extends DialogBox implements PairwiseTableHandler{

	private String popupId;
	private PairwisePopupCytoscapeController cyController;
	private PairwisePopupTablePanel tablePanel;
	private List<PairwiseOverlayObject> pairwiseOverlayProperties;
	private Set<String> diagramNodes;
	
	private int zIndex;
	private boolean focused = false;
	
	private FlowPanel main;
	
	public PairwisePopup(GraphObject graphObject, List<PairwiseOverlayObject> pairwiseOverlayProperties, int zIndex) {
		setDiagramNodes(graphObject);
		initPanel(graphObject.getStId(), zIndex, pairwiseOverlayProperties);
	}
	
	public PairwisePopup(String uniprot, String geneName, List<PairwiseOverlayObject> pairwiseOverlayProperties, int zIndex) {
		setDiagramNodes(uniprot);
		initPanel(uniprot, zIndex, pairwiseOverlayProperties);
	}
	
	private void initPanel(String popupId, int zIndex, List<PairwiseOverlayObject> pairwiseOverlayProperties) {
		this.zIndex = zIndex;
		this.popupId = popupId;
		this.pairwiseOverlayProperties = pairwiseOverlayProperties;
		initPanel();
		panelClicked();
	}

	private void initPanel() {
		FocusPanel focus = new FocusPanel();
		setStyleName(RESOURCES.getCSS().popupPanel());
		setAutoHideEnabled(false);
		setModal(false);
		
		main = new FlowPanel();
		main.add(new PwpButton("Close", RESOURCES.getCSS().close(), e-> hide()));
		
		main.add(getMainPanel());
		
		focus.add(main);
		focus.addClickHandler(e -> panelClicked());
		setTitlePanel();
		setWidget(focus);
		
		int popupNumber = PairwiseOverlayFactory.get().getNumberOfPopups();
		this.setPopupPosition(popupNumber*20, popupNumber*20);
		
		show();
		
		//create PairwisePopupCytoscapeController after panel creation 
		//otherwise, cytoscape.js has no panel to mount to
		cyController = new PairwisePopupCytoscapeController(popupId, diagramNodes, pairwiseOverlayProperties, RESOURCES, zIndex);
		
		//must add Results table after cyController is created
		main.add(tablePanel = new PairwisePopupTablePanel(pairwiseOverlayProperties, diagramNodes, RESOURCES, this));
	}
	
	private void setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		InlineLabel title = new InlineLabel("Pairwise popup: " + popupId);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
	}

	private void panelClicked() {
		PairwiseOverlayFactory.get().resetZIndexes();
		this.getElement().getStyle().setZIndex(PairwiseOverlayFactory.get().getMaxZIndex());
		focused = true;
		cyController.setFocused(focused);
	}
	
	public void resetZIndex() {
		this.getElement().getStyle().setZIndex(zIndex);
		focused = false;
		cyController.setFocused(focused);
	}

	private FlowPanel getMainPanel() {
		FlowPanel result = new FlowPanel();
		
		result.setStyleName(RESOURCES.getCSS().container());
		
		//Panel for cytoscape to interact with
		SimplePanel cyPanel = new SimplePanel();
		String containerId = "cy-" + popupId;
		cyPanel.getElement().setId(containerId);
		cyPanel.setStyleName(RESOURCES.getCSS().cyView());
		result.add(cyPanel);

		return result;
	}
	
	/**
	 * Sets diagram nodes when popup initiated from diagram view.
	 * Converts any isoforms or ENSG to standard Uniprot identifier.
	 * @param graphObject
	 */
	private void setDiagramNodes(GraphObject graphObject) {
		this.diagramNodes = new HashSet<>();
		if(graphObject instanceof GraphPhysicalEntity) {
			Set<GraphPhysicalEntity> entities = ((GraphPhysicalEntity)graphObject).getParticipants();
			for(GraphPhysicalEntity entity : entities) {
				if(entity instanceof GraphEntityWithAccessionedSequence || entity instanceof GraphProteinDrug) {
					String identifier = entity.getIdentifier();
					if(identifier == null) continue;
					if(identifier.contains("-"))													//removes any isoform identifiers
						identifier = identifier.substring(0, identifier.indexOf("-"));
					else if(identifier.contains("ENSG") || identifier.contains("ENST")) { 											//convert ENSG to uniprot
						if(identifier.contains("ENSG")) {
							identifier = PairwiseInfoService.getGeneToUniprotMap().get(entity.getDisplayName().substring(0, entity.getDisplayName().indexOf(" ")));
						}
					}
					diagramNodes.add(identifier);
				}
			}
		}
	}
	
	/**
	 * Sets diagram nodes when popup initiated from FI view
	 * @param uniprot
	 * @param geneName
	 */
	private void setDiagramNodes(String uniprot) {
		this.diagramNodes = new HashSet<>();
		diagramNodes.add(uniprot);
	}

	@Override
	public void hide() {
		PairwiseOverlayFactory.get().removePopup(this.popupId);
		super.hide();
	}

	@Override
	public void addInteractions(Set<PairwiseTableEntity> entities) {
		cyController.addInteractions(entities);
	}
	
	public void loadOverlay() {
		cyController.loadOverlay();
		tablePanel.loadOverlay();
	}
	
	public void changeOverlayColumn(int column) {
		cyController.updateOverlayColumn(column);
		tablePanel.updateOverlayColumn(column);
	}

	@Override
	protected void endDragging(MouseUpEvent event) {
		cyController.resize();
		super.endDragging(event);
	}

	public void updatePairwiseObjects(List<PairwiseOverlayObject> currentPairwiseObjects) {
		cyController.pairwisePropertiesChanged();
		tablePanel.pairwisePropertiesChanged();
	}

	/**
	 * Below here is all for styling. Handles styles of PairwisePopup,
	 * PairwisePopupTablePanel, and PairwisePopupCytoscapeController
	 */
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("org/reactome/web/fi/client/visualisers/fiview/cytoscape-style.json")
		public TextResource fiviewStyle();
		
		@Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
	}
	
	@CssResource.ImportedWithPrefix("idg-pairwisePopup")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/popup/PairwisePopup.css";
		
		String popupPanel();
				
		String cyView();
		
		String header();
		
		String unselectable();
		
		String close();
		
		String innerContainer();
		
		String overlayInfo();
		
		String filter();
		
		String controls();
		
		String smallButton();
		
		String controlLabel();
		
		String infoButton();
		
		String container();
		
		String filterTextBox();
		
		String pager();
		
		String filterPanel();
		
		String table();
		
		String eTypeAndTissueLabel();
		
		String smallText();
		
		String exportButton();
		
		String pagerPanel();
		
	}
}