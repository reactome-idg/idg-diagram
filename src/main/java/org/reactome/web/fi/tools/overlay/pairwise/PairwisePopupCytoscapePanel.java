package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.data.loader.OverlayLoader;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.overlay.profiles.OverlayColours;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper.Handler;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupCytoscapePanel implements Handler {

	public interface CytoscapePanelHandler{
		
	}
	
	private String containerId;
	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private Set<String> diagramNodes;
	private Set<String> displayedNodes;
	private Map<PairwiseTableEntity, Integer> edgeIdToEntity;
	
	private CytoscapeEntity cy;
	
	private int edgeCount = 0;
	
	private DataOverlay dataOverlay;
	
	public PairwisePopupCytoscapePanel(String popupId, Set<String> diagramNodes, List<PairwiseOverlayObject> pairwiseOverlayObjects, NewPairwisePopup.Resources RESOURCES) {
		this.diagramNodes = diagramNodes;
		this.displayedNodes = new HashSet<>();
		this.edgeIdToEntity = new HashMap<>();
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
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
	}
	
	public void addInteractions(Set<PairwiseTableEntity> entities) {
		boolean resetLayout = false;
		for(PairwiseTableEntity entity : entities) {
			//ensures interactions cannot be added if they exist as a diagram source edge. 
			//These interactions should be implied.
			if(diagramNodes.contains(entity.getSourceId()) && diagramNodes.contains(entity.getInteractorId())) return;
			
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
	 * Directs creation of an edge based on passed in tableEntity
	 * Uses relationship and dataDesc to change style and color of the line.
	 * @param source is the uniprot of the base diagram protein
	 * @param target is the uniprot of the pariwise interactor
	 * @param relationship
	 * @param dataDesc
	 */
	private boolean addEdge(PairwiseTableEntity tableEntity) {
		
		for(PairwiseTableEntity entity: edgeIdToEntity.keySet())
			if(entity.equals(tableEntity)) return false;
		
		edgeIdToEntity.put(tableEntity, edgeCount);
		
		JSONValue val = makeFI(edgeCount, 
				tableEntity.getSourceId(), tableEntity.getInteractorId(), tableEntity.getPosOrNeg());
		
		cy.addCytoscapeEdge(containerId, val.toString());
		for(PairwiseOverlayObject prop:  pairwiseOverlayObjects) {
			if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "positive")
				cy.recolorEdge(edgeIdToEntity.get(tableEntity)+"", prop.getPositiveLineColorHex());
			else if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "negative")
				cy.recolorEdge(edgeIdToEntity.get(tableEntity)+"", prop.getNegativeLineColorHex());
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
	
	public void loadOverlay() {
		DataOverlayProperties props = PairwiseOverlayFactory.get().getDataOverlayProperties();
		props.setUniprots(String.join(",", this.displayedNodes));
		
		OverlayLoader loader = new OverlayLoader();
		loader.load(props, new OverlayLoader.Handler() {
			@Override
			public void onDataOverlayLoaded(DataOverlay dataOverlay) {
				PairwisePopupCytoscapePanel.this.dataOverlay = dataOverlay;
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
	
	public void updateOverlayColumn(int column) {
		dataOverlay.setColumn(column);
		overlayData();
	}

	public void pairwisePropertiesChanged() {
		cy.clearCytoscapeGraph();
		displayedNodes.clear();
		this.edgeIdToEntity.clear();
		this.edgeCount = 0;
		initBaseCytoscape();
	}
	
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
}
