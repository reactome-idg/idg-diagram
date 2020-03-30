package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.List;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class NewPairwisePopup extends AbstractPairwisePopup{

	private String popupId;
	private PairwisePopupCytoscapePanel cyPanel;
	private List<PairwiseOverlayObject> pairwiseOverlayProperties;
	
	public NewPairwisePopup(GraphObject graphObject, List<PairwiseOverlayObject> pairwiseOverlayProperties, int zIndex) {
		this(graphObject.getStId(), zIndex, pairwiseOverlayProperties);
		cyPanel.init(graphObject);
	}
	
	public NewPairwisePopup(String uniprot, String geneName, List<PairwiseOverlayObject> pairwiseOverlayProperties, int zIndex) {
		this(uniprot, zIndex, pairwiseOverlayProperties);
		cyPanel.init(uniprot, geneName);
	}
	
	private NewPairwisePopup(String popupId, int zIndex, List<PairwiseOverlayObject> pairwiseOverlayProperties) {
		this.popupId = popupId;
		this.pairwiseOverlayProperties = pairwiseOverlayProperties;
		initPanel();
		panelClicked();
	}

	private void initPanel() {
		FocusPanel focus = new FocusPanel();
		setStyleName(RESOURCES.getCSS().popupPanel());
		setAutoHideEnabled(false);
		setModal(false);
		
		FlowPanel main = new FlowPanel();
		main.add(new PwpButton("Close", RESOURCES.getCSS().close(), e-> hide()));
		
		main.add(getMainPanel());
		
		focus.add(main);
		focus.addClickHandler(e -> panelClicked());
		setTitlePanel();
	}
	
	private void setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		InlineLabel title = new InlineLabel("Pairwise popup: " + popupId);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
	}

	private void panelClicked() {
		PairwiseOverlayFactory.get().resetZIndexes();
		this.getElement().getStyle().setZIndex(PairwiseOverlayFactory.get().getMaxZIndex());
		focused=true;
	}

	private FlowPanel getMainPanel() {
		FlowPanel result = new FlowPanel();
		
		result.setStyleName(RESOURCES.getCSS().container());
		
		result.add(cyPanel = new PairwisePopupCytoscapePanel(popupId, pairwiseOverlayProperties, RESOURCES));
		result.add(new PairwisePopupTablePanel(pairwiseOverlayProperties, RESOURCES));
		
		
		return result;
	}


	@Override
	public void hide() {
		PairwiseOverlayFactory.get().removePopup(this.popupId);
		super.hide();
	}




	/**
	 * Below here is all for styling. Handles styles of PairwisePopup,
	 * PairwisePopupTablePanel, and PairwisePopupCytoscapePanel
	 */
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
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/PairwisePopup.css";
		
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
		
		String filterTextBox();
		
		String pager();
		
		String filterPanel();
		
		String table();
		
		String eTypeAndTissueLabel();
		
		String smallText();
		
		String exportButton();
		
	}
}
