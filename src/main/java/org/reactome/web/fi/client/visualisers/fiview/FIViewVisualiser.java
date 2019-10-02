package org.reactome.web.fi.client.visualisers.fiview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.GraphObjectFactory;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.fi.data.content.FIViewContent;
import org.reactome.web.gwtCytoscapeJs.events.EdgeClickedEvent;
import org.reactome.web.gwtCytoscapeJs.events.EdgeHoveredEvent;
import org.reactome.web.gwtCytoscapeJs.events.EdgeMouseOutEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeClickedEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeHoveredEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeMouseOutEvent;
import org.reactome.web.gwtCytoscapeJs.handlers.EdgeClickedHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.EdgeHoveredHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.EdgeMouseOutHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.NodeClickedHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.NodeHoveredHandler;
import org.reactome.web.gwtCytoscapeJs.handlers.NodeMouseOutHandler;
import org.reactome.web.fi.client.visualisers.fiview.FIViewInfoPopup;
import org.reactome.web.fi.data.model.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewVisualiser extends SimplePanel implements Visualiser,
	EdgeClickedHandler, EdgeHoveredHandler, EdgeMouseOutHandler, NodeClickedHandler,
	NodeHoveredHandler, NodeMouseOutHandler{
	
	private EventBus eventBus;
	private CytoscapeEntity cy;
	
	private Context context;
	
	private AnalysisStatus analysisStatus;
    private ExpressionSummary expressionSummary;
    private int selectedExpCol = 0;
	
	private boolean initialised = false;
	private boolean cytoscapeInitialised;
	private boolean edgeClickedFlag;
	private boolean edgeHoveredFlag;
    private int viewportWidth = 0;
    private int viewportHeight = 0;
	private FIViewInfoPopup infoPopup;
	
	SimplePanel cyView;
    
	public FIViewVisualiser(EventBus eventBus) {
		super();
		this.getElement().addClassName("pwp-FIViz"); //IMPORTANT!
		this.eventBus = eventBus;
		
		cyView =  new SimplePanel();
		
		initHandlers();
		
		//default this value to false
		edgeClickedFlag = false;
		edgeHoveredFlag = false;
	}
	
	protected void initialise() {
		if(!initialised) {
			this.initialised = true;
			this.viewportWidth = getParent().getOffsetWidth();
			this.viewportHeight = getParent().getOffsetHeight();
			ScriptInjector.fromString(FIVIEWPORTRESOURCES.cytoscapeLibrary().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
			
			cy = new CytoscapeEntity(this.eventBus, FIVIEWPORTRESOURCES.fiviewStyle().getText());
			
			cyView.getElement().setId("cy");
			
			this.add(cyView);
			this.cyView.setSize(viewportWidth+"px", viewportHeight+"px");
			
			infoPopup = new FIViewInfoPopup();
			infoPopup.getElement().setId("FIVIZ-info-popup");
			infoPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					int left = (305 - offsetWidth);
					int top = (110 - getOffsetHeight() - offsetHeight);
					infoPopup.setPopupPosition(left, top);
				}
			});
			infoPopup.hide();
			
			setSize(viewportWidth, viewportHeight);
			
			cytoscapeInitialised = false;
			
		}
	}
	
	private void initHandlers() {
		eventBus.addHandler(EdgeClickedEvent.TYPE, this);
		eventBus.addHandler(EdgeHoveredEvent.TYPE, this);
		eventBus.addHandler(EdgeMouseOutEvent.TYPE, this);
		eventBus.addHandler(NodeClickedEvent.TYPE, this);
		
	}

	@Override
	public void fitDiagram(boolean animation) { 
		cy.fitCytoscape();
	}

	@Override
	public void zoomDelta(double deltaFactor) {
		// TODO Auto-generated method stub
		
	}

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
		if(!cytoscapeInitialised) {
			cy.cytoscapeInit(((FIViewContent)content).getProteinArray(), 
							 ((FIViewContent)content).getFIArray(),  
							 "cose");
			cy.resetCytoscapeLayout();
			cytoscapeInitialised = true;
		}
		else if(cytoscapeInitialised) {
			cy.clearCytoscapeGraph();
			cy.addCytoscapeNodes(((FIViewContent)content).getProteinArray());
			cy.addCytoscapeEdge(((FIViewContent)content).getFIArray());
			cy.resetSelection();
			cy.resetCytoscapeLayout();
		}
	}

	@Override
	public void onNodeClicked(NodeClickedEvent event) {
				
		infoPopup.hide();
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Protein Short Name: " + event.getName() +
									"\n" +
									"Protein Accession: " + event.getNodeId())
				.toSafeHtml());
		infoPopup.setHtmlLabel(html);
		infoPopup.show();		
	}
	
	@Override
	public void onNodeHovered(NodeHoveredEvent event) {
		infoPopup.hide();
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Node Accession number: " + event.getNodeId())
				.toSafeHtml());
		infoPopup.setHtmlLabel(html);
		infoPopup.show();
	}

	@Override
	public void onEdgeMouseOut(EdgeMouseOutEvent event) {
		infoPopup.hide();
		
	}

	@Override
	public void onEdgeHovered(EdgeHoveredEvent event) {
		
		JSONObject fi = ((FIViewContent)context.getContent()).getFIFromMap(event.getEdgeId()).get("data").isObject();
		
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Functional interaction: " + 
									 fi.get("source") + " " +
									 fi.get("annotationDirection") + " " +
									 fi.get("target"))
				.toSafeHtml());
		infoPopup.setHtmlLabel(html);
		infoPopup.show();
		
		//set edgeHoveredFlag to true
		edgeHoveredFlag = true;
		
		//Fire GraphObjectSelectedEvent
		String dbId= sortGraphObject(fi.get("reactomeSources"));
		GraphObject graphObject = context.getContent().getDatabaseObject(dbId);
		eventBus.fireEventFromSource(new GraphObjectHoveredEvent(graphObject),  this);
		
	}

	@Override
	public void onEdgeClicked(EdgeClickedEvent event) {
		cy.resetSelection();
		infoPopup.hide();
		
		JSONObject fi = ((FIViewContent)context.getContent()).getFIFromMap(event.getEdgeId()).get("data").isObject();
		
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Protein One Name: " + fi.get("source") + "\n"
									+ "Interaction Direction: " + fi.get("annotationDirection") + "\n"
									+ "Protein Two Name: " + fi.get("target"))
				.toSafeHtml());
		infoPopup.setHtmlLabel(html);
		infoPopup.show();
		
		//set edgeClickedFlag to true
		edgeClickedFlag = true;
		
		//Fire GraphObjectSelectedEvent
		String dbId= sortGraphObject(fi.get("reactomeSources"));
		GraphObject graphObject = context.getContent().getDatabaseObject(dbId);
		eventBus.fireEventFromSource(new GraphObjectSelectedEvent(graphObject, false),  this);
		
		
	}
	
	@Override
	public void onNodeMouseOut(NodeMouseOutEvent event) {
		infoPopup.hide();
	}
	
	@Override
	public void resetContext() {
		this.context = null;
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
		
		//Sends first COMPLEX when iterating over array from low to high DbId if reaction hasn't been returned
		for(JSONObject obj: objList) {
			if(obj.get("sourceType").isString().toString().toUpperCase().contentEquals("COMPLEX"))
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
	public void highlightInteractor(DiagramInteractor diagramInteractor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean resetHighlight(boolean notify) {
		cy.resetSelection();
		return true;
	}

	@Override
	public boolean resetSelection(boolean notify) {
		cy.resetSelection();
		return true;
	}

	@Override
	public boolean selectGraphObject(GraphObject graphObject, boolean notify) {
		
		//ensure passed in graph object exists. If not, return false.
		if(graphObject ==  null)
			return false;

		//check if call initiated with onEdgeClicked. if so, return
		if(edgeClickedFlag) {
			edgeClickedFlag = false;
			return true;
		}
		
		cy.hierarchySelect("reactomeId", graphObject.getDbId().toString());
		return true;
	}

	@Override
	public GraphObject getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadAnalysis() {
		analysisStatus = context.getAnalysisStatus();
        expressionSummary = analysisStatus.getExpressionSummary();
        selectedExpCol = 0;
	}

	@Override
	public void resetAnalysis() {
		analysisStatus = null;
        expressionSummary = null;
        selectedExpCol = 0;
	}

	@Override
	public void expressionColumnChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsCollapsed(String resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsFiltered() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsLayoutUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsLoaded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interactorsResourceChanged(OverlayResource resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSize(int width, int height) {
				
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		this.cyView.setWidth(width + "px");
		this.cyView.setHeight(height + "px");
		this.viewportWidth = width;
		this.viewportHeight = height;
//		cy.centerCytoscape(); action lags until next resize
		
	}

	@Override
	public void flagItems(Set<DiagramObject> flaggedItems, Boolean includeInteractors) {
		resetFlag();
		for(DiagramObject diagramObject : flaggedItems) {
			eventBus.fireEventFromSource(new NodeClickedEvent(diagramObject.getId().toString(), diagramObject.getDisplayName()), this);
		}
		
	}

	@Override
	public void resetFlag() {
		cy.resetSelection();
	}
	
	/**
	 * Everything below here is for resource loading for the cytoscape view button.
	 */
    public static FIViewportResources FIVIEWPORTRESOURCES;
    static {
        FIVIEWPORTRESOURCES = GWT.create(FIViewportResources.class);
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


    }
}
