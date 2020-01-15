package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.diagram.events.PairwiseOverlayButtonClickedEvent;
import org.reactome.web.diagram.handlers.PairwiseOverlayButtonClickedHandler;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.events.PairwiseDataLoadedEvent;
import org.reactome.web.fi.handlers.PairwiseDataLoadedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PairwisePopout extends PopupPanel implements ResizeHandler, PairwiseOverlayButtonClickedHandler, PairwiseDataLoadedHandler, CytoscapeEntity.Handler{

	private EventBus eventBus;
	private CytoscapeEntity cy;
	private SimplePanel cyView;
	private boolean initialized = false;
	
	private JSONArray currentNodeArray;
	private JSONArray currentEdgeArray;
	
	private List<PairwiseEntity> currentPairwiseOverlay;
	
	public PairwisePopout(EventBus eventBus) {
		this.eventBus = eventBus;
		currentPairwiseOverlay = new ArrayList<>();
		this.setStyleName(RESOURCES.getCSS().popupPanel());
		
		this.cy = new CytoscapeEntity(eventBus, RESOURCES.fiviewStyle().getText(), this);
		initPanel();
		
		eventBus.addHandler(PairwiseOverlayButtonClickedEvent.TYPE, this);
		eventBus.addHandler(PairwiseDataLoadedEvent.TYPE, this);
	}

	private void initPanel() {
		this.setAutoHideEnabled(true);
		this.setModal(true);
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setAutoHideOnHistoryEventsEnabled(false);
		Window.addResizeHandler(this);
		
		int width = (int) Math.round(Window.getClientWidth() * 0.5);
		int height = (int) Math.round(Window.getClientHeight() * 0.5);
		this.setWidth(width + "px");
		this.setHeight(height + "px");
		
		FlowPanel container = new FlowPanel();
		container.setStyleName(RESOURCES.getCSS().container());
		container.add(setTitlePanel());
		
		FlowPanel innerContainer = new FlowPanel();
		innerContainer.setStyleName(RESOURCES.getCSS().innerContainer());
		
		cyView = new SimplePanel();
		cyView.getElement().setId("cy-popout");
		cyView.setStyleName(RESOURCES.getCSS().cyView());
		innerContainer.add(cyView);
		
		FlowPanel overlayInfo = new FlowPanel();
		overlayInfo.setStyleName(RESOURCES.getCSS().overlayInfo());
		innerContainer.add(overlayInfo);
		
		container.add(innerContainer);
		this.add(container);
	}
	
	private FlowPanel setTitlePanel() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().header());
		result.addStyleName(RESOURCES.getCSS().unselectable());
		InlineLabel title = new InlineLabel("Pairwise Relationship");
		result.add(title);
		result.add(getCloseButton());
		return result;
	}

	private Button getCloseButton() {
		Button result = new Button();
		result.setStyleName(RESOURCES.getCSS().close());
		result.setTitle("Close Pairwise View");
		result.addClickHandler(e -> super.hide());
		return result;
	}
	
	@Override
	public void onResize(ResizeEvent event) {
		if(isVisible()){
            int width = (int) Math.round(RootLayoutPanel.get().getOffsetWidth() * 0.5);
            int height = (int) Math.round(RootLayoutPanel.get().getOffsetHeight() * 0.5);
            this.setWidth(width + "px");
            this.setHeight(height + "px");
        }
	}
	
	@Override
	public void onPairwiseOverlayButtonClicked(PairwiseOverlayButtonClickedEvent event) {
		if(currentPairwiseOverlay.size()==0) {
			return;
		}
		
		currentNodeArray = new JSONArray();
		
		if(event.getGraphObject() != null) {
			constructFIs(event.getGraphObject());
		}
		else if(event.getGeneName() != null) {
			constructFIs(event.getGeneName());
		}
		
		updateView();
		
		this.show();
	}

	private void updateView() {
		if(!initialized) {
			cy.cytoscapeInit(currentNodeArray.toString(),
							 currentEdgeArray.toString(),
							 "cose",
							 "cy-popout",
							 false);
			cy.setCytoscapeLayout("cose");
			initialized = true;
		}
		else {
			cy.clearCytoscapeGraph();
			cy.addCytoscapeNodes(currentNodeArray.toString());
			cy.addCytoscapeEdge(currentEdgeArray.toString());
			cy.setCytoscapeLayout("cose");
		}

	}

	private void constructFIs(String uniprot) {
//		JSONArray nodeArr = new JSONArray();
//		nodeArr.set(nodeArr.size(), getProtein())
	}

	private void constructFIs(GraphObject graphObject) {
		JSONArray nodeArr = new JSONArray();
		JSONArray edgeArr = new JSONArray();
		if(graphObject instanceof GraphComplex) {
			GraphComplex complex = (GraphComplex) graphObject;
			List<GraphPhysicalEntity> entities = new ArrayList<>();
			entities.addAll(complex.getParticipants());
			for(int i=0; i<entities.size(); i++) {
				nodeArr.set(nodeArr.size(), getProtein(entities.get(i)));
				for(int j=i+1; j<entities.size(); j++) {
					edgeArr.set(edgeArr.size(), makeFI(edgeArr.size(), entities.get(i).getIdentifier(), entities.get(j).getIdentifier()));
				}
				for(PairwiseEntity entity: currentPairwiseOverlay) {
					if(entities.get(i).getDisplayName().contains(entity.getGene())) {
						if(entity.getPosGenes()!= null && entity.getPosGenes().size() > 0)
							for(int k=0; k<10; k++)
								nodeArr.set(nodeArr.size(), getProtein(entity.getPosGenes().get(k)));
					}
				}
					
			}
		}
		currentNodeArray = nodeArr;
		currentEdgeArray = edgeArr;
	}

	private JSONValue makeFI(int size, String source, String target) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("edges"));
		
		JSONObject edge = new JSONObject();
		edge.put("id", new JSONString(size+""));
		edge.put("source", new JSONString(source));
		edge.put("target", new JSONString(target));
		edge.put("direction", new JSONString("-"));
		
		result.put("data", edge);
		return result;
	}

	private JSONValue getProtein(GraphPhysicalEntity entity) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		node.put("id", new JSONString(entity.getIdentifier()));
		node.put("name", new JSONString(entity.getDisplayName()));
		
		result.put("data", node);
		
		return result;
	}
	
	private JSONValue getProtein(String gene) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		node.put("id", new JSONString(gene));
		node.put("name", new JSONString(gene));
		
		result.put("data", node);
		return result;
	}
	
	@Override
	public void onPairwisieDataLoaded(PairwiseDataLoadedEvent event) {
		currentPairwiseOverlay = event.getEntities();
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
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/PairwisePopout.css";
		
		String popupPanel();
		
		String container();
		
		String cyView();
		
		String header();
		
		String unselectable();
		
		String close();
		
		String innerContainer();
		
		String overlayInfo();
	}
}
