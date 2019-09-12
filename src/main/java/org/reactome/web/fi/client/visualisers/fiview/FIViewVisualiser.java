package org.reactome.web.fi.client.visualisers.fiview;

import java.util.Set;

import org.reactome.web.diagram.client.visualisers.Visualiser;
import org.reactome.web.diagram.data.Context;
import org.reactome.web.diagram.data.content.Content;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.diagram.data.interactors.model.DiagramInteractor;
import org.reactome.web.diagram.data.layout.DiagramObject;
import org.reactome.web.fi.data.content.FIViewContent;
import org.reactome.web.fi.events.EdgeClickedEvent;
import org.reactome.web.fi.events.EdgeHoveredEvent;
import org.reactome.web.fi.events.EdgeMouseOutEvent;
import org.reactome.web.fi.events.NodeClickedEvent;
import org.reactome.web.fi.handlers.EdgeClickedHandler;
import org.reactome.web.fi.handlers.EdgeHoveredHandler;
import org.reactome.web.fi.handlers.EdgeMouseOutHandler;
import org.reactome.web.fi.handlers.NodeClickedHandler;
import org.reactome.web.fi.client.visualisers.fiview.FIViewInfoPopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewVisualiser extends AbsolutePanel implements Visualiser,
	EdgeClickedHandler, EdgeHoveredHandler, EdgeMouseOutHandler, NodeClickedHandler{
	
	private EventBus eventBus;
	private CytoscapeEntity cy;
	
	private Context context;
	
	private boolean initialised;
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
			
			cy = new CytoscapeEntity(this.eventBus);
			
			SimplePanel cyView =  new SimplePanel();
			cyView.getElement().setId("cy");
			cyView.setSize("100%", "100%");
			
			this.add(cyView);
			
			this.viewportWidth = getParent().getOffsetWidth();
			this.viewportHeight = getParent().getOffsetHeight();
			this.setWidth(viewportWidth + "px");
			this.setHeight(viewportHeight+ "px");
			
			infoPopup = new FIViewInfoPopup();
			
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
		cy.cytoscapeInit(((FIViewContent)content).getProteinArray(), 
						 ((FIViewContent)content).getFIArray(), 
						 FIVIEWPORTRESOURCES.fiviewStyle().getText(), 
						 "cose");
		
	}

	@Override
	public void onNodeClicked(NodeClickedEvent event) {
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
	}

	@Override
	public void onEdgeClicked(EdgeClickedEvent event) {
		infoPopup.hide();
		HTML html = new HTML(new SafeHtmlBuilder()
				.appendEscapedLines("Protein One Name: " + event.getSourceName() + "\n"
									+ "Protein Two Name: " + event.getTargetName() + "\n"
									+ "Interaction Direction: " + event.getDirection() + "\n"
									+ "Reactome Sources Id List: " + event.getReactomeSources())
				.toSafeHtml());
		infoPopup.setHtmlLabel(html);
		infoPopup.show();
		
	}
	
	@Override
	public void resetContext() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void contentLoaded(Context context) {
		setContext(context);
	}

	@Override
	public void contentRequested() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GraphObject getSelected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadAnalysis() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetAnalysis() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetFlag() {
		// TODO Auto-generated method stub
		
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


    }
}
