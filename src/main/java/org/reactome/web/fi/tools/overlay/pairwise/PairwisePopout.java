package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.events.PairwiseOverlayButtonClickedEvent;
import org.reactome.web.diagram.handlers.PairwiseOverlayButtonClickedHandler;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PairwisePopout extends PopupPanel implements ResizeHandler, CytoscapeEntity.Handler{

	private CytoscapeEntity cy;
	private SimplePanel cyView;
	private boolean initialized = false;
	
	private JSONArray currentNodeArray;
	private JSONArray currentEdgeArray;
	private JSONArray baseNodeArray;
	private JSONArray baseEdgeArray;
	
	private List<PairwiseEntity> currentPairwiseOverlay;
	private List<PairwiseOverlayObject> pairwiseOverlayProperties;
	private List<String> diagramGeneNames;
	private List<String> displayedNodes;

	
	public PairwisePopout(EventBus eventBus) {
		currentPairwiseOverlay = new ArrayList<>();
		diagramGeneNames = new ArrayList<>();
		this.setStyleName(RESOURCES.getCSS().popupPanel());
		
		this.cy = new CytoscapeEntity(RESOURCES.fiviewStyle().getText(), this);
		initPanel();
		
	}

	private void initPanel() {
		this.setAutoHideEnabled(true);
		this.setModal(true);
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setAutoHideOnHistoryEventsEnabled(false);
		Window.addResizeHandler(this);
		
		int width = (int) Math.round(Window.getClientWidth() * 0.5);
		int height = (int) Math.round(Window.getClientHeight() * 0.5);
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		
		FlowPanel container = new FlowPanel();
		container.add(setTitlePanel());
		
		FlowPanel innerContainer = new FlowPanel();
		innerContainer.setStyleName(RESOURCES.getCSS().innerContainer());
		
		cyView = new SimplePanel();
		cyView.getElement().setId("cy-popout");
		cyView.setStyleName(RESOURCES.getCSS().cyView());
		innerContainer.add(cyView);
				
		FlowPanel overlayInfo = new FlowPanel();
		overlayInfo.setStyleName(RESOURCES.getCSS().overlayInfo());
		innerContainer.add(overlayInfo);
		overlayInfo.add(getOverlayInfo());
		
		container.add(innerContainer);
		this.add(container);
	}
	
	/**
	 * Gets the right hand side of the popout 
	 * @return
	 */
	private FlowPanel getOverlayInfo() {
		FlowPanel result = new FlowPanel();
		
		FlowPanel filter = new FlowPanel();
		filter.setStyleName(RESOURCES.getCSS().filter());
		result.add(filter);
		
		FlowPanel controls = getControls();
		controls.setStyleName(RESOURCES.getCSS().controls());
		result.add(controls);
		
		return result;
	}

	private FlowPanel getControls() {
		FlowPanel result = new FlowPanel();
		
		InlineLabel clearLabel = new InlineLabel("Clear all relationships");
		clearLabel.setStyleName(RESOURCES.getCSS().controlLabel());
		Button clearButton = new Button("Clear");
		clearButton.setStyleName(RESOURCES.getCSS().smallButton());
		clearButton.addClickHandler(e -> onClearButtonClicked());
		result.add(clearLabel);
		result.add(clearButton);
		
		return result;
	}

	private void onClearButtonClicked() {
		cy.clearCytoscapeGraph();
		this.currentNodeArray = new JSONArray();
		this.currentEdgeArray = new JSONArray();
		cy.addCytoscapeNodes(baseNodeArray.toString());
		cy.addCytoscapeEdge(baseEdgeArray.toString());
		cy.setCytoscapeLayout("cose");
		
	}

	private FlowPanel setTitlePanel() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().header());
		result.addStyleName(RESOURCES.getCSS().unselectable());
		InlineLabel title = new InlineLabel("Pairwise Relationship");
		result.add(title);
		result.add(getCloseButton());
		return result;
	}

	private Button getCloseButton() {
		Button result = new Button();
		result.setStyleName(RESOURCES.getCSS().close());
		result.setTitle("Close Pairwise View");
		result.addClickHandler(e -> super.hide());
		return result;
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
	
	//keeping around for use in new iteration of popup
//	@Override
//	public void onPairwiseOverlayButtonClicked(PairwiseOverlayButtonClickedEvent event) {
//		if(currentPairwiseOverlay.size()==0) {
//			return;
//		}
//		
//		//resets values for when new popup is opened
//		currentNodeArray = new JSONArray();
//		currentEdgeArray = new JSONArray();
//		baseNodeArray = new JSONArray();
//		baseEdgeArray = new JSONArray();
//		diagramGeneNames = new ArrayList<>();
//		displayedNodes = new ArrayList<>();
//		
//		if(event.getGraphObject() != null) { //used when popup opened from diagram view
//			constructBaseFIs(event.getGraphObject());
//		}
//		else if(event.getGeneName() != null) { //used when popup opened from FIView
//			constructBaseFIs(event.getGeneName());
//		}
//		
//		updateView();
//		
//		this.show();
//	}

	/**
	 * directs updating when panel opens
	 */
	private void updateView() {
		if(!initialized) {
			cy.cytoscapeInit(baseNodeArray.toString(),
							 baseEdgeArray.toString(),
							 "cose",
							 "cy-popout");
			initialized = true;
		}
		else {
			cy.clearCytoscapeGraph();
			cy.addCytoscapeNodes(baseNodeArray.toString());
			cy.addCytoscapeEdge(baseEdgeArray.toString());
		}
		addInitialPairwiseRelationships();
		cy.setCytoscapeLayout("cose");
	}

	/**
	 * adds initial node to graph for popup opened from FIView
	 * @param uniprot
	 */
	private void constructBaseFIs(String geneName) {
		JSONArray nodeArr = new JSONArray();
		nodeArr.set(nodeArr.size(), getProtein(geneName, false));
		baseNodeArray = nodeArr;
		this.currentNodeArray = nodeArr;
		addDiagramGeneName(geneName);
	}

	/**
	 * Adds nodes and edges for complex or protein opened from diagram.
	 * Does not add any pairwise relationship data
	 * @param graphObject
	 */
	private void constructBaseFIs(GraphObject graphObject) {
		JSONArray nodeArr = new JSONArray();
		JSONArray edgeArr = new JSONArray();
		if(graphObject instanceof GraphComplex) {
			GraphComplex complex = (GraphComplex) graphObject;
			List<GraphPhysicalEntity> entities = new ArrayList<>();
			entities.addAll(complex.getParticipants());
			for(int i=0; i<entities.size(); i++) {
				nodeArr.set(nodeArr.size(), getProtein(entities.get(i)));
				addDiagramGeneName(entities.get(i).getDisplayName());
				for(int j=i+1; j<entities.size(); j++) {
					edgeArr.set(edgeArr.size(), makeFI(edgeArr.size(), entities.get(i).getDisplayName(), entities.get(j).getDisplayName(), "solid"));
				}
			}
		}
		baseNodeArray = nodeArr;
		baseEdgeArray = edgeArr;
	}
	
	/**
	 * Directs addition of first 10 positive and negative relationships for each gene present in base diagram object
	 * Adds 10 relationships based in alphabetical order
	 */
	private void addInitialPairwiseRelationships() {
		
		for(PairwiseEntity entity: currentPairwiseOverlay) {
			if(!diagramGeneNames.contains(entity.getGene())) continue;
			if(entity.getPosGenes() != null) {
				Collections.sort(entity.getPosGenes());
				for(int i=0; i<10; i++) {
					if(entity.getPosGenes().get(i) == null) break;
					addNode(entity.getPosGenes().get(i));
					addEdge(entity.getGene(), entity.getPosGenes().get(i), "positive", entity.getDataDesc().getId());
				}
			}
			if(entity.getNegGenes() != null) {
				Collections.sort(entity.getNegGenes());
				for(int i=0; i<10; i++) {
					if(entity.getNegGenes().get(i) == null) break;
					addNode(entity.getNegGenes().get(i));
					addEdge(entity.getGene(), entity.getNegGenes().get(i), "negative", entity.getDataDesc().getId());
				}
			}
		}
	}

	/**
	 * Add a node based on just a gene name
	 * @param gene
	 */
	private void addNode(String gene) {
		if(displayedNodes.contains(gene)) return;
		JSONValue val = getProtein(gene, true);
		currentNodeArray.set(currentNodeArray.size() + baseNodeArray.size(), val);
		displayedNodes.add(gene);
		cy.addCytoscapeNodes(val.toString());
	}
	
	/**
	 * Directs creation of an edge based on passed in source and target
	 * Uses relationship and dataDesc to change style and color of the line.
	 * @param source
	 * @param target
	 * @param relationship
	 * @param dataDesc
	 */
	private void addEdge(String source, String target, String relationship, String dataDesc) {
		int edgeId = currentEdgeArray.size() + baseEdgeArray.size();
		JSONValue val = makeFI(edgeId, source, target, relationship);
		currentEdgeArray.set(currentEdgeArray.size() + baseEdgeArray.size(), val);
		cy.addCytoscapeEdge(val.toString());
		for(PairwiseOverlayObject prop:  pairwiseOverlayProperties) {
			if(prop.getId() == dataDesc && relationship == "positive")
				cy.recolorEdge(edgeId+"", prop.getPositiveLineColorHex());
			else if(prop.getId() == dataDesc & relationship == "negative")
				cy.recolorEdge(edgeId+"", prop.getNegativeLineColorHex());
		}
	}
	
	/**
	 * Adds passed in gene name to set of gene names from diagram.
	 * Removes any identifiers to the actual gene name
	 * @param displayName
	 */
	private void addDiagramGeneName(String displayName) {
		int index  = displayName.indexOf(" ");
		if(index > 0)
			displayName = displayName.substring(0, index);
		diagramGeneNames.add(displayName);
		displayedNodes.add(displayName);
	}

	/**
	 * Makes a FI edge bassed on a passed in id, target and source
	 * @param id
	 * @param source
	 * @param target
	 * @return
	 */
	private JSONValue makeFI(int id, String source, String target, String relationship) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("edges"));
		
		int index = source.indexOf(" ");
		if(index>0)
			source = source.substring(0, index);
		
		index = target.indexOf(" ");
		if(index>0)
			target = target.substring(0, index);
		
		JSONObject edge = new JSONObject();
		edge.put("id", new JSONString(id+""));
		edge.put("source", new JSONString(source));
		edge.put("target", new JSONString(target));
		edge.put("direction", new JSONString("-"));
		edge.put("lineStyle", new JSONString(relationship));
		
		result.put("data", edge);
		return result;
	}

	/**
	 * Makes a node for a passed in GraphPhysicalEntity
	 * @param entity
	 * @return
	 */
	private JSONValue getProtein(GraphPhysicalEntity entity) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		
		
		String gene = entity.getDisplayName();
		int index = gene.indexOf(" ");
		if(index > 0)
			gene = gene.substring(0, index);
		node.put("id", new JSONString(gene));
		node.put("name", new JSONString(gene));
		
		result.put("data", node);
		
		return result;
	}
	
	/**
	 * Makes a node for only a passed in gene name string
	 * @param gene
	 * @return
	 */
	private JSONValue getProtein(String gene, boolean interactor) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		node.put("id", new JSONString(gene));
		node.put("name", new JSONString(gene));
		if(interactor == true)
			node.put("interactor", new JSONString("true"));
		else
			node.put("interactor", new JSONString("false"));
		
		result.put("data", node);
		return result;
	}
	
	//keeping for reference with new popup 
//	@Override
//	public void onPairwisieDataLoaded(PairwiseDataLoadedEvent event) {
//		currentPairwiseOverlay = event.getEntities();
//		this.pairwiseOverlayProperties = event.getPairwiseOverlayObjects();
//	}
	
	@Override
	public void onNodeClicked(String id, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeClicked(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeHovered(String id, String name, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeHovered(String id, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeMouseOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeMouseOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCytoscapeCoreContextEvent(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCytoscapeCoreSelectedEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEdgeContextSelectEvent(String id, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeContextSelectEvent(String id, String name, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("org/reactome/web/fi/tools/overlay/pairwise/cytoscape-style.json")
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
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/PairwisePopout.css";
		
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
	}
}
