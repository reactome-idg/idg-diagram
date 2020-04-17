package org.reactome.web.fi.tools.popup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;
import org.reactome.web.fi.client.popups.FIViewInfoPopup;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.data.loader.TCRDDataLoader;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.overlay.profiles.OverlayColours;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseNodeContextPopup;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;
import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper.Handler;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author brunsont
 *
 */
public class IDGPopupCytoscapeController implements Handler{

	public interface CytoscapePanelHandler{
		void highlightPIRow(PairwiseTableEntity entity, boolean select);
		void highlightDrugRow(DrugTargetResult entity, boolean select);
		void resetSelection();
	}
	
	private CytoscapePanelHandler handler;
	
	private String containerId;
	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private Set<String> diagramNodes;
	private Set<String> displayedNodes;
	private Map<PairwiseTableEntity, Integer> entityToEdgeId;
	private Map<Integer, DrugTargetResult> edgeIdToDrugTarget;
	private Map<String, DrugTargetResult> presentDrugs;
	
	private CytoscapeEntity cy;
	
	private int edgeCount = 0;
	
	private DataOverlay dataOverlay;
	
	private boolean focused;
	private int zIndex;
	private FIViewInfoPopup infoPopup;
	
	public IDGPopupCytoscapeController(String popupId, Set<String> diagramNodes, IDGPopup.Resources RESOURCES, int zIndex, CytoscapePanelHandler handler) {
		this.diagramNodes = diagramNodes;
		this.handler = handler;
		this.displayedNodes = new HashSet<>();
		this.entityToEdgeId = new HashMap<>();
		this.edgeIdToDrugTarget = new HashMap<>();
		this.zIndex = zIndex;
		this.pairwiseOverlayObjects = IDGPopupFactory.get().getCurrentPairwiseProperties();
		this.containerId = "cy-" + popupId;
		
		this.cy = new CytoscapeEntity(RESOURCES.fiviewStyle().getText(), this);
		cy.cytoscapeInit(new JSONArray().toString(), new JSONArray().toString(), "cose", containerId);
		cy.setCytoscapeLayout("cose");
		
		initBaseCytoscape();
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
			loadOverlay();
			return;
		}
		
		for(String node : diagramNodes) {
			addNode(node,false);
		}
		for(int i=0; i<diagramNodesList.size(); i++) {
			for(int j=i+1; j<diagramNodes.size(); j++) {
				PairwiseTableEntity entity = new PairwiseTableEntity(diagramNodesList.get(i),diagramNodesList.get(j),"solid");
				addEdge(entity);
			}
		}
		cy.setCytoscapeLayout("cose");
		loadOverlay();
	}
	
	public void addInteractions(Set<PairwiseTableEntity> entities) {
		boolean resetLayout = false;
		for(PairwiseTableEntity entity : entities) {
			//ensures interactions cannot be added if they exist as a diagram source edge. 
			//These interactions should be implied.
			if(diagramNodes.contains(entity.getSourceId()) && diagramNodes.contains(entity.getInteractorId())) continue;
			
			addNode(entity.getInteractorId(), true);
			boolean added = addEdge(entity);
			resetLayout = added == true ? added:resetLayout;
		}
		if(resetLayout) cy.setCytoscapeLayout("cose");
		loadOverlay();
	}

	/**
	 * Add a node based on just a gene name
	 * @param uniprot
	 */
	private void addNode(String uniprot, boolean interactor) {
			Map<String, String> uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
		
		if(displayedNodes.contains(uniprot))return;
		
		JSONValue val = getProtein(uniprot, uniprotToGeneMap.get(uniprot), interactor);
		displayedNodes.add(uniprot);
		cy.addCytoscapeNodes(containerId, val.toString());
	}
	
	/**
	 * Makes a node for only a passed in gene name string
	 * @param displayName
	 * @return
	 */
	private JSONValue getProtein(String id, String displayName, boolean interactor) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		node.put("id", new JSONString(id));
		node.put("name", new JSONString(displayName));
		if(interactor == true)
			node.put("interactor", new JSONString("true"));
		else
			node.put("interactor", new JSONString("false"));
		node.put("color", new JSONString("#FF0000"));
		result.put("data", node);
		return result;
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
		
		for(PairwiseTableEntity entity: entityToEdgeId.keySet())
			if(entity.equals(tableEntity)) return false;
		
		entityToEdgeId.put(tableEntity, edgeCount);
		
		JSONValue val = makeFI(edgeCount, 
				tableEntity.getSourceId(), tableEntity.getInteractorId(), tableEntity.getPosOrNeg());
		
		cy.addCytoscapeEdge(containerId, val.toString());
		for(PairwiseOverlayObject prop:  pairwiseOverlayObjects) {
			if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "positive")
				cy.recolorEdge(edgeCount+"", prop.getPositiveLineColorHex());
			else if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "negative")
				cy.recolorEdge(edgeCount+"", prop.getNegativeLineColorHex());
		}
		
		edgeCount++;
		return true;
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
	 * Iterate over interactions in each drug. If diagramNodes contains the interaction uniprot, make and add edge to edgeArray
	 * If edgeArray > 0 for a drug, make the node for that drug, add it to the cytoscape view, and add all the edges.
	 * Repeat for all drugs present in the diagram.
	 * @param entities 
	 */
	public void addDrugs(List<DrugTargetResult> entities) {
		presentDrugs = new HashMap<>();
		entities.forEach(x -> {
			if(!presentDrugs.containsKey("DG" + x.getDrugId())) {
				JSONObject drug = getProtein("DG"+x.getDrugId(), x.getDrugName(), false).isObject();
				drug.get("data").isObject().put("drug", new JSONString("true"));
				cy.addCytoscapeNodes(containerId, drug.toString());
				cy.highlightNode("DG"+x.getDrugId(), "#B89AE6");
				presentDrugs.put("DG"+x.getDrugId(), x);
			}
			JSONObject edge = makeFI(edgeCount, x.getUniprot(), "DG"+x.getDrugId(), "solid").isObject();
			edgeIdToDrugTarget.put(edgeCount, x);
			cy.addCytoscapeEdge(containerId, edge.toString());
			edgeCount++;
		});
//		Collection<Drug> drugs = IDGPopupFactory.get().getDrugTargets();
//		drugs.forEach(drug -> {
//			JSONArray edgeArray = new JSONArray();
//			drug.getDrugInteractions().forEach((k,v) -> {
//				if(diagramNodes.contains(k)) {
//					JSONObject edge = makeFI(edgeCount, k, "DG"+drug.getId(), "solid").isObject();
//					edgeIdToDrugTarget.put(edgeCount, v);
//					edgeArray.set(edgeArray.size(), edge);
//					edgeCount++;
//				}
//			});
//			if(edgeArray.size() > 0) {
//				JSONObject protein = getProtein("DG"+drug.getId(), drug.getName(), false).isObject();
//				protein.get("data").isObject().put("drug", new JSONString("true"));
//				
//				if(!presentDrugs.containsKey("DG"+drug.getId())) {
//					cy.addCytoscapeNodes(containerId, protein.toString());
//					cy.highlightNode("DG"+drug.getId(), "#B89AE6");
//					presentDrugs.put("DG"+drug.getId(), drug);
//				}
//				cy.addCytoscapeEdge(containerId, edgeArray.toString());
//			}
//		});
		cy.setCytoscapeLayout("cose");
	}
	

	public void loadOverlay() {
		DataOverlayProperties props = IDGPopupFactory.get().getDataOverlayProperties();
		props.setUniprots(String.join(",", this.displayedNodes));
		
		TCRDDataLoader loader = new TCRDDataLoader();
		loader.load(props, new TCRDDataLoader.Handler() {
			@Override
			public void onDataOverlayLoaded(DataOverlay dataOverlay) {
				IDGPopupCytoscapeController.this.dataOverlay = dataOverlay;
				overlayData();
			}
			@Override
			public void onOverlayLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
		});
	}
	
	protected void overlayData() {
		cy.resetNodeColor();
		cy.resetSelection();
		recolorDrugs();
		this.dataOverlay.updateIdentifierValueMap();
		
		if(dataOverlay.isDiscrete())
			overlayDiscreteData();
		else
			overlayContinuousData();
	}
	
	private void recolorDrugs() {
		if(presentDrugs != null)
			presentDrugs.keySet().forEach(d -> {
				cy.highlightNode(d+"", "#B89AE6");
			});
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
	
	public void updateOverlayColumn(int column) {
		dataOverlay.setColumn(column);
		overlayData();
	}

	public void pairwisePropertiesChanged() {
		cy.clearCytoscapeGraph();
		this.pairwiseOverlayObjects = IDGPopupFactory.get().getCurrentPairwiseProperties();
		displayedNodes.clear();
		this.entityToEdgeId.clear();
		presentDrugs.clear();
		this.edgeIdToDrugTarget.clear();
		this.edgeCount = 0;
		initBaseCytoscape();
	}
	
	protected void removeEdge(String id) {
		if(diagramNodes.contains(id)) return;
		cy.removeCytoscapeNode(id);
		displayedNodes.remove(id);
		
		entityToEdgeId.keySet().forEach(k -> {
			if(k.getInteractorId() == id) {
				entityToEdgeId.remove(k);
				return;
			}
		});
	}
	
	public void selectInteraction(PairwiseTableEntity entity) {
		if(entityToEdgeId.containsKey(entity)) {
			cy.selectEdge(entityToEdgeId.get(entity)+"");
		}
	}
	
	public void selectDrug(DrugTargetResult entity) {
		if(edgeIdToDrugTarget.containsValue(entity))
			edgeIdToDrugTarget.forEach((k,v) -> {
				if(entity == v)
					cy.selectEdge(k+"");
			});
	}
	
	public void resize() {
		cy.resize();
	}
	
	public void setFocused(boolean focused) {
		this.focused = focused;
	}
	
	private int getCorrectZIndex() {
		if(focused == true)
			return IDGPopupFactory.get().getMaxZIndex() + 1;
		return zIndex+1;
	}

	@Override
	public void onNodeHovered(String id, String name, int x, int y) {
		if(infoPopup == null) infoPopup = new FIViewInfoPopup();
		infoPopup.getElement().getStyle().setZIndex(getCorrectZIndex());
		infoPopup.setNodeLabel(id, name, x, y);
		infoPopup.show();
	}

	@Override
	public void onEdgeHovered(String id, int x, int y) {
		if(infoPopup == null) infoPopup = new FIViewInfoPopup();
		infoPopup.getElement().getStyle().setZIndex(getCorrectZIndex());
		
		if(this.edgeIdToDrugTarget.containsKey(Integer.parseInt(id))) {
			openDrugEdgePopup(Integer.parseInt(id), x, y);
			return;
		}
		
		String description = "";
		
		PairwiseTableEntity edge = null;
		for(Entry<PairwiseTableEntity, Integer> entry : entityToEdgeId.entrySet()) {
			if(Integer.parseInt(id) == entry.getValue()) {
				edge = entry.getKey();
				break;
			}
		}
		if(edge == null)return;
		
		if(edge.getPosOrNeg() == "solid")
			description = "Diagram source edge";
		else
			description = edge.getDataDesc() + "|" + edge.getPosOrNeg();
		
		infoPopup.setEdgeLabel(description, x, y);
	}

	private void openDrugEdgePopup(int edgeId, int x, int y) {
		String description = "";
		DrugTargetResult interaction = this.edgeIdToDrugTarget.get(edgeId);
		description = "Action Type: " + interaction.getActionType() + "\n" +
					  "Activity Type: " + interaction.getActivityType() + "\n" +
					  "Activity Value: " + NumberFormat.getFormat("#.##E0").format(interaction.getActivityValue());
		infoPopup.setEdgeLabel(description, x, y);
	}

	@Override
	public void onNodeMouseOut() {
		infoPopup.hide();
	}

	@Override
	public void onEdgeMouseOut() {
		infoPopup.hide();
		
	}

	/**
	 * Opens remove context button on context click
	 */
	@Override
	public void onNodeContextSelectEvent(String id, String name, int x, int y) {
		//only opening if node is not a drug node
		if(presentDrugs== null || !presentDrugs.containsKey(id))
			openProteinContextInfo(id, name, x, y);
		else if(presentDrugs != null && presentDrugs.containsKey(id))
			openDrugContextInfo(id, x,y);
	}

	private void openDrugContextInfo(String id, int x, int y) {
		DrugTargetContextPanel popup = new DrugTargetContextPanel(presentDrugs.get(id));
		popup.getElement().getStyle().setZIndex(getCorrectZIndex());
		popup.setPopupPosition(x+5, y+5);
		popup.show();
	}

	/**
	 * Makes context info for node or interactor context select
	 * @param id
	 * @param name
	 * @param x
	 * @param y
	 */
	private void openProteinContextInfo(String id, String name, int x, int y) {
		String dataOverlayValue = null;
		if(dataOverlay.isDiscrete() && !dataOverlay.getEType().equals("Target Development Level"))
			dataOverlayValue = dataOverlay.getLegendTypes().get((int)Math.round(dataOverlay.getIdentifierValueMap().get(id)));
		else if(!dataOverlay.isDiscrete())
			dataOverlayValue = dataOverlay.getIdentifierValueMap().get(id) +"";
		
		boolean showRemove = !diagramNodes.contains(id);
		PairwiseNodeContextPopup popup = new PairwiseNodeContextPopup(id,name, dataOverlayValue, showRemove, new PairwiseNodeContextPopup.Handler() {
			@Override
			public void onRemoveButtonClicked(String id) {
				removeEdge(id);
				
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
		popup.show();
	}
	
	@Override
	public void onNodeClicked(String id, String name) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEdgeClicked(String id) {
		int idInt = Integer.parseInt(id);
		if(edgeIdToDrugTarget.containsKey(idInt)) {
			handler.highlightDrugRow(edgeIdToDrugTarget.get(idInt), true);
			return;
		}
		entityToEdgeId.forEach((k,v) -> {
			if(v == idInt)
				handler.highlightPIRow(k, true);
		});
	}

	@Override
	public void onCytoscapeCoreContextEvent(int x, int y) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCytoscapeCoreSelectedEvent() {
		handler.resetSelection();
	}

	@Override
	public void onEdgeContextSelectEvent(String id, int x, int y) {
		// TODO Auto-generated method stub
	}

	public void resetSelection() {
		cy.deselectAll();
	}
}
