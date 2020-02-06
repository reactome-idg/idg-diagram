package org.reactome.web.fi.client.visualisers.fiview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.events.AnalysisProfileChangedEvent;
import org.reactome.web.diagram.events.ExpressionColumnChangedEvent;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.diagram.handlers.AnalysisProfileChangedHandler;
import org.reactome.web.diagram.handlers.ExpressionColumnChangedHandler;
import org.reactome.web.diagram.profiles.analysis.AnalysisColours;
import org.reactome.web.diagram.profiles.interactors.InteractorColours;
import org.reactome.web.diagram.util.gradient.ThreeColorGradient;
import org.reactome.web.fi.data.content.FIViewContent;
import org.reactome.web.gwtCytoscapeJs.events.EdgeClickedEvent;
import org.reactome.web.gwtCytoscapeJs.events.EdgeContextSelectEvent;
import org.reactome.web.gwtCytoscapeJs.events.EdgeHoveredEvent;
import org.reactome.web.gwtCytoscapeJs.events.EdgeMouseOutEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeClickedEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeContextSelectEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeHoveredEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeMouseOutEvent;
import org.reactome.web.gwtCytoscapeJs.events.CytoscapeCoreContextEvent;
import org.reactome.web.gwtCytoscapeJs.events.CytoscapeCoreSelectedEvent;
import org.reactome.web.gwtCytoscapeJs.handlers.EdgeClickedHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.EdgeContextSelectHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.EdgeHoveredHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.EdgeMouseOutHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.NodeClickedHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.NodeContextSelectHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.NodeHoveredHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.NodeMouseOutHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.CytoscapeCoreContextHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.CytoscapeCoreSelectedHandler;
import org.reactome.web.fi.client.visualisers.fiview.FIViewInfoPopup;
import org.reactome.web.fi.events.CytoscapeLayoutChangedEvent;
import org.reactome.web.fi.events.FIViewMessageEvent;
import org.reactome.web.fi.events.FIViewOverlayEdgeHoveredEvent;
import org.reactome.web.fi.events.FIViewOverlayEdgeSelectedEvent;
import org.reactome.web.fi.events.FireGraphObjectSelectedEvent;
import org.reactome.web.fi.handlers.CytoscapeLayoutChangedHandler;
import org.reactome.web.fi.handlers.FireGraphObjectSelectedHandler;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.overlay.profiles.OverlayColours;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;


/**
 * 
 * @author brunsont
 *
 */
public class FIViewVisualizer extends AbsolutePanel implements Visualiser, AnalysisProfileChangedHandler,
	ExpressionColumnChangedHandler, CytoscapeLayoutChangedHandler, FireGraphObjectSelectedHandler, CytoscapeEntity.Handler{
	
	private EventBus eventBus;
	private CytoscapeEntity cy;
	
	private Context context;
	
	private FILayoutChangerPanel fILayoutChangerPanel;
	private EdgeContextPanel edgeContextPanel;
	private Map<String, NodeContextPanel> nodeContextPanelMap;
	
	private GraphObject selected;
	
	private AnalysisStatus analysisStatus;
    private int selectedExpCol = 0;
	
	private boolean initialised = false;
	private boolean cytoscapeInitialised;
	private boolean cytoscapeClickedFlag;
	private boolean edgeHoveredFlag;
    private int viewportWidth = 0;
    private int viewportHeight = 0;
	private FIViewInfoPopup infoPopup;
	
	private SimplePanel cyView;
	private DataOverlay dataOverlay;
    
	public FIViewVisualizer(EventBus eventBus) {
		super();
		this.getElement().addClassName("pwp-FIViz"); //IMPORTANT!
		this.eventBus = eventBus;
		
		fILayoutChangerPanel = new FILayoutChangerPanel(eventBus);
		edgeContextPanel = new EdgeContextPanel(eventBus);
		nodeContextPanelMap = new HashMap<>();
		cyView =  new SimplePanel();
				
		initHandlers();
		
		//default this value to false
		cytoscapeClickedFlag = false;
		edgeHoveredFlag = false;
	}
	
	protected void initialise() {
		if(!initialised) {
			this.initialised = true;
			this.viewportWidth = getParent().getOffsetWidth();
			this.viewportHeight = getParent().getOffsetHeight();
			ScriptInjector.fromString(FIVIEWPORTRESOURCES.cytoscapeLibrary().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
			
			//created once and reused every time a new context is loaded
			cy = new CytoscapeEntity(FIVIEWPORTRESOURCES.fiviewStyle().getText(), this);
			
			cyView.getElement().setId("cy");
			
			this.add(cyView);
			this.cyView.setSize(viewportWidth+"px", viewportHeight+"px");
			
			setSize(viewportWidth, viewportHeight);
			
			cytoscapeInitialised = false;
			
			//set up popup for info and set location
			infoPopup = new FIViewInfoPopup();
			infoPopup.setStyleName(FIVIEWPORTRESOURCES.getCSS().popup());
			infoPopup.hide();
		}
	}
	
	private void initHandlers() {        
		eventBus.addHandler(AnalysisProfileChangedEvent.TYPE, this);
		eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
		eventBus.addHandler(CytoscapeLayoutChangedEvent.TYPE, this);
		eventBus.addHandler(FireGraphObjectSelectedEvent.TYPE, this);
	}

	@Override
	public void fitDiagram(boolean animation) { 
		cy.fitCytoscape();
	}

	@Override
	public void zoomDelta(double deltaFactor) {/* Nothing Here */}

	@Override
	public void zoomIn() {
		cy.zoomCytoscape(1);
	}

	@Override
	public void zoomOut() {
		cy.zoomCytoscape(-1);
	}

	@Override
	public void padding(int dX, int dY) { 
		if(dX == 10)
			cy.panLeft(dX);
		else if(dX == -10)
			cy.panRight(dX);
		else if (dY == 10)
			cy.panUp(dY);
		else if(dY == -10)
			cy.panDown(dY);
	}

	@Override
	public void exportView() {
		// TODO Auto-generated method stub
		
	}

  @Override
    protected void onLoad() {
        super.onLoad();
        Scheduler.get().scheduleDeferred(() -> initialise());
    }
	
	@Override
	public void setContext(Context context) {
		this.context = context;
		Content content = context.getContent();
//		if(!cytoscapeInitialised) {
//			cy.cytoscapeInit(((FIViewContent)content).getProteinArray(), 
//							 ((FIViewContent)content).getFIArray(),  
//							 "cose", 
//							 "cy"); // sets the container to use
//			cytoscapeInitialised = true;
//		}
//		else if(cytoscapeInitialised) {
//			cy.clearCytoscapeGraph();
//			cy.addCytoscapeNodes(((FIViewContent)content).getProteinArray());
//			cy.addCytoscapeEdge(((FIViewContent)content).getFIArray());
//		}
		
		if(!cytoscapeInitialised) {
			cy.cytoscapeInit(new JSONArray().toString(), new JSONArray().toString(), "cose", "cy");
			cytoscapeInitialised = true;
		}
		cy.clearCytoscapeGraph();
		cy.addCytoscapeNodes(((FIViewContent)content).getProteinArray());
		cy.addCytoscapeEdge(((FIViewContent)content).getFIArray());
		
		cy.setCytoscapeLayout("cose");
		eventBus.fireEventFromSource(new FIViewMessageEvent(false), this);
	}

	@Override
	public void onNodeClicked(String id, String name) {
		infoPopup.hide();		
	}
	
	@Override
	public void onNodeHovered(String id, String name, int x, int y) {
		infoPopup.hide();
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines(name + " (" + id + ")")
				.toSafeHtml());
		html.setStyleName(FIVIEWPORTRESOURCES.getCSS().label());
		infoPopup.setHtmlLabel(html);
		infoPopup.setPopupPosition(x+10, y+10);
		infoPopup.show();
	}

	@Override
	public void onEdgeMouseOut() {
		infoPopup.hide();
		eventBus.fireEventFromSource(new GraphObjectHoveredEvent(), this);
		
		if(dataOverlay != null && !dataOverlay.isDiscrete())
			eventBus.fireEventFromSource(new FIViewOverlayEdgeHoveredEvent(new ArrayList<>()), this);
	}

	@Override
	public void onEdgeHovered(String id, int x, int y) {
		JSONObject fi = ((FIViewContent)context.getContent()).getFIFromMap(id).get("data").isObject();
		
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines( 
									 fi.get("source") + " - " +
									 fi.get("target"))
				.toSafeHtml());
		html.setStyleName(FIVIEWPORTRESOURCES.getCSS().label());
		infoPopup.setHtmlLabel(html);
		infoPopup.setPopupPosition(x+10, y+10);
		infoPopup.show();
		
		//set edgeHoveredFlag to true
		edgeHoveredFlag = true;
		
		//Fire GraphObjectSelectedEvent
		Long dbId= Long.parseLong(sortGraphObject(fi.get("reactomeSources")));
		GraphObject graphObject = context.getContent().getDatabaseObject(dbId);
		eventBus.fireEventFromSource(new GraphObjectHoveredEvent(graphObject),  this);
		
		if(dataOverlay != null && !dataOverlay.isDiscrete())
			eventBus.fireEventFromSource(new FIViewOverlayEdgeHoveredEvent(getNodeExpression(fi)), this);
	}

	@Override
	public void onEdgeClicked(String id) {
		infoPopup.hide();
		
		JSONObject fi = ((FIViewContent)context.getContent()).getFIFromMap(id).get("data").isObject();
		
		//set cytoscapeClickedFlag to true
		cytoscapeClickedFlag = true;
		
		//Fire GraphObjectSelectedEvent
		Long dbId= Long.parseLong(sortGraphObject(fi.get("reactomeSources")));
		GraphObject graphObject = context.getContent().getDatabaseObject(dbId);
		eventBus.fireEventFromSource(new GraphObjectSelectedEvent(graphObject, false),  this);
		
		if(dataOverlay != null && !dataOverlay.isDiscrete()) {
			eventBus.fireEventFromSource(new FIViewOverlayEdgeSelectedEvent(getNodeExpression(fi)), this);
		}
	}
	
	private List<Double> getNodeExpression(JSONObject fi) {
		List<Double> expression = new ArrayList<>();
		expression.add(dataOverlay.getIdentifierValueMap().get(fi.get("source").isString().stringValue()));
		expression.add(dataOverlay.getIdentifierValueMap().get(fi.get("target").isString().stringValue()));
		expression.removeAll(Collections.singleton(null));
		return expression;
	}
	
	@Override
	public void onCytoscapeCoreContextEvent(int x, int y) {
		fILayoutChangerPanel.show();
		setPopupLocation(x, y, fILayoutChangerPanel);
	}

	@Override
	public void onEdgeContextSelectEvent(String id, int x, int y) {
		JSONObject fi = ((FIViewContent)context.getContent()).getFIFromMap(id).get("data").isObject();
		edgeContextPanel.updateContext(fi);
		edgeContextPanel.show();
		setPopupLocation(x, y, edgeContextPanel);
	}
	
	@Override
	public void onNodeContextSelectEvent(String id, String name, int x, int y) {
		if(nodeContextPanelMap.containsKey(id)) {
			//show popup before setting location so sizing is correct. UI doesn't update fast enough to cause an artifact
			nodeContextPanelMap.get(id).show();
			setPopupLocation(x, y, nodeContextPanelMap.get(id)); 
			return;
		}
		
		NodeContextPanel nodeContextPanel;
		
		//Send overlay value to context panel if dataOverlay exists
		if(dataOverlay != null && dataOverlay.getUniprotToEntitiesMap().containsKey(id))
			nodeContextPanel = new NodeContextPanel(eventBus, name, id, dataOverlay);
		else 
			nodeContextPanel = new NodeContextPanel(eventBus, name, id);
		setPopupLocation(x, y, nodeContextPanel);
		
		//cache so it doesn't have to be recreated every time
		nodeContextPanelMap.put(id, nodeContextPanel);
	}
	
	/**
	 * Sets location of context popups
	 * @param eventX
	 * @param eventY
	 * @param panel
	 */
	private void setPopupLocation(int eventX, int eventY, DialogBox panel) {
		eventX +=5;
		eventY +=5;
		if((this.getOffsetHeight() + this.getElement().getAbsoluteTop() - 35) < (eventY + panel.getOffsetHeight()))
			eventY = eventY - panel.getOffsetHeight()-10;
		if((this.getOffsetWidth() + this.getElement().getAbsoluteLeft()) < (eventX + panel.getOffsetWidth()))
			eventX = eventX - panel.getOffsetWidth()-10;
		
		panel.setPopupPosition(eventX, eventY);
		
	}
	
	@Override
	public void onFireGraphObjectSelected(FireGraphObjectSelectedEvent event) {
		//set cytoscapeClickedFlag to true
		cytoscapeClickedFlag = true;
		
		GraphObject graphObject = context.getContent().getDatabaseObject(event.getReactomeId());
		eventBus.fireEventFromSource(new GraphObjectSelectedEvent(graphObject, false), this);
	}
	
	@Override
	public void onCytoscapeCoreSelectedEvent() {
		cytoscapeClickedFlag = true;
		eventBus.fireEventFromSource(new GraphObjectSelectedEvent(null,false), this);
		if(dataOverlay != null && !dataOverlay.isDiscrete()) 
			eventBus.fireEventFromSource(new FIViewOverlayEdgeHoveredEvent(new ArrayList<>()), this);
	}

	protected String getAnnotationDirection(JSONObject fi) {
		if(fi.get("direction") == null)
			return "-";
		else
			return fi.get("direction").isString().stringValue();
		
	}
	
	@Override
	public void onNodeMouseOut() {
		infoPopup.hide();
	}
	
	@Override
	public void resetContext() {
		this.context = null;
		nodeContextPanelMap.clear();
		cytoscapeInitialised = false;
	}
	
	@Override
	public void contentLoaded(Context context) {
		setContext(context);
	}

	@Override
	public void contentRequested() {
		context = null;
		cy.clearCytoscapeGraph();
	}
	
	/**
	 * recieves a set of reactomeSources from an edge hovered or edge clicked event and sorts it.
	 * Sorting preferences returning the reactomeId of the source with the lowest reactomeId and source type of "Reaction."
	 * If no reaction exists in a set of sourcesFlowPanel, the lowest reactomeId with source type of "Complex" will be returned.
	 * If no source type exists on any of the passed in sourcesFlowPanel, it will return the lowest reactomeId present.
	 * @param reactomeSources
	 * @return
	 */
	private String sortGraphObject(JSONValue reactomeSources) {

		List<JSONObject> objList = new ArrayList<>();
		
		JSONArray jsonArray = reactomeSources.isArray();
		
		//parse over jsonArray, convert each source to a FIEntityNode, and adds to a FIEntityNode array list
		if(jsonArray != null) {
			for(int i=0; i<jsonArray.size(); i++) {
				JSONObject obj = jsonArray.get(i).isObject();
				objList.add(obj);
			}
		}
		
		//return dbId of the single source passed in
		if(objList.isEmpty()) {
			JSONObject obj = reactomeSources.isObject();
			return obj.get("reactomeId").isString().stringValue();
		}
			
		//Sorts sourcesList by dbId
		Collections.sort(objList, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				return Long.compare(Long.parseLong(o1.get("reactomeId").isString().stringValue()), Long.parseLong(o2.get("reactomeId").isString().stringValue()));
			}
		});
		
		//Sends first reaction when iterating over array from low to high DbId
		for (JSONObject obj : objList) {
			if (obj.get("sourceType").isString().toString().toUpperCase().contentEquals("REACTION"));
				return obj.get("reactomeId").isString().stringValue();
		}
		
		//If no obj in objList has a sourceType, send first entry, which will have lowest DbId after sorting above.
		return objList.get(0).get("reactomeId").isString().stringValue();
	}

	@Override
	public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
		
		//ensure passed in graph object exists.
		if(graphObject == null)
			return false;
		
		//check if call initiated with onEdgeHovered. if so, return
		if(edgeHoveredFlag) {
			edgeHoveredFlag = false;
			return true;
		}
		
		cy.hierarchyHover("reactomeId", graphObject.getDbId().toString());
		return true;
	}

	@Override
	public void highlightInteractor(DiagramInteractor diagramInteractor) {/* Nothing Here */}

	@Override
	public boolean resetHighlight(boolean notify) {
		cy.removeEdgeClass("hovered");
		return true;
	}

	@Override
	public boolean resetSelection(boolean notify) {
		if(context==null) return false;
		
		cy.removeClass("highlighted");
		
		if(notify) {
			eventBus.fireEventFromSource(new GraphObjectSelectedEvent(null, false), this);
		}
		return true;
	}

	@Override
	public boolean selectGraphObject(GraphObject graphObject, boolean notify) {
		
		//ensure passed in graph object exists. If not, return false.
		if(graphObject ==  null)
			return false;

		//check if call initiated with onEdgeClicked. if so, return
		if(cytoscapeClickedFlag) {
			cytoscapeClickedFlag = false;
			return true;
		}
		
		cy.hierarchySelect("reactomeId", graphObject.getDbId().toString());
		this.selected = graphObject;
		return true;
	}

	@Override
	public GraphObject getSelected() {
		return selected;
	}

	@Override
	public void loadAnalysis() {
		//analysisStatus set in context when onAnalysisLoaded in DiagramViewerImpl is ran
		analysisStatus = context.getAnalysisStatus();
        Double minExp = 0.0; Double maxExp = 0.0;
        AnalysisType analysisType = AnalysisType.NONE;
        if(cy!=null && analysisStatus != null) {
        	analysisType = AnalysisType.getType(analysisStatus.getAnalysisSummary().getType());
        	cy.resetStyle();
            //setup lighter node color
            cy.setNodeFill(InteractorColours.get().PROFILE.getProtein().getLighterFill());
        	if(analysisStatus.getExpressionSummary()!=null) {
        		minExp = analysisStatus.getExpressionSummary().getMin();
        		maxExp = analysisStatus.getExpressionSummary().getMax();
        	}
        }
        
        //make list of entities to be highlighted
        List<GraphObject> entities = new LinkedList<>();
        for(GraphObject identifier : context.getContent().getIdentifierMap().values()) {
        	if(identifier instanceof GraphPhysicalEntity) {
        		if(((GraphPhysicalEntity) identifier).isHit()) {
        			entities.add(identifier);
        		}
        	}
        }
        
        //render entities based on analysis type
        for(GraphObject entity : entities) {
        	switch(analysisType) {
        	case NONE:
        	case SPECIES_COMPARISON:
        	case OVERREPRESENTATION:
        		drawAnalysisEnrichmentNode(entity);
        		break;
        	case EXPRESSION:
        	case GSVA:
        	case GSA_STATISTICS:
        		drawAnalysisExpressionNode(entity, minExp, maxExp);
        		break;
        	case GSA_REGULATION:
        		drawAnalysisRegulationNode(entity, minExp);
        	}
        }
	}

	@Override
	public void resetAnalysis() {
		analysisStatus = null;
        selectedExpCol = 0;
        if(cy != null) {
        	cy.resetStyle();
        }
	}

	//highlight enrichment node
	private void drawAnalysisEnrichmentNode(GraphObject entity) {
		cy.highlightNode(((GraphPhysicalEntity)entity).getIdentifier(),
				AnalysisColours.get().PROFILE.getEnrichment().getGradient().getMax(), "1");
	}
	
	//highlight expression of a node
	private void drawAnalysisExpressionNode(GraphObject entity, Double minExp, Double maxExp) {
		String color = getExpressionColor(((GraphPhysicalEntity)entity).getExpression(), minExp, maxExp);
		cy.highlightNode(((GraphPhysicalEntity)entity).getIdentifier(), color, "1");
	}
	
	//highlight regulated node
	private void drawAnalysisRegulationNode(GraphObject entity, Double minExp) {
		String color = getRegulationColor(((GraphPhysicalEntity)entity).getExpression(), minExp);
		cy.highlightNode(((GraphPhysicalEntity)entity).getIdentifier(), color, "1");
	}
	
	//get node color for a given expression
	private String getExpressionColor(List<Double> exp, Double minExp, Double maxExp) {
		double value = minExp;
		if(exp != null)
			value = exp.get(selectedExpCol);
		return AnalysisColours.get().expressionGradient.getColor(value, minExp, maxExp);
	}
	
	//get node color for given regulation
	private String getRegulationColor(List<Double> exp, Double minExp) {
		double value = minExp;
		if(exp != null)
			value = exp.get(selectedExpCol);
		return AnalysisColours.get().regulationColorMap.getColor((int)value);
	}
	
	@Override
	public void expressionColumnChanged() {
		loadAnalysis();
	}

	@Override
	public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
		this.selectedExpCol = e.getColumn();
	}
	
	@Override
	public void interactorsCollapsed(String resource) {/* Nothing Here */}

	@Override
	public void interactorsFiltered() {/* Nothing Here */}

	@Override
	public void interactorsLayoutUpdated() {/* Nothing Here */}

	@Override
	public void interactorsLoaded() {/* Nothing Here */}

	@Override
	public void interactorsResourceChanged(OverlayResource resource) {/* Nothing Here */}

	@Override
	public void setSize(int width, int height) {
				
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		this.cyView.setWidth(width + "px");
		this.cyView.setHeight(height + "px");
		this.viewportWidth = width;
		this.viewportHeight = height;
		//TODO: make so center happens on resize
//		cy.centerCytoscape(); action lags until next resize
	}

	@Override
	public void flagItems(Set<DiagramObject> flaggedItems, Boolean includeInteractors) {
		resetFlag();
		for(DiagramObject diagramObject : flaggedItems) {
			if(diagramObject.getIsDisease())
				continue;
			if(diagramObject.getSchemaClass() == "EntityWithAccessionedSequence")
				cy.addNodeClass("name", diagramObject.getDisplayName(), "flagged");
		}
	}
	
	@Override
	public void onAnalysisProfileChanged(AnalysisProfileChangedEvent event) {
		loadAnalysis();
	}
	
	@Override
	public void onCytoscapeLayoutChanged(CytoscapeLayoutChangedEvent event) {
		cy.setCytoscapeLayout(event.getType().toString().toLowerCase());
	}

	@Override
	public void resetFlag() {
		cy.removeNodeClass("flagged");
	}
	
	/**
	 * Directs overlay of TCRD data onto the Cytoscape. FI view by re-coloring nodes
	 * @param dataOverlay
	 */
	public void overlayNodes(DataOverlay dataOverlay) {
		cy.resetStyle();
		cy.resetSelection();
		this.dataOverlay = dataOverlay;
		if(dataOverlay == null) {
			nodeContextPanelMap.clear();
			return;
		}
        this.dataOverlay.updateIdentifierValueMap();
		
		if(dataOverlay.isDiscrete())
			overlayDiscreteData();
		else if(!dataOverlay.isDiscrete())
			overlayContinuousData();
	}

	/**
	 * Renders overlay for continuous expression data from TCRD server
	 * @param dataOverlay
	 */
	private void overlayContinuousData() {
		ThreeColorGradient gradient = AnalysisColours.get().expressionGradient;
		dataOverlay.getIdentifierValueMap().forEach((v,k) -> {
			String color = gradient.getColor(k,dataOverlay.getMinValue(),dataOverlay.getMaxValue());
			cy.highlightNode(v, color, ".8");
		});
	}

	/**
	 * Renders overlay for discrete expression data from TCRD server 
	 * @param dataOverlay
	 */
	private void overlayDiscreteData() {
		Map<Double, String> colourMap = OverlayColours.get().getColours();
		dataOverlay.getIdentifierValueMap().forEach((v,k) -> {
			String color = colourMap.get(new Double(k));
			cy.highlightNode(v, color, ".8");

		});
	}
	
	/**
	 * Everything below here is for resource loading for the cytoscape view button.
	 */
    public static FIViewportResources FIVIEWPORTRESOURCES;
    static {
        FIVIEWPORTRESOURCES = GWT.create(FIViewportResources.class);
        FIVIEWPORTRESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface FIViewportResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source("org/reactome/web/fi/client/visualisers/fiview/cytoscape-style.json")
        public TextResource fiviewStyle();
        
        @Source("org/reactome/web/fi/client/visualisers/fiview/cytoscape.umd.js")
        public TextResource cytoscapeLibrary();
       
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }
    
    @CssResource.ImportedWithPrefix("fIViewVisualiser")
    public interface ResourceCSS extends CssResource{
    	String CSS = "org/reactome/web/fi/client/visualisers/fiview/FIVisualiser.css";
    	
    	String popup();
    	
    	String label();
    }
}
