package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.data.graph.model.GraphEntityWithAccessionedSequence;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.model.GraphProteinDrug;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.gwtCytoscapeJs.client.CytoscapeWrapper.Handler;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupCytoscapePanel extends SimplePanel implements Handler {

	public interface CytoscapePanelHandler{
		
	}
	
	private String containerId;
	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	private Set<String> diagramNodes;
	private Set<String> displayedNodes;
	private Map<PairwiseTableEntity, Integer> edgeIdToEntity;
	
	private CytoscapeEntity cy;
	
	public PairwisePopupCytoscapePanel(String popupId, List<PairwiseOverlayObject> pairwiseOverlayObjects, NewPairwisePopup.Resources RESOURCES) {
		this.displayedNodes = new HashSet<>();
		this.edgeIdToEntity = new HashMap<>();
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
		
		this.getElement().setId(containerId = "cy-" + popupId);
		this.setStyleName(RESOURCES.getCSS().cyView());
		
		this.cy = new CytoscapeEntity(RESOURCES.fiviewStyle().getText(), this);
		cy.cytoscapeInit(new JSONArray().toString(), new JSONArray().toString(), "cose", containerId);
		cy.setCytoscapeLayout("cose");
	}

	public void init(GraphObject graphObject) {
		setDiagramNodes(graphObject);
		initBaseCytoscape();
	}

	public void init(String uniprot, String geneName) {
		setDiagramNodes(uniprot, geneName);
		initBaseCytoscape();
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
		
		edgeIdToEntity.put(tableEntity, edgeIdToEntity.size());
		
		JSONValue val = makeFI(edgeIdToEntity.size(), 
				tableEntity.getSourceId(), tableEntity.getInteractorId(), tableEntity.getPosOrNeg());
		
		cy.addCytoscapeEdge(containerId, val.toString());
		for(PairwiseOverlayObject prop:  pairwiseOverlayObjects) {
			if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "positive")
				cy.recolorEdge(edgeIdToEntity.get(tableEntity)+"", prop.getPositiveLineColorHex());
			else if(prop.getId() == tableEntity.getDataDesc() && tableEntity.getPosOrNeg() == "negative")
				cy.recolorEdge(edgeIdToEntity.get(tableEntity)+"", prop.getNegativeLineColorHex());
		}
		
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
