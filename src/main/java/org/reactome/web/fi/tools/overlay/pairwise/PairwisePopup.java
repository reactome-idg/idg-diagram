package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphProteinDrug;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.common.IDGPager;
import org.reactome.web.fi.common.IDGPager.Handler;
import org.reactome.web.fi.common.RemoveButtonPopup;
import org.reactome.web.fi.data.loader.OverlayLoader;
import org.reactome.web.fi.data.loader.PairwiseDataLoader;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.overlay.profiles.OverlayColours;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwisePopupFactory;
import org.reactome.web.fi.tools.overlay.pairwise.results.PairwisePopupResultsTable;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.MouseUpEvent;
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
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.ListDataProvider;

public class PairwisePopup extends AbstractPairwisePopup implements Handler{

	private String popupId;
	private String containerId;
	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private Map<String,String> uniprotToGeneMap;
	private Set<String> displayedNodes;
	private Set<String> diagramNodes;
	
	private Map<String,Set<String>> edgeMap; //map pairwise interactor node to edge
	private int edgeCount = 0;
	
	private DataOverlay dataOverlay;
	private DataOverlay tableDataOverlay;
	
	private List<PairwiseTableEntity> tableEntities;
	private PairwisePopupResultsTable table;
	private ListDataProvider<PairwiseTableEntity> provider;
	private IDGPager pager;
	
	private CytoscapeEntity cy;
	private Boolean cytoscapeInitialized = false;
	
	private FlowPanel main;
	private FlowPanel infoPanel;
	private FlowPanel customPopup;
	
	public PairwisePopup(GraphObject graphObject, List<PairwiseOverlayObject> pairwiseOverlayObjects, int zIndex) {
		this(graphObject.getStId(), zIndex, pairwiseOverlayObjects);
		this.zIndex = zIndex;
		setDiagramNodes(graphObject);
		initBaseCytoscape();
		loadPairwiseInteractors();
	}

	public PairwisePopup(String uniprot, String geneName, List<PairwiseOverlayObject> pairwiseOverlayObjects, int zIndex) {
		this(uniprot, zIndex, pairwiseOverlayObjects);
		setDiagramNodes(uniprot, geneName);
		initBaseCytoscape();
		loadPairwiseInteractors();
	}
	
	private PairwisePopup(String popupId, int zIndex, List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.popupId = popupId;
		this.zIndex = zIndex;
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		this.displayedNodes = new HashSet<>();
		this.edgeMap = new HashMap<>();
		initPanel();
		panelClicked();
		this.uniprotToGeneMap = PairwisePopupFactory.get().getUniprotToGeneMap();
		
		//Initialize Cytoscape js with no nodes or edges
		initializeCytoscape(new JSONArray(), new JSONArray());
	}

	private void initPanel() {
		FocusPanel focus = new FocusPanel();
		setStyleName(RESOURCES.getCSS().popupPanel());
		setAutoHideEnabled(false);
		setModal(false);
		
		main = new FlowPanel();
		
		main.add(new PwpButton("Close", RESOURCES.getCSS().close(), e -> hide()));

		main.add(getMainPanel());
		
		focus.add(main);
		focus.addClickHandler(e -> panelClicked());
		setTitlePanel();
		setWidget(focus);
				
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
		
		infoPanel = new FlowPanel();
		result.add(infoPanel);
		
		result.add(customPopup = new FlowPanel());
		customPopup.getElement().getStyle().setPosition(Position.RELATIVE);
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
					if(identifier.contains("-"))													//removes any isoform identifiers
						identifier = identifier.substring(0, identifier.indexOf("-"));
					else if(identifier.contains("ENSG")) { 											//convert ENSG to uniprot
						for(Map.Entry<String,String> entry: uniprotToGeneMap.entrySet()) {			//Iterate over map. Check value vs. display name
							if(entity.getDisplayName().contains(entry.getValue())) {				//If equal, replace with key (uniprot)
								identifier = entry.getKey();
								break;
							}
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
	private void setDiagramNodes(String uniprot, String geneName) {
		this.diagramNodes = new HashSet<>();
		diagramNodes.add(uniprot);
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
	 * Use pairwiseOverlayMap to make PairwiseResultsTable and set on infoPanel
	 * !!! MUST BE CALLED AFTER uniprotToGeneMap is loaded
	 */
	private void setPairwiseResultsTable() {
		infoPanel.clear();

		provider = new ListDataProvider<>();
		pager = new IDGPager(this);
		
		table = new PairwisePopupResultsTable(tableEntities, provider,pager);
		
		//Add view button column
		ActionCell<PairwiseTableEntity> actionCell = new ActionCell<>("View", new ActionCell.Delegate<PairwiseTableEntity>() {

			@Override
			public void execute(PairwiseTableEntity object) {
				addInteractions(object);
			}
		});
		
		IdentityColumn<PairwiseTableEntity> viewColumn = new IdentityColumn<>(actionCell);
		table.addColumn(viewColumn,"View Relationship");
				
		infoPanel.add(table);
		infoPanel.add(pager);
		infoPanel.setVisible(true);
	}
	
	/**
	 * Catches event when pager change pages
	 */
	@Override
	public void onPageChanged() {
		int pageStart = pager.getPageStart();
		int pageEnd = pageStart + PairwisePopupResultsTable.PAGE_SIZE;
		List<String> entities = new ArrayList<>();
		for(int i = pageStart; i<pageEnd; i++)
			entities.add(tableEntities.get(i).getInteractorId());
		loadTableOverlayData(entities);
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
	 * Adds nodes from diagram or fiview to the cytoscape display. 
	 * Connects all nodes by an edge.
	 */
	private void initBaseCytoscape() {
		List<String> diagramNodesList = new ArrayList<>(diagramNodes);

		//triggers when popup loads from FIView
		if(diagramNodes.size() == 1) {
			addNode(diagramNodesList.get(0), false);
			return;
		}
		
		for(String node : diagramNodes) {
			addNode(node,false);
		}
		for(int i=0; i<diagramNodesList.size(); i++) {
			for(int j=i+1; j<diagramNodes.size(); j++) {
				addEdge(diagramNodesList.get(i), diagramNodesList.get(j), "solid", "");
			}
		}
	}
	
	/**
	 * performs loading of pairwise tableEntities based on pairwiseOverlayProperties.
	 * @param pairwiseOverlayProperties
	 */
	private void loadPairwiseInteractors() {
		PairwiseDataLoader loader = new PairwiseDataLoader();
		PairwiseOverlayProperties props = new PairwiseOverlayProperties(pairwiseOverlayObjects, String.join(",", diagramNodes));
		loader.loadPairwiseData(props, new AsyncCallback<List<PairwiseTableEntity>>() {
			@Override
			public void onSuccess(List<PairwiseTableEntity> uniprotToPairwiseEntityMap) {
				tableEntities = uniprotToPairwiseEntityMap;
				setPairwiseResultsTable();
				addInitialInteractors(); //can add initial interactors only after uniprotToGeneMap and pairwiseOverlayMap are set.
			}
			@Override
			public void onFailure(Throwable caught) {
				Console.error(caught.getMessage());
			}
		});
	}

	/**
	 * Iterates over pairwiseOverlayMap of tableEntities.
	 * Directs the overlay of positive and negative interactions.
	 */
	private void addInitialInteractors() {
		Set<String> darkProteins = PairwisePopupFactory.get().getTDarkSet();
		for(String diagramNode : diagramNodes) {
			int counter  = 0;
			for(PairwiseTableEntity entity: tableEntities) {
				if(counter == 10) break;
				if(entity.getSourceId() == diagramNode && darkProteins.contains(entity.getInteractorId())) {
					addNode(entity.getInteractorId(), true);
					addEdge(diagramNode, entity.getInteractorId(), entity.getPosOrNeg(), entity.getDataDesc());
					counter++;
				}
			}
		}
		cy.setCytoscapeLayout("cose");
		
		loadOverlay();
	}

	/**
	 * Directs addition of interactor node and all edges present in tableEntities
	 * @param entity
	 */
	private void addInteractions(PairwiseTableEntity entity) {
		addNode(entity.getInteractorId(), true);
		for(PairwiseTableEntity rel : tableEntities)
			if(entity.getInteractorId() == rel.getInteractorId())
				addEdge(rel.getSourceId(),rel.getInteractorId(),rel.getPosOrNeg(),rel.getDataDesc());
		loadOverlay();
	}
	
	/**
	 * Add a node based on just a gene name
	 * @param uniprot
	 */
	private void addNode(String uniprot, boolean interactor) {
		
		if(displayedNodes.contains(uniprot)) return;
		JSONValue val = getProtein(uniprot, uniprotToGeneMap.get(uniprot), interactor);
		displayedNodes.add(uniprot);
		cy.addCytoscapeNodes(val.toString());
	}

	/**
	 * Directs creation of an edge based on passed in source and target
	 * Uses relationship and dataDesc to change style and color of the line.
	 * @param source is the uniprot of the base diagram protein
	 * @param target is the uniprot of the pariwise interactor
	 * @param relationship
	 * @param dataDesc
	 */
	private void addEdge(String source, String target, String relationship, String dataDesc) {
		String edge = source+target+relationship+dataDesc;
		
		if(edgeMap.keySet().contains(target) && edgeMap.get(target).contains(edge)) return;
		
		JSONValue val = makeFI(edgeCount, source, target, relationship);
		
		if(!edgeMap.keySet().contains(target))
			edgeMap.put(target, new HashSet<>());
		edgeMap.get(target).add(edge);
		
		cy.addCytoscapeEdge(val.toString());
		for(PairwiseOverlayObject prop:  pairwiseOverlayObjects) {
			if(prop.getId() == dataDesc && relationship == "positive")
				cy.recolorEdge(edgeCount+"", prop.getPositiveLineColorHex());
			else if(prop.getId() == dataDesc && relationship == "negative")
				cy.recolorEdge(edgeCount+"", prop.getNegativeLineColorHex());
		}
		edgeCount++;
	}
	
	@Override
	protected void endDragging(MouseUpEvent event) {
		cy.resize(); //alerts cytoscape js core to location change of canvas to listen on
		super.endDragging(event);
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
		node.put("color", new JSONString("#FF0000"));
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
	
	/**
	 * Loads DataOverlay from TCRD and directs recoloration of nodes
	 * Causes reload of table page
	 */
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
			public void onDataOverlayLoaded(DataOverlay data) {
				dataOverlay = data;
				overlayData();
				onPageChanged(); //causes load for table
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
	
	/**
	 * Handles overlay loading for pairwise table
	 * @param entities
	 */
	private void loadTableOverlayData(List<String> entities) {
		DataOverlayProperties properties = PairwisePopupFactory.get().getDataOverlayProperties();
		OverlayLoader loader = new OverlayLoader();
		properties.setUniprots(String.join(",", entities));
		loader.load(properties, new OverlayLoader.Handler() {	
			@Override
			public void onOverlayLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
			@Override
			public void onDataOverlayLoaded(DataOverlay data) {
				tableDataOverlay = data;
				updateTableData();
			}
		});
	}
	
	/**
	 * Updates overlay values on results table
	 */
	private void updateTableData() {
		this.tableDataOverlay.updateIdentifierValueMap();
		if(dataOverlay.isDiscrete()) {
			for(PairwiseTableEntity entity : tableEntities) {
				entity.setData("");
				if(tableDataOverlay.getIdentifierValueMap().keySet().contains(entity.getInteractorId()))
					entity.setData(tableDataOverlay.getLegendTypes().get(tableDataOverlay.getIdentifierValueMap().get(entity.getInteractorId()).intValue()));
			}
		}
		else {
			for(PairwiseTableEntity entity : tableEntities) {
				entity.setData("");
				if(tableDataOverlay.getIdentifierValueMap().keySet().contains(entity.getInteractorId()))
					entity.setData(""+tableDataOverlay.getIdentifierValueMap().get(entity.getInteractorId()));
			}
		}
		provider.refresh();
	}

	/**
	 * directs coloration nodes based on current overlay
	 */
	private void overlayData() {
		cy.resetNodeColor();
		cy.resetSelection();
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
			cy.highlightNode(uniprot, color);
		}
	}

	private void overlayDiscreteData() {
		Map<Double, String> colourMap = OverlayColours.get().getColours();
		for(String uniprot : displayedNodes) {
			String color = colourMap.get(dataOverlay.getIdentifierValueMap().get(uniprot));
			if(color == null || color == "") continue;
			cy.highlightNode(uniprot, color);
		}
	}
	
	/**
	 * Opens remove context button on context click
	 */
	@Override
	public void onNodeContextSelectEvent(String id, String name, int x, int y) {
		if(diagramNodes.contains(id)) return;
		int index;
		if(focused == true)
			index = PairwisePopupFactory.get().getMaxZIndex() + 1;
		else
			index = zIndex + 1;
		RemoveButtonPopup panel = new RemoveButtonPopup(index,id, new RemoveButtonPopup.Handler() {
			@Override
			public void onRemoveButtonClicked(String identifier) {
				PairwisePopup.this.cy.removeCytoscapeNode(identifier);
				displayedNodes.remove(identifier);
				edgeMap.remove(identifier);
			}
		});
		
		panel.setPopupPosition(x+5, y+5);
		panel.getElement().setId(this.containerId);
		panel.show();
	}
	
	/**
	 * Updates popup when pairwise overlay options change
	 * @param pairwiseOverlayObjects
	 */
	public void updatePairwiseObjects(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		cy.clearCytoscapeGraph();
		displayedNodes.clear();
		initBaseCytoscape();
		loadPairwiseInteractors();
	}
	
	/**
	 * Directs recoloration of nodes and table values when overlay column changes
	 * @param column
	 */
	public void changeOverlayColumn(int column) {
		//updates column represented in cytoscape
		if(dataOverlay != null)
			dataOverlay.setColumn(column);
		overlayData();
		
		//updates column represented in table 'Overlay value' column
		if(tableDataOverlay != null)
			tableDataOverlay.setColumn(column);
		updateTableData();
	}
	
	/**
	 * Used to focus panel in front of other open popups on panel click
	 */
	private void panelClicked() {
		PairwisePopupFactory.get().resetZIndexes();
		this.getElement().getStyle().setZIndex(PairwisePopupFactory.get().getMaxZIndex());
		focused = true; //set so context menu's have correct z index
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
