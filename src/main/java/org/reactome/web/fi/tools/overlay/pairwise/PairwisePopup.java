package org.reactome.web.fi.tools.overlay.pairwise;

import org.reactome.web.diagram.common.PwpButton;
import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwisePopupFactory;

import com.google.gwt.core.client.GWT;
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
	
	private FlowPanel main;
	
	private FlowPanel infoPanel;
	
	public PairwisePopup(GraphObject graphObject) {
		this.popupId = graphObject.getStId();
		initPanel();
	}

	public PairwisePopup(String uniprot, String geneName) {
		this.popupId = uniprot;
		initPanel();
	}

	private void initPanel() {
		setStyleName(RESOURCES.getCSS().popupPanel());
		setAutoHideEnabled(false);
		setModal(false);
		
		main = new FlowPanel();
//		main.add(new PwpButton("Close", RESOURCES.getCSS().close(), e -> hide()));
		
		main.add(getMainPanel());
		
		setTitlePanel();
		setWidget(main);
		show();
	}
	
	private FlowPanel getMainPanel() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().container());

		SimplePanel cyView = new SimplePanel();
		cyView.getElement().setId("cy-"+popupId);
		cyView.setStyleName(RESOURCES.getCSS().cyView());
		result.add(cyView);
		
		Button infoButton = new Button("Show/Hide info");
		infoButton.setStyleName(RESOURCES.getCSS().infoButton());
		infoButton.addClickHandler(e -> onInfoButtonClicked());
		result.add(infoButton);
		
		infoPanel = new FlowPanel();
		
		return result;
	}

	private void onInfoButtonClicked() {
		infoPanel.setVisible(!infoPanel.isVisible());
	}

	private void setTitlePanel() {
		FlowPanel fp = new FlowPanel();
		fp.add(new PwpButton("Close", RESOURCES.getCSS().close(), e -> hide()));
		InlineLabel title = new InlineLabel("Pairwise popup: " + popupId);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
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
