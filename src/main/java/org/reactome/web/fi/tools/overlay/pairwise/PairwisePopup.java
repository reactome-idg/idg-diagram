package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.graph.model.GraphComplex;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.diagram.data.graph.model.GraphPhysicalEntity;
import org.reactome.web.fi.client.visualisers.fiview.CytoscapeEntity;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwisePopupFactory;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;

public class PairwisePopup extends AbstractPairwisePopup {

	private String popupId;
	private String containerId;
	private CytoscapeEntity cy;
	private Boolean cytoscapeInitialized = false;
	
	private FlowPanel main;
	
	private FlowPanel infoPanel;
	
	public PairwisePopup(GraphObject graphObject) {
		this.popupId = graphObject.getStId();
		initPanel();
		loadNetwork(graphObject);
	}

	public PairwisePopup(String uniprot, String geneName) {
		this.popupId = uniprot;
		initPanel();
		loadNetwork(geneName);
	}

	private void initPanel() {
		setStyleName(RESOURCES.getCSS().popupPanel());
		setAutoHideEnabled(false);
		setModal(false);
		
		main = new FlowPanel();
		main.add(new PwpButton("Close", RESOURCES.getCSS().close(), e -> onCloseButtonClicked()));

		main.add(getMainPanel());
		
		setTitlePanel();
		setWidget(main);
				
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
		
		infoPanel = new FlowPanel(); //TODO: Setup info panel
		
		return result;
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

	private void updateView(JSONArray nodeArr, JSONArray edgeArr) {
		if(!cytoscapeInitialized) {
			this.cy = new CytoscapeEntity(RESOURCES.fiviewStyle().getText(), this);
			cy.cytoscapeInit(nodeArr.toString(),edgeArr.toString(), "cose", containerId);
			cytoscapeInitialized = true;
		}
		cy.setCytoscapeLayout("cose");
	}

	private void loadNetwork(GraphObject graphObject) {
		JSONArray nodeArr = new JSONArray();
		JSONArray edgeArr = new JSONArray();
		if(graphObject instanceof GraphComplex) {
			GraphComplex complex = (GraphComplex) graphObject;
			List<GraphPhysicalEntity> entities = new ArrayList<>();
			entities.addAll(complex.getParticipants());
			for(int i=0; i<entities.size(); i++) {
				nodeArr.set(nodeArr.size(), getProtein(entities.get(i)));
				for(int j=i+1; j<entities.size(); j++) {
					edgeArr.set(edgeArr.size(), 
								makeFI(edgeArr.size(), 
								entities.get(i).getIdentifier(), 
								entities.get(j).getIdentifier(), 
								"solid"));
				}
			}
		}
		updateView(nodeArr, edgeArr); //initializes and adds diagram nodes and edges
		loadPairwiseRelationships(graphObject);
	}

	private void loadNetwork(String geneName) {
		JSONArray nodeArr = new JSONArray();
		nodeArr.set(nodeArr.size(), getProtein(geneName, false));
		updateView(nodeArr, new JSONArray());
	}

	private void loadPairwiseRelationships(GraphObject graphObject) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Makes a node for a passed in GraphPhysicalEntity
	 * @param entity
	 * @return
	 */
	private JSONValue getProtein(GraphPhysicalEntity entity) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		
		node.put("id", new JSONString(entity.getIdentifier()));
		node.put("name", new JSONString(entity.getDisplayName()));
		
		result.put("data", node);
		
		return result;
	}
	
	/**
	 * Makes a node for only a passed in gene name string
	 * @param gene
	 * @return
	 */
	private JSONValue getProtein(String gene, boolean interactor) {
		JSONObject result = new JSONObject();
		result.put("group", new JSONString("nodes"));
		
		JSONObject node = new JSONObject();
		node.put("id", new JSONString(gene));
		node.put("name", new JSONString(gene));
		if(interactor == true)
			node.put("interactor", new JSONString("true"));
		else
			node.put("interactor", new JSONString("false"));
		
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
	
	private void onCloseButtonClicked() {
		this.hide();
	}
	
	@Override
	public void hide() {
		PairwisePopupFactory.get().removePopup(this.popupId);
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
		
		@Source("org/reactome/web/fi/tools/overlay/pairwise/cytoscape-style.json")
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
	}
}
