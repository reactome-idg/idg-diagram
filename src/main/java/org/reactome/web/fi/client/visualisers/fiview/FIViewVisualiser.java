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
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.data.graph.raw.EntityNode;
import org.reactome.web.diagram.data.graph.raw.Graph;
import org.reactome.web.diagram.data.graph.raw.factory.GraphFactory;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.diagram.data.layout.factory.DiagramObjectException;
import org.reactome.web.diagram.events.GraphObjectHoveredEvent;
import org.reactome.web.diagram.events.GraphObjectSelectedEvent;
import org.reactome.web.fi.data.content.FIViewContent;
import org.reactome.web.fi.events.EdgeClickedEvent;
import org.reactome.web.fi.events.EdgeHoveredEvent;
import org.reactome.web.fi.events.EdgeMouseOutEvent;
import org.reactome.web.fi.events.NodeClickedEvent;
import org.reactome.web.fi.handlers.EdgeClickedHandler;
import org.reactome.web.fi.handlers.EdgeHoveredHandler;
import org.reactome.web.fi.handlers.EdgeMouseOutHandler;
import org.reactome.web.fi.handlers.NodeClickedHandler;
import org.reactome.web.gwtCytoscapeJs.events.NodeHoveredEvent;
import org.reactome.web.gwtCytoscapeJs.events.NodeMouseOutEvent;
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
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewVisualiser extends AbsolutePanel implements Visualiser,
	EdgeClickedHandler, EdgeHoveredHandler, EdgeMouseOutHandler, NodeClickedHandler,
	NodeHoveredHandler, NodeMouseOutHandler{
	
	private EventBus eventBus;
	private CytoscapeEntity cy;
	
	private Context context;
	
	private AnalysisStatus analysisStatus;
    private ExpressionSummary expressionSummary;
    private int selectedExpCol = 0;
	
	private boolean initialised;
	private boolean cytoscapeInitialised;
    private int viewportWidth = 0;
    private int viewportHeight = 0;
	private FIViewInfoPopup infoPopup;
    
	public FIViewVisualiser(EventBus eventBus) {
		super();
		this.getElement().addClassName("pwp-FIViz");
		this.eventBus = eventBus;
		
		initHandlers();
	}
	
	protected void initialise() {
		if(!initialised) {
			this.initialised = true;
			
			ScriptInjector.fromString(FIVIEWPORTRESOURCES.cytoscapeLibrary().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
			
			cy = new CytoscapeEntity(this.eventBus, FIVIEWPORTRESOURCES.fiviewStyle().getText());
			
			SimplePanel cyView =  new SimplePanel();
			cyView.getElement().setId("cy");
			cyView.setSize("100%", "100%");
			
			this.add(cyView);
			
			this.viewportWidth = getParent().getOffsetWidth();
			this.viewportHeight = getParent().getOffsetHeight();
			this.setWidth(viewportWidth + "px");
			this.setHeight(viewportHeight + "px");
			
			infoPopup = new FIViewInfoPopup();
			infoPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
				
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					int left = (getParent().getParent().getOffsetWidth() - offsetWidth)/12;
					int top = (getParent().getParent().getOffsetHeight() - offsetHeight)/7;
					infoPopup.setPopupPosition(left, top);
				}
			});
			infoPopup.hide();
			
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
							 FIVIEWPORTRESOURCES.fiviewStyle().getText(), 
							 "cose");
			cytoscapeInitialised = true;
		}
	}

	@Override
	public void onNodeClicked(NodeClickedEvent event) {
		
		cy.highlightSelectedEdgeGroup(event.getNodeId(), FIVIEWPORTRESOURCES.fiviewStyle().getText());
		
		infoPopup.hide();
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Protein Short Name: " + event.getShortName() +
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
		infoPopup.hide();
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Edge interaction: " + event.getInteractionDirection())
				.toSafeHtml());
		infoPopup.setHtmlLabel(html);
		infoPopup.show();
		
		//Fire GraphObjectHoveredEvent
		SourcesEntity source = sortGraphObject(event.getReactomeSources());
		GraphObject graphObject = GraphObjectFactory.getOrCreateDatabaseObject(source);
		eventBus.fireEventFromSource(new GraphObjectHoveredEvent(graphObject), this);
		
	}

	@Override
	public void onEdgeClicked(EdgeClickedEvent event) {
		
		cy.resetSelection();
		
		infoPopup.hide();
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Protein One Name: " + event.getSourceName() + "\n"
									+ "Interaction Direction: " + event.getDirection() + "\n"
									+ "Protein Two Name: " + event.getTargetName())
				.toSafeHtml());
		infoPopup.setHtmlLabel(html);
		infoPopup.show();
		
		//Fire GraphObjectSelectedEvent
		SourcesEntity source = sortGraphObject(event.getReactomeSources());
		GraphObject graphObject = GraphObjectFactory.getOrCreateDatabaseObject(source);
		eventBus.fireEventFromSource(new GraphObjectSelectedEvent(graphObject, false),  this);
		
		
	}
	
	@Override
	public void onMouseOut(NodeMouseOutEvent event) {
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
	
	private SourcesEntity sortGraphObject(String reactomeSources) {

		List<SourcesEntity> sourcesList = new ArrayList<>();
		
		JSONValue value = JSONParser.parseStrict(reactomeSources);
		JSONArray jsonArray = value.isArray();
		
		//parse over jsonArray, convert each source to a SourcesEntity, and adds to a SourcesEntity array list
		if(jsonArray != null) {
			for(int i=0; i<jsonArray.size(); i++) {
				JSONObject obj = jsonArray.get(i).isObject();
				SourcesEntity source = null;
				try {
					source = SourceFactory.getSourceEntity(SourcesEntity.class, obj.toString());
					sourcesList.add(source);
				} catch (DiagramObjectException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(sourcesList.isEmpty()) {
			try {
				SourcesEntity source;
				source = SourceFactory.getSourceEntity(SourcesEntity.class, value.toString());
				return source;
			} catch(DiagramObjectException e) {
				e.printStackTrace();
			}
		}
			
		//Sorts sourcesList by dbId
		Collections.sort(sourcesList, new Comparator<SourcesEntity>() {
			@Override
			public int compare(SourcesEntity o1, SourcesEntity o2) {
				return o1.getDbId().compareTo(o2.getDbId());
			}
		});
		
		//Sends first reaction when iterating over array from low to high DbId
		for (SourcesEntity src : sourcesList) {
			if (src.getSchemaClass().toUpperCase().contentEquals("REACTION"))
				return src;
			
		}
		
		//Sends first COMPLEX when iterating over arroay from low to high DbId if reaction hasnt been returned
		for(SourcesEntity src: sourcesList) {
			if(src.getSchemaClass().toUpperCase().contentEquals("COMPLEX"))
				return src;
		}
		
		//If no SourceEntity has a sourceType of reaction, send first Complex, which will have lowest DbId after sorting above.
		return sourcesList.get(0);
	}

	@Override
	public boolean highlightGraphObject(GraphObject graphObject, boolean notify) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void highlightInteractor(DiagramInteractor diagramInteractor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean resetHighlight(boolean notify) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean resetSelection(boolean notify) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean selectGraphObject(GraphObject graphObject, boolean notify) {
		eventBus.fireEventFromSource(new NodeClickedEvent(graphObject.getDbId().toString(), graphObject.getDisplayName()), this);
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
		// TODO Auto-generated method stub
		
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
        
        @Source("org/reactome/web/fi/client/visualisers/fiview/cytoscape.min.js")
        public TextResource cytoscapeLibrary();


    }
}
