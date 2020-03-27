package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
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
import org.reactome.web.fi.client.popups.FILayoutChangerPanel;
import org.reactome.web.fi.client.popups.FIViewInfoPopup;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.common.CommonButton;
import org.reactome.web.fi.common.IDGListBox;
import org.reactome.web.fi.common.IDGPager;
import org.reactome.web.fi.common.IDGPager.Handler;
import org.reactome.web.fi.common.IDGTextBox;
import org.reactome.web.fi.data.loader.OverlayLoader;
import org.reactome.web.fi.data.loader.PairwiseDataLoader;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.FILayoutType;
import org.reactome.web.fi.overlay.profiles.OverlayColours;
import org.reactome.web.fi.tools.export.ExportPairwiseRelationshipsPanel;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.results.PairwisePopupResultsTable;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopup extends AbstractPairwisePopup implements Handler{

	private String popupId;
	private String containerId;
	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private Map<String,String> uniprotToGeneMap;
	
	private Set<String> displayedNodes;
	private Set<String> diagramNodes;
	private int diagramEdgesCount;
	private Set<Integer> displayedEdges;
	
	private DataOverlay dataOverlay;
	private DataOverlay tableDataOverlay;
	
	private List<PairwiseTableEntity> tableEntities;
	private List<PairwiseTableEntity> filteredTableEntities;
	private PairwisePopupResultsTable table;
	private ListDataProvider<PairwiseTableEntity> provider;
	private IDGPager pager;
	private InlineLabel eTypeAndTissue;
	
	private IDGTextBox filterGeneNameBox;
	private IDGListBox sourceListBox;
	private CheckBox showPositive;
	private CheckBox showNegative;
	
	private CytoscapeEntity cy;
	
	private FlowPanel main;
	private FlowPanel infoPanel;
	
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
		this.tableEntities = new ArrayList<>();
		this.displayedEdges = new HashSet<>();
		initPanel();
		panelClicked();
		this.uniprotToGeneMap = PairwiseOverlayFactory.get().getUniprotToGeneMap();
		
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
				
		int popupNumber = PairwiseOverlayFactory.get().getNumberOfPopups();
		this.setPopupPosition(popupNumber*20, popupNumber*20);
		
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
		
		table = new PairwisePopupResultsTable(filteredTableEntities, provider, pager, new PairwisePopupResultsTable.Handler() {
			@Override
			public void onColumnSorted() {
				onPageChanged();
			}
		});
		table.setStyleName(RESOURCES.getCSS().table());
		
		//Add view button column
		ActionCell<PairwiseTableEntity> actionCell = new ActionCell<>("View", new ActionCell.Delegate<PairwiseTableEntity>() {
			@Override
			public void execute(PairwiseTableEntity object) {
				addInteraction(object);
				cy.selectNode(object.getInteractorId());
			}
		});
		
		IdentityColumn<PairwiseTableEntity> viewColumn = new IdentityColumn<>(actionCell);
		table.addColumn(viewColumn,"View Relationship");
				
		FlowPanel pagerPanel = new FlowPanel();
		pagerPanel.getElement().getStyle().setHeight(30, Unit.PX);
		
		pager.setStyleName(RESOURCES.getCSS().pager());
	
		pagerPanel.add(this.eTypeAndTissue = new InlineLabel());
		eTypeAndTissue.setStyleName(RESOURCES.getCSS().smallText());
		eTypeAndTissue.addStyleName(RESOURCES.getCSS().eTypeAndTissueLabel());
		pagerPanel.add(pager);
		
		CommonButton exportButton = new CommonButton("Export Relationships", e -> onExportButtonClicked());
		exportButton.addStyleName(RESOURCES.getCSS().exportButton());
		pagerPanel.add(exportButton);
		exportButton.setVisible(false); //TODO: Find way to export table and re-enable this button
		
		infoPanel.add(getFilterPanel());
		infoPanel.add(table);
		infoPanel.add(pagerPanel);
		infoPanel.setVisible(true);
	
	}

	/**
	 * Open export dialog for Pairwise Relationships
	 */
	private void onExportButtonClicked() {
		ExportPairwiseRelationshipsPanel export = new ExportPairwiseRelationshipsPanel(popupId, tableEntities);
		export.show();
	}

	/**
	 * This panel is for filtering table results;
	 * @return
	 */
	private FlowPanel getFilterPanel() {
		
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(RESOURCES.getCSS().filterPanel());
		
		panel.add(new InlineLabel("Filter table results:"));
		
		filterGeneNameBox = new IDGTextBox();
		filterGeneNameBox.addKeyUpHandler(e -> filterTableEntities());
		filterGeneNameBox.setStyleName(RESOURCES.getCSS().filterTextBox());
		filterGeneNameBox.getElement().setPropertyString("placeholder", "Filter by Gene...");
		panel.add(filterGeneNameBox);
		
		panel.add(getSourceListBox());
		
		showPositive = new CheckBox("pos");
		showPositive.getElement().getStyle().setDisplay(Display.INLINE);
		showPositive.addClickHandler(e -> filterTableEntities());
		showNegative = new CheckBox("neg");
		showNegative.getElement().getStyle().setDisplay(Display.INLINE);
		showNegative.addClickHandler(e -> filterTableEntities());
		panel.add(showPositive);
		panel.add(showNegative);
		
		showPositive.setValue(true, false);
		showNegative.setValue(true, false);
				
		return panel;
	}
	
	/**
	 * Filter list box for filtering by source
	 * @return
	 */
	private IDGListBox getSourceListBox() {
		sourceListBox = new IDGListBox();
		
		List<String> list = new ArrayList<>();
		list.add("Show all sources");
		for(PairwiseOverlayObject obj : pairwiseOverlayObjects) {
			list.add(obj.getId());
		}
		
		sourceListBox.setListItems(list);
		sourceListBox.setMultipleSelect(false);
		sourceListBox.setSelectedIndex(0);
		sourceListBox.addChangeHandler(e -> filterTableEntities());
		
		sourceListBox.getElement().getStyle().setMarginLeft(5, Unit.PX);
		sourceListBox.getElement().getStyle().setWidth(225, Unit.PX);
		
		return sourceListBox;
	}

	/**
	 * Filter tableEntities based on set of filters
	 */
	private void filterTableEntities() {
		List<PairwiseTableEntity> newList = new ArrayList<>();
		
		String filterText = filterGeneNameBox.getText().toUpperCase();
		//sort interactors by data description and interactor name
		for(PairwiseTableEntity entity : tableEntities) {
			if(entity.getInteractorName() == null)continue;
			if(sourceListBox.getSelectedIndex() != 0 && !entity.getDataDesc().equals(sourceListBox.getSelectedItemText())) continue;
			if((!showPositive.getValue() && entity.getPosOrNeg().equals("positive"))||(!showNegative.getValue() && entity.getPosOrNeg().equals("negative"))) continue;
			
			//filter all columns based on filter box
			if(!entity.getInteractorName().toUpperCase().contains(filterText) 
					&& !entity.getSourceName().toUpperCase().contains(filterText)
					&& !entity.getOverlayValue().toUpperCase().contains(filterText)
					&& !entity.getPosOrNeg().toUpperCase().contains(filterText)
					&& !entity.getDataDesc().toUpperCase().contains(filterText)) continue;
			
			newList.add(entity);
		}
		
		provider.getList().clear();
		provider.getList().addAll(newList);
		onPageChanged();
	}

	/**
	 * Catches event when pager change pages
	 */
	@Override
	public void onPageChanged() {
		if(tableEntities.size() == 0) return;
		int pageStart = pager.getPageStart();
		
		//Set equal to page end or number of filteredTableEntities if less than PairwisePopupResultsTable.PAGE_SIZE
		//ensures loop does break
		int pageEnd = filteredTableEntities.size() > PairwisePopupResultsTable.PAGE_SIZE ? 
				pageStart + PairwisePopupResultsTable.PAGE_SIZE : filteredTableEntities.size(); 
		
		List<String> entities = new ArrayList<>();
		for(int i = pageStart; i<pageEnd; i++)
			entities.add(filteredTableEntities.get(i).getInteractorId());
		loadTableOverlayData(entities);
	}

	/**
	 * initializes cytoscape with the base nodes and edges passed into the above constructors.
	 * @param nodeArr
	 * @param edgeArr
	 */
	private void initializeCytoscape(JSONArray nodeArr, JSONArray edgeArr) {
		this.cy = new CytoscapeEntity(RESOURCES.fiviewStyle().getText(), this);
		cy.cytoscapeInit(nodeArr.toString(),edgeArr.toString(), "cose", containerId);
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
				PairwiseTableEntity entity;
				tableEntities.add(entity = new PairwiseTableEntity(diagramNodesList.get(i),diagramNodesList.get(j),"solid"));
				boolean added = addEdge(entity);
				if(added)
					diagramEdgesCount++;
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
		loader.loadPairwiseData(props, false, new PairwiseDataLoader.Handler() {
			@Override
			public void onPairwiseLoaderError(Throwable exception) {
				Console.error(exception.getMessage());
			}
			@Override
			public void onPairwiseDataLoaded(List<PairwiseTableEntity> tableEntities) {
				PairwisePopup.this.tableEntities.addAll(tableEntities);
				filteredTableEntities = new ArrayList<>(tableEntities);
				setPairwiseResultsTable();
				addInitialInteractors(); //can add initial interactors only after uniprotToGeneMap and pairwiseOverlayMap are set.
			}
		});
	}

	/**
	 * Iterates over pairwiseOverlayMap of tableEntities.
	 * Directs the overlay of positive and negative interactions.
	 */
	private void addInitialInteractors() {
		Set<String> darkProteins = PairwiseOverlayFactory.get().getTDarkSet();
		for(String diagramNode : diagramNodes) {
			int counter  = 0;
			for(PairwiseTableEntity entity: tableEntities) {
				if(counter == 10) break;
				if(entity.getSourceId() == diagramNode && darkProteins.contains(entity.getInteractorId())) {
					addInteraction(entity);
					counter++;
				}
			}
			for(int i=counter; i< 10; i++) {
				int indexToGet = i+diagramEdgesCount;
				if(indexToGet >= tableEntities.size())break;
				PairwiseTableEntity entity = tableEntities.get(indexToGet); //offset by the number of diagram edges present in the array of table entities
				if(entity.getSourceId() == diagramNode && !displayedEdges.contains(indexToGet)) {
					addInteraction(entity);
				}
			}
		}
		cy.setCytoscapeLayout("cose");
		
		loadOverlay();
		filterTableEntities();
	}

	/**
	 * Directs addition of interactor node and all edges present in tableEntities
	 * @param entity
	 */
	private void addInteraction(PairwiseTableEntity entity) {
		//ensures interactions cannot be added if they exist as a diagram source edge. 
		//These interactions should be implied.
		if(diagramNodes.contains(entity.getSourceId()) && diagramNodes.contains(entity.getInteractorId())) return;
		
		addNode(entity.getInteractorId(), true);
		addEdge(entity);
		for(PairwiseTableEntity rel : tableEntities)
			if(entity.getInteractorId() == rel.getInteractorId())
				addEdge(rel);
		loadOverlay();
		filterTableEntities();
	}
	
	/**
	 * Add a node based on just a gene name
	 * @param uniprot
	 */
	private void addNode(String uniprot, boolean interactor) {
		if(this.uniprotToGeneMap == null)
			uniprotToGeneMap = PairwiseOverlayFactory.get().getUniprotToGeneMap();
		
		if(displayedNodes.contains(uniprot))return;
		
		JSONValue val = getProtein(uniprot, uniprotToGeneMap.get(uniprot), interactor);
		displayedNodes.add(uniprot);
		cy.addCytoscapeNodes(containerId, val.toString());
	}
	
	/**
	 * Directs creation of an edge based on passed in tableEntity
	 * Uses relationship and dataDesc to change style and color of the line.
	 * @param source is the uniprot of the base diagram protein
	 * @param target is the uniprot of the pariwise interactor
	 * @param relationship
	 * @param dataDesc
	 */
	private boolean addEdge(PairwiseTableEntity tableEntity) {
		
		if(displayedEdges.contains(tableEntities.indexOf(tableEntity))) return false;
		
		JSONValue val = makeFI(tableEntities.indexOf(tableEntity), 
				tableEntity.getSourceId(), tableEntity.getInteractorId(), tableEntity.getPosOrNeg());
		
		cy.addCytoscapeEdge(containerId, val.toString());
		for(PairwiseOverlayObject prop:  pairwiseOverlayObjects) {
			if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "positive")
				cy.recolorEdge(tableEntities.indexOf(tableEntity)+"", prop.getPositiveLineColorHex());
			else if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "negative")
				cy.recolorEdge(tableEntities.indexOf(tableEntity)+"", prop.getNegativeLineColorHex());
		}
		
		displayedEdges.add(tableEntities.indexOf(tableEntity));
		return true;
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
		DataOverlayProperties properties = PairwiseOverlayFactory.get().getDataOverlayProperties();
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
		DataOverlayProperties properties = PairwiseOverlayFactory.get().getDataOverlayProperties();
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
		if(tableDataOverlay.isDiscrete()) {
			for(PairwiseTableEntity entity : filteredTableEntities) {
				entity.setOverlayValue("");
				if(tableDataOverlay.getIdentifierValueMap().keySet().contains(entity.getInteractorId()))
					entity.setOverlayValue(tableDataOverlay.getLegendTypes().get(tableDataOverlay.getIdentifierValueMap().get(entity.getInteractorId()).intValue()));
			}
		}
		else {
			for(PairwiseTableEntity entity : filteredTableEntities) {
				entity.setOverlayValue("");
				if(tableDataOverlay.getIdentifierValueMap().keySet().contains(entity.getInteractorId()))
					entity.setOverlayValue(""+tableDataOverlay.getIdentifierValueMap().get(entity.getInteractorId()));
			}
		}
		provider.refresh();
		
		//Use cytoscape views version of dataOverlay so it still works 
		//if results are filtered to nothing
		if(tableDataOverlay.getEType().equals("Target Development Level"))
			this.eTypeAndTissue.setText("Overlay data source: " + tableDataOverlay.getEType());
		else
			this.eTypeAndTissue.setText("Overlay data source: " + tableDataOverlay.getTissueTypes().get(tableDataOverlay.getColumn()) + " - " + tableDataOverlay.getEType());
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
		String dataOverlayValue = null;
		if(dataOverlay.isDiscrete() && !dataOverlay.getEType().equals("Target Development Level"))
			dataOverlayValue = dataOverlay.getLegendTypes().get((int)Math.round(dataOverlay.getIdentifierValueMap().get(id)));
		else if(!dataOverlay.isDiscrete())
			dataOverlayValue = dataOverlay.getIdentifierValueMap().get(id) +"";
		
		
		PairwiseNodeContextPopup popup = new PairwiseNodeContextPopup(id,name, dataOverlayValue, new PairwiseNodeContextPopup.Handler() {
			@Override
			public void onRemoveButtonClicked(String id) {
				PairwisePopup.this.removeEdge(id);
				
			}
		});
		
		TCRDInfoLoader.loadSingleTargetLevelProtein(id, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				popup.setTargetDevLevel(result);
			}
			@Override
			public void onFailure(Throwable caught) {
				Console.error(caught);
			}
		});
		
		popup.getElement().getStyle().setZIndex(getCorrectZIndex());
		popup.setPopupPosition(x+5, y+5);
		popup.getElement().setId(this.containerId);
		popup.show();
	}
	
	protected void removeEdge(String id) {
		if(diagramNodes.contains(id)) return;
		cy.removeCytoscapeNode(id);
		displayedNodes.remove(id);
		
		for(int i=0; i<tableEntities.size(); i++) {
			if(tableEntities.get(i).getInteractorId() == id) {
				displayedEdges.remove(i);
			}
		}
		filterTableEntities();
	}

	@Override
	public void onNodeHovered(String id, String name, int x, int y) {
		if(infoPopup == null) main.add(infoPopup = new FIViewInfoPopup());
		infoPopup.getElement().getStyle().setZIndex(getCorrectZIndex());
		infoPopup.setNodeLabel(id, name, x, y);
	}

	@Override
	public void onEdgeHovered(String id, int x, int y) {
		if(infoPopup == null) main.add(infoPopup = new FIViewInfoPopup());
		infoPopup.getElement().getStyle().setZIndex(getCorrectZIndex());
		
		String description = "";
		
		PairwiseTableEntity edge = tableEntities.get(Integer.parseInt(id));
		
		if(edge.getPosOrNeg() == "solid")
			description = "Diagram source edge";
		else
			description = edge.getDataDesc() + "|" + edge.getPosOrNeg();
		
		infoPopup.setEdgeLabel(description, x, y);
	}

	@Override
	public void onCytoscapeCoreContextEvent(int x, int y) {
		FILayoutChangerPanel layoutChanger = new FILayoutChangerPanel(cy.getLayout(), new FILayoutChangerPanel.LayoutChangeHandler() {
			
			@Override
			public void onLayoutChange(FILayoutType type) {
				cy.setCytoscapeLayout(type.toString().toLowerCase());
			}
		});
		layoutChanger.getElement().getStyle().setZIndex(getCorrectZIndex());
		layoutChanger.show();
		layoutChanger.setPopupPosition(x+5, y+5);
	}

	private int getCorrectZIndex() {
		if(focused == true)
			return PairwiseOverlayFactory.get().getMaxZIndex() + 1;
		return zIndex+1;
	}
	
	/**
	 * Updates popup when pairwise overlay options change
	 * Clears all values that need to be reset
	 * @param pairwiseOverlayObjects
	 */
	public void updatePairwiseObjects(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		cy.clearCytoscapeGraph();
		tableEntities.clear();
		displayedNodes.clear();
		filteredTableEntities.clear();
		displayedNodes.clear();
		displayedEdges.clear();
		diagramEdgesCount = 0;
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
		PairwiseOverlayFactory.get().resetZIndexes();
		this.getElement().getStyle().setZIndex(PairwiseOverlayFactory.get().getMaxZIndex());
		focused = true; //set so context menu's have correct z index
	}
	
	@Override
	public void hide() {
		PairwiseOverlayFactory.get().removePopup(this.popupId);
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
		
		String filterTextBox();
		
		String pager();
		
		String filterPanel();
		
		String table();
		
		String eTypeAndTissueLabel();
		
		String smallText();
		
		String exportButton();
		
	}
}
