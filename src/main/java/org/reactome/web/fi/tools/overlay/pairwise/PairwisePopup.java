package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.data.loader.OverlayLoader;
import org.reactome.web.fi.data.loader.PairwiseDataLoader;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.overlay.profiles.OverlayColours;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwisePopupFactory;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PairwisePopup extends AbstractPairwisePopup{

	private String popupId;
	private String containerId;
	private int edgeCount = 0;
	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private Map<String, List<PairwiseEntity>> pairwiseOverlayMap;
	private Map<String,String> uniprotToGeneMap;
	private List<String> displayedNodes;
	
	private DataOverlay dataOverlay;
	
	private CytoscapeEntity cy;
	private Boolean cytoscapeInitialized = false;
	
	private FlowPanel main;
	
	private FlowPanel infoPanel;
	
	public PairwisePopup(GraphObject graphObject, List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.popupId = graphObject.getStId();
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		this.displayedNodes = new ArrayList<>();
		initPanel();
		loadNetwork(graphObject);
	}

	public PairwisePopup(String uniprot, String geneName, List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.popupId = uniprot;
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		this.displayedNodes = new ArrayList<>();
		initPanel();
		loadNetwork(uniprot, geneName);
	}

	private void initPanel() {
		setStyleName(RESOURCES.getCSS().popupPanel());
		setAutoHideEnabled(false);
		setModal(false);
		
		main = new FlowPanel();
		
		main.add(new PwpButton("Close", RESOURCES.getCSS().close(), e -> hide()));

		main.add(getMainPanel());
		
		setTitlePanel();
		setWidget(main);
				
		show();
	}
	
	private FlowPanel getMainPanel() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().container());
		
		SimplePanel cyView = new SimplePanel();
		cyView.getElement().setId(containerId = "cy-"+popupId);
		cyView.setStyleName(RESOURCES.getCSS().cyView());
		result.add(cyView);
		
		Button infoButton = new Button("Show/Hide info");
		infoButton.setStyleName(RESOURCES.getCSS().infoButton());
		infoButton.addClickHandler(e -> onInfoButtonClicked());
		result.add(infoButton);
		
		infoPanel = new FlowPanel(); //TODO: Setup info panel
		
		return result;
	}

	private void onInfoButtonClicked() {
		infoPanel.setVisible(!infoPanel.isVisible());
	}

	private void setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		InlineLabel title = new InlineLabel("Pairwise popup: " + popupId);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
	}

	/**
	 * initializes cytoscape with the base nodes and edges passed into the above constructors.
	 * @param nodeArr
	 * @param edgeArr
	 */
	private void initializeCytoscape(JSONArray nodeArr, JSONArray edgeArr) {
		if(!cytoscapeInitialized) {
			this.cy = new CytoscapeEntity(RESOURCES.fiviewStyle().getText(), this);
			cy.cytoscapeInit(nodeArr.toString(),edgeArr.toString(), "cose", containerId);
			cytoscapeInitialized = true;
		}
		cy.setCytoscapeLayout("cose");
	}

	/**
	 * Loads base nodes and edges from Graph object that is of type GraphComplex or GraphPhysicalEntity.
	 * Directs loading of pairwise relationhips for displayed nodes.
	 * @param graphObject
	 */
	private void loadNetwork(GraphObject graphObject) {
		JSONArray nodeArr = new JSONArray();
		JSONArray edgeArr = new JSONArray();
		if(graphObject instanceof GraphPhysicalEntity) {
			GraphPhysicalEntity complex = (GraphPhysicalEntity) graphObject;
			List<GraphPhysicalEntity> entities = new ArrayList<>();
			entities.addAll(complex.getParticipants());
			for(int i=0; i<entities.size(); i++) {
				nodeArr.set(nodeArr.size(), getProtein(entities.get(i)));
				displayedNodes.add(entities.get(i).getIdentifier());
				for(int j=i+1; j<entities.size(); j++) {
					edgeArr.set(edgeCount++, 
								makeFI(edgeArr.size(), 
								entities.get(i).getIdentifier(), 
								entities.get(j).getIdentifier(), 
								"solid"));
				}
			}
		}
		initializeCytoscape(nodeArr, edgeArr); //initializes and adds diagram nodes and edges
		loadPairwiseRelationships(graphObject);
	}

	/**
	 * Loads base node when opened from FIViz or single protein GraphObject.
	 * Directs loading of pairwise entities.
	 * @param uniprot
	 * @param geneName
	 */
	private void loadNetwork(String uniprot, String geneName) {
		JSONArray nodeArr = new JSONArray();
		nodeArr.set(nodeArr.size(), getProtein(uniprot, geneName, false));
		initializeCytoscape(nodeArr, new JSONArray());
		load(new PairwiseOverlayProperties(pairwiseOverlayObjects, uniprot));
	}

	/**
	 * collects uniprots of a graphObject and directs loading of pairwise entities;
	 * @param graphObject
	 */
	private void loadPairwiseRelationships(GraphObject graphObject) {
		List<String> uniprots = new ArrayList<>();
		if(graphObject instanceof GraphPhysicalEntity) {
			Set<GraphPhysicalEntity> entities = ((GraphPhysicalEntity)graphObject).getParticipants();
			for(GraphPhysicalEntity entity: entities)
				uniprots.add(entity.getIdentifier());
		}
		load(new PairwiseOverlayProperties(pairwiseOverlayObjects, String.join(",", uniprots)));
	}
	
	/**
	 * performs loading of pairwise entities based on passed in pairwiseOverlayProperties.
	 * PairwiseOverlayProperties must contain a list of PairwiseOverlaObjects and a list of uniprots as a string.
	 * @param pairwiseOverlayProperties
	 */
	private void load(PairwiseOverlayProperties pairwiseOverlayProperties) {
		PairwiseDataLoader loader = new PairwiseDataLoader();
		loader.loadPairwiseData(pairwiseOverlayProperties, new PairwiseDataLoader.Handler() {
			@Override
			public void onPairwiseDataLoadedError(Exception e) {
				GWT.log(e.toString());
			}
			@Override
			public void onPairwiseDataLoaded(Map<String, List<PairwiseEntity>> uniprotToPairwiseEntityMap) {
				pairwiseOverlayMap = uniprotToPairwiseEntityMap;
				loadUniprotToGeneMap(); //need to load this after loading the overlay items for connecting genes to uniprots for display
			}
		});
	}
	
	/**
	 * Loads Uniprot to gene name map and directs addition of initial interactors after successful loading.
	 */
	private void loadUniprotToGeneMap() {
		PairwiseInfoService.loadUniprotToGeneMap(new AsyncCallback<Map<String,String>>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log(caught.getMessage());
			}
			@Override
			public void onSuccess(Map<String, String> uniprotToGeneNameMap) {
				uniprotToGeneMap = uniprotToGeneNameMap;
				addInitialInteractors(); //can add initial interactors only after uniprotToGeneMap and pairwiseOverlayMap are set.
			}
		});
	}

	/**
	 * Iterates over pairwiseOverlayMap of entities.
	 * Directs the overlay of positive and negative interactions.
	 */
	private void addInitialInteractors() {
		pairwiseOverlayMap.forEach((k,v) -> {
			v.forEach(entity -> {
				if(entity.getPosGenes() != null) 
					addInteractorSet(entity.getGene(), entity.getPosGenes(), "positive", entity.getDataDesc().getId());
				if(entity.getNegGenes() != null) 
					addInteractorSet(entity.getGene(), entity.getNegGenes(), "negative", entity.getDataDesc().getId());
			});
		});
		cy.setCytoscapeLayout("cose");
		
		loadOverlay();
	}
	
	/**
	 * Given a diagram gene source, add a set of uniprotes as connected nodes with either "positive"
	 * or "negative" for interaction. id is used to determine line color in addEdge().
	 * @param source
	 * @param uniprots
	 * @param interaction
	 * @param id
	 */
	private void addInteractorSet(String source, List<String> uniprots, String interaction, String id) {
		for(int i=0; i<5; i++) {
			if(uniprots.get(i) == null) break;
			addNode(uniprots.get(i));
			addEdge(source, uniprots.get(i), interaction, id);
		}
	}
	
	/**
	 * Add a node based on just a gene name
	 * @param uniprot
	 */
	private void addNode(String uniprot) {
		if(displayedNodes.contains(uniprot)) return;
		JSONValue val = getProtein(uniprot, uniprotToGeneMap.get(uniprot), true);
		displayedNodes.add(uniprot);
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
		int edgeId = edgeCount++;
		JSONValue val = makeFI(edgeId, source, target, relationship);
		cy.addCytoscapeEdge(val.toString());
		for(PairwiseOverlayObject prop:  pairwiseOverlayObjects) {
			if(prop.getId() == dataDesc && relationship == "positive")
				cy.recolorEdge(edgeId+"", prop.getPositiveLineColorHex());
			else if(prop.getId() == dataDesc & relationship == "negative")
				cy.recolorEdge(edgeId+"", prop.getNegativeLineColorHex());
		}
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
		
		node.put("id", new JSONString(entity.getIdentifier()));
		node.put("name", new JSONString(entity.getDisplayName()));
		
		result.put("data", node);
		
		return result;
	}
	
	/**
	 * Makes a node for only a passed in gene name string
	 * @param gene
	 * @return
	 */
	private JSONValue getProtein(String uniprot, String gene, boolean interactor) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		node.put("id", new JSONString(uniprot));
		node.put("name", new JSONString(gene));
		if(interactor == true)
			node.put("interactor", new JSONString("true"));
		else
			node.put("interactor", new JSONString("false"));
		
		result.put("data", node);
		return result;
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
		
		JSONObject edge = new JSONObject();
		edge.put("id", new JSONString(id+""));
		edge.put("source", new JSONString(source));
		edge.put("target", new JSONString(target));
		edge.put("direction", new JSONString("-"));
		edge.put("lineStyle", new JSONString(relationship));
		
		result.put("data", edge);
		return result;
	}
	
	public void loadOverlay() {
		DataOverlayProperties properties = PairwisePopupFactory.get().getDataOverlayProperties();
		OverlayLoader loader = new OverlayLoader();
		properties.setUniprots(getAllUniprots());
		
		loader.load(properties, new OverlayLoader.Handler() {
			@Override
			public void onOverlayLoadedError(Throwable exception) {
				Console.log(exception);
			}
			@Override
			public void onDataOverlayLoaded(DataOverlay dataOverlay) {
				overlayData(dataOverlay);
			}
		});
	}
	
	/**
	 * Gather uniprots for dataOverlay service call
	 * @return
	 */
	private String getAllUniprots() {
		Set<String> uniprots = new HashSet<>();
		uniprots.addAll(this.displayedNodes);
		uniprots.remove(null);
		return String.join(",", uniprots);
	}

	private void overlayData(DataOverlay dataOverlay) {
		cy.resetStyle();
		cy.resetSelection();
		this.dataOverlay = dataOverlay;
		this.dataOverlay.updateIdentifierValueMap();
		if(dataOverlay.isDiscrete())
			overlayDiscreteData();
		else
			overlayContinuousData();
	}
	
	private void overlayContinuousData() {
		ThreeColorGradient gradient = AnalysisColours.get().expressionGradient;
		for(String uniprot: displayedNodes) {
			if(dataOverlay.getIdentifierValueMap().get(uniprot) == null) continue;
			String color = gradient.getColor(dataOverlay.getIdentifierValueMap().get(uniprot), dataOverlay.getMinValue(), dataOverlay.getMaxValue());
			cy.highlightNode(uniprot, 
							 color,
							 ".8");
		}
	}

	private void overlayDiscreteData() {
		Map<Double, String> colourMap = OverlayColours.get().getColours();
		for(String uniprot : displayedNodes)
			cy.highlightNode(uniprot, colourMap.get(dataOverlay.getIdentifierValueMap().get(uniprot)), ".8");
	}

	@Override
	public void hide() {
		PairwisePopupFactory.get().removePopup(this.popupId);
		super.hide();
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
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/PairwisePopup.css";
		
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
