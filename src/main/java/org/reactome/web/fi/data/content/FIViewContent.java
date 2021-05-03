package org.reactome.web.fi.data.content;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.content.GenericContent;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPathway;
import org.reactome.web.diagram.data.graph.model.GraphSubpathway;
import org.reactome.web.diagram.data.layout.Coordinate;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;
import org.reactome.web.diagram.util.MapSet;
import org.reactome.web.fi.data.model.FIEntityFactory;
import org.reactome.web.fi.data.model.FIEntityNode;
import org.reactome.web.fi.data.model.FIEventNode;
import org.reactome.web.fi.data.model.ProteinEntityNode;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import uk.ac.ebi.pwp.structures.quadtree.client.Box;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewContent extends GenericContent{
	
	private Map<String, String> existingProteins;
	private Map<Long, GraphObject>graphObjectCache;
	private MapSet<String, GraphObject> identifierMap;
	private JSONArray proteinArray;
	private JSONArray fIArray;
	private Map<String, JSONObject> fIMap;
	private String fiJson;	
	private boolean fIViewContent;
	
	public FIViewContent(String fiJson) {
		this.graphObjectCache = new HashMap<>();
        this.identifierMap = new MapSet<>();
        
        //specific data to FIViewcontent
		this.existingProteins = new HashMap<>();
		this.fIMap = new HashMap<>();
		this.proteinArray = new JSONArray();
		this.fIArray = new JSONArray();
		this.fiJson = fiJson;
		
		GraphObjectFactory.content = this;
		
		parseFIPathway(this.fiJson);
	}
	
	@Override
	public Content init() {
		return this;
	}
	
	/**
	 * This method parses an income json from the corews server and
	 * sends parsed json one interaction at a time to convert to FIs
	 * @param fiJson
	 */
	private void parseFIPathway(String fiJson) {
				
		//setup Json to be manipulated
    	JSONValue value = JSONParser.parseStrict(fiJson);
    	JSONObject fiInteraction = value.isObject();
    	JSONValue fiInnerValue = fiInteraction.get("interaction");
    	JSONArray fiInnerArray = fiInnerValue.isArray();
    	
    	if(fiInnerArray == null) {
    		generateNodesAndEdge(fiInnerValue);
    		makeIdentifiersMap();
    		return;
    	}
    	
    	//for loop to iterate over fiInteractionArray
    	for(int i=0; i <fiInnerArray.size(); i++) {
    		//get objects internal to interaction in fiInteractionArray.get(i)
    		JSONValue interaction = fiInnerArray.get(i);
    		generateNodesAndEdge(interaction);
    	}
    	
    	//Takes all existing proteins after FIs are made,
    	//converts them to graphObjects and places them on identifierMap.
    	if(existingProteins.size() > 0) makeIdentifiersMap();
    	
	}

	private void generateNodesAndEdge(JSONValue interaction) {
		JSONObject interactionOb = interaction.isObject();
		JSONObject firstProtein = interactionOb.get("firstProtein").isObject();
		JSONObject secondProtein = interactionOb.get("secondProtein").isObject();
		JSONObject annotation = interactionOb.get("annotation").isObject();
		JSONValue reactomeSources = interactionOb.get("reactomeSources");
		
		//Get info for protein node and Fi edge
		if(firstProtein != null && secondProtein !=null && annotation !=null && reactomeSources != null) {
			//get info for protein node
			String shortNameOne = firstProtein.get("shortName").isString().stringValue();
			String accessionOne = firstProtein.get("primaryAccession").isString().stringValue();
			String shortNameTwo = secondProtein.get("shortName").isString().stringValue();
			String accessionTwo = secondProtein.get("primaryAccession").isString().stringValue();
			
			//get info for FI edge
			String annotationDirection = "";
			if(annotation.get("direction") != null) {
				annotationDirection = annotation.get("direction").isString().stringValue();
			}
			else if(annotation.get("direction") == null)
				annotationDirection = "none";
			
			//send interaction to be added to cytoscape.js network
			makeFI(shortNameOne, accessionOne, shortNameTwo, accessionTwo, annotationDirection, reactomeSources);
			
			//create graph object from reactomeSources and add to cache
			convertSourcesToGraphObjects(reactomeSources);

		}
	}
	
	/**
	 * Makes Identifier map for use later
	 */
	private void makeIdentifiersMap() {
		int counter = 0;
		for(Map.Entry<String, String> protein : existingProteins.entrySet()) {
			JSONObject proteinJson = new JSONObject();
			proteinJson.put("shortName", new JSONString(protein.getValue()));
			proteinJson.put("id", new JSONString(counter +""));
			proteinJson.put("identifier", new JSONString(protein.getKey()));
			proteinJson.put("sourceType", new JSONString("EntityWithAccessionedSequence"));
			try {
				ProteinEntityNode source = FIEntityFactory.getSourceEntity(ProteinEntityNode.class, proteinJson.toString());		
				identifierMap.add(protein.getKey(), GraphObjectFactory.getOrCreateDatabaseObject(source));
			} catch(DiagramObjectException e) {
				e.printStackTrace();
			}
			counter--;
		}
		
	}

	/**
	 * directs generation of a passed in proteins and interaction 
	 * Use when starting CytoscapeVisualizer with multiple nodes
	 * Also creates GraphObjects from reactomeSources and adds to graphObjectCache.
	 * @param proteinOneShortName
	 * @param proteinOneAccession
	 * @param proteinTwoShortName
	 * @param proteinTwoAccession
	 * @param annotationDirection
	 * @param reactomeSources
	 */
	public void makeFI(String proteinOneShortName, 
			String proteinOneAccession,
			String proteinTwoShortName, 
			String proteinTwoAccession,
			String annotationDirection,
			JSONValue reactomeSources) {
		
		proteinOneAccession = correctAccession(proteinOneShortName, proteinOneAccession);
		proteinTwoAccession = correctAccession(proteinTwoShortName, proteinTwoAccession);

		//set proteins for an interaction if they don't exist as nodes already
		ensureProteinInJSON(proteinOneShortName, proteinOneAccession);
		ensureProteinInJSON(proteinTwoShortName, proteinTwoAccession);

		//set interaction or edge between two proteins.
		JSONObject interaction = createFIEdge(proteinOneAccession, 
				proteinTwoAccession, 
				annotationDirection, 
				reactomeSources);

		//add each edge to fi array and put on map
		fIMap.put(fIArray.size() + "", interaction);
		fIArray.set(fIArray.size(), interaction);
	}
	
	/**
	 * Check accession of proteins and remove any isoform identifiers 
	 * and change any ENSG identifiers to Uniprot.
	 * !!!MUST BE CALLED AFTER UniprotToGeneMap LOADED IN PAIRWISEPOPUPFACTORY
	 * @param geneName
	 * @param accession
	 * @return
	 */
	private String correctAccession(String geneName, String accession) {
		if(accession.contains("-"))
			return accession.substring(0, accession.indexOf("-"));
		else if(accession.contains("ENSG")) {
			Map<String, String> uniprotToGeneMap = IDGPopupFactory.get().getUniprotToGeneMap();
			for(Map.Entry<String,String> entry: uniprotToGeneMap.entrySet()) {
				if(geneName == entry.getValue()) {
					accession = entry.getKey();
					return accession;
				}
			}
		}
		return accession;
	}

	/**
	 * Creates element in proteinArray if it doesn't already exist in the proteinMap
	 * @param shortName
	 * @param accession
	 * @return 
	 */
	private void ensureProteinInJSON(String shortName, String accession) {

		if (existingProteins.containsKey(accession))
			return;

		//make JSONObject for protein
		JSONObject protein = new JSONObject();
		protein.put("id", new JSONString(accession));
		protein.put("name", new JSONString(shortName));
		protein.put("color", new JSONString("#00CC00"));

		//add protein to proteinArray of nodes
		JSONObject proteinData = new JSONObject();
		proteinData.put("group", new JSONString("nodes"));
		proteinData.put("data", protein);
		proteinArray.set(proteinArray.size(), proteinData);
		existingProteins.put(accession, shortName);
	}
	
	/**
	 * make a single edge for a Cytoscape.js display
	 * @param proteinOneAccession
	 * @param proteinTwoAccession
	 * @param annotationDirection
	 * @param reactomeSources
	 * @return
	 */
	private JSONObject createFIEdge(String proteinOneAccession, 
			String proteinTwoAccession,
			String annotationDirection, 
			JSONValue reactomeSources) {
		
		JSONObject fi = new JSONObject();
		fi.put("id", new JSONString(fIArray.size()+""));
		fi.put("source", new JSONString(proteinOneAccession));
		fi.put("target", new JSONString(proteinTwoAccession));
		fi.put("direction", new JSONString(annotationDirection));
		fi.put("reactomeSources", reactomeSources);
		
		JSONArray idA = new JSONArray();
		
		JSONArray jsonA = reactomeSources.isArray();
		if(jsonA != null) {
			for (int i=0; i<jsonA.size(); i++) {
				JSONObject obj = jsonA.get(i).isObject();
				JSONValue id = obj.get("reactomeId");
				idA.set(idA.size(), id);
			}
			fi.put("reactomeId", idA);			
		}
		else if(jsonA == null) {
			JSONObject obj = reactomeSources.isObject();
			JSONObject idOb = new JSONObject();
			idOb.put("reactomeId", obj.get("reactomeId"));
			
			fi.put("reactomeId", obj.get("reactomeId"));
		}
		
		JSONObject fiData = new JSONObject();
		fiData.put("group", new JSONString("edges"));
		fiData.put("data", fi);

		return fiData;		
	}
	
	/**
	 * parses reactomeSources if necessary and sends to cache for processing
	 * @param reactomeSources
	 */
	private void convertSourcesToGraphObjects(JSONValue reactomeSources) {
		
		JSONArray jsonArray = reactomeSources.isArray();
		
		
		if(jsonArray != null) {
			for(int i=0; i<jsonArray.size(); i++) {
			
				JSONObject obj = jsonArray.get(i).isObject();
				makeGraphObject(obj);
			}
		}
		else if(jsonArray == null) {
			JSONObject obj = reactomeSources.isObject();
			makeGraphObject(obj);	
		}
	}

	/**
	 * converts reactomeSources JsonObject into GraphObjects
	 * This converts Complexes into FIEntityNodes.
	 * Converts Reactions into FIEventNodes.
	 * @param reactomeSources
	 */
	protected void makeGraphObject(JSONObject reactomeSources) {
		if(graphObjectCache.containsKey(Long.parseLong(reactomeSources.get("reactomeId").isString().stringValue())))
			return;
		
		JSONObject sourceObj = new JSONObject();
		sourceObj.put("reactomeId", reactomeSources.get("reactomeId"));
		
		//choose sourceType for graph object based on passed in sourceType
		sourceObj.put("sourceType", extractSourceType(reactomeSources));
		
		//makes graphObject from source and stores in graphObjectCache
		GraphObject graphObj = null;
		try {
			if(sourceObj.get("sourceType").isString().stringValue().contentEquals("Complex")) {
				sourceObj.put("reactomeId", reactomeSources.get("reactomeId"));
				FIEntityNode source = FIEntityFactory.getSourceEntity(FIEntityNode.class, sourceObj.toString());
				graphObj = GraphObjectFactory.getOrCreateDatabaseObject(source);
			}
			else if(sourceObj.get("sourceType").isString().stringValue().contentEquals("Reaction")){
				sourceObj.put("dbId", reactomeSources.get("reactomeId"));
				FIEventNode source = FIEntityFactory.getSourceEntity(FIEventNode.class, sourceObj.toString());
				graphObj = GraphObjectFactory.getOrCreateDatabaseObject(source);
			}
			
		} catch(DiagramObjectException e) {
			e.printStackTrace();
		}
		graphObjectCache.put(graphObj.getDbId(), graphObj);
	}
	
	/**
	 * Determines sourceType for each reactomeSource added to graphObjectCache
	 * @param obj
	 * @return
	 */
	private JSONString extractSourceType(JSONObject obj) {
		String name = obj.get("sourceType").isString().stringValue();
		name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		return new JSONString(name);
	}
	
	public String getProteinArray() {
		return proteinArray.toString();
	}
	
	public JSONArray getFIArray() {
		return fIArray;
	}

	public JSONObject getFIFromMap(String id) {
		return fIMap.get(id);
	}
	
	@Override
	public void cache(GraphObject dbObject) {
		// TODO Auto-generated method stub
	}

	@Override
	public void cache(List<? extends DiagramObject> diagramObjects) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean containsOnlyEncapsulatedPathways() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsEncapsulatedPathways() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<DiagramObject> getHoveredTarget(Coordinate p, double factor) {
        return Collections.EMPTY_LIST;
	}

	@Override
	public Set<GraphPathway> getEncapsulatedPathways() {
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public GraphObject getDatabaseObject(String identifier) {
		Long dbId = Long.parseLong(identifier.substring(identifier.lastIndexOf("-")+1));
		if(dbId != null)
			return getDatabaseObject(dbId);
		
		return getDatabaseObject(Long.parseLong(identifier));
	}

	@Override
	public GraphObject getDatabaseObject(Long dbId) {
		return graphObjectCache.get(dbId);
	}


	@Override
	public MapSet<String, GraphObject> getIdentifierMap() {
		return identifierMap;
	}

	@Override
	public GraphSubpathway getGraphSubpathway(String stId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphSubpathway getGraphSubpathway(Long dbId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GraphSubpathway> getSubpathways() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GraphObject> getAllInvolvedPathways() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiagramObject getDiagramObject(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DiagramObject getDiagramObject(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GraphObject> getDatabaseObjects() {
		// TODO Auto-generated method stub
		return new HashSet<>(this.graphObjectCache.values());
	}

	@Override
	public Collection<DiagramObject> getDiagramObjects() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<DiagramObject> getVisibleItems(Box visibleArea) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfBurstEntities() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clearDisplayedInteractors() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Type getType() {
		return Type.DIAGRAM;
	}

	public boolean isFIViewContentFlag() {
		return fIViewContent;
	}

	public void setFIViewContent(boolean fiViewContent) {
		fIViewContent = fiViewContent;
	}

}
