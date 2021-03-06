package org.reactome.web.fi.tools.overlay.pairwise;

import org.reactome.web.fi.client.popups.NodeDialogPanel;
import org.reactome.web.fi.common.CommonButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseNodeContextPopup extends DialogBox{

	public interface Handler{
		void onRemoveButtonClicked(String id);
	}
	
	private final String PROTEIN_URL = "https://www.uniprot.org/uniprot/";
	
	private Handler handler;
	
	private String accession;
	private Label targetDevLevel;
	
	public PairwiseNodeContextPopup(String accession, String name, String dataOverlayValue, boolean showRemove, Handler handler) {
		this.accession = accession;
		this.handler = handler;
		setStyleName(RESOURCES.getCSS().nodePopup());
		setAutoHideEnabled(true);
		setModal(false);
		
		FlowPanel main = new FlowPanel();
		main.setStyleName(RESOURCES.getCSS().mainPanel());
		
		//Info panel contains the gene symbol, a link to uniprot for the uniprot, and the data overlay value
		FlowPanel infoPanel = new FlowPanel();
		
		Label lbl = new Label("Gene Symbol: " +  name);
		lbl.setStyleName(RESOURCES.getCSS().label());
		infoPanel.add(lbl);

		if(!accession.contains("ENSG")) {
			String link = PROTEIN_URL + accession;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder()
					.appendEscapedLines("Uniprot: " + accession).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(RESOURCES.getCSS().linkAnchor());
			infoPanel.add(linkAnchor);
		}
		
		infoPanel.add(targetDevLevel = new Label());
		
		//if not null, set label with either No Expression or the overlay value
		if(dataOverlayValue != null)
			infoPanel.add(new Label("Overlay value: " + (dataOverlayValue == "undefined" ? "N/A":dataOverlayValue)));
		
		main.add(infoPanel);
		main.add(getOptionsPanel(showRemove));
		setTitlePanel(name);
		setWidget(main);
	}

	private void setTitlePanel(String name) {
		FlowPanel fp = new FlowPanel();
		Image img = new Image(NodeDialogPanel.RESOURCES.entity());
		fp.add(img);
		
		InlineLabel title = new InlineLabel(name);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(RESOURCES.getCSS().header());
	}
	
	/**
	 * Add a row of options buttons for context popups
	 * @return
	 */
	private FlowPanel getOptionsPanel(boolean showRemove) {
		FlowPanel result = new FlowPanel();
		
		if(showRemove) {
			CommonButton removeButton;
			result.add(removeButton = new CommonButton("Remove", e->onRemoveButtonClicked()));
			removeButton.setStyleName(RESOURCES.getCSS().removeButton());
		}
		
		return result;
	}
	
	private void onRemoveButtonClicked() {
		handler.onRemoveButtonClicked(accession);
		this.hide();
	}

	public void setTargetDevLevel(String result) {
		targetDevLevel.setText("Target Development Level: " + result);
	}
	
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-PairwiseNodeContextPopup")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/PairwiseNodeContextPopup.css";
	
		String removeButton();
		
		String header();
		
		String linkAnchor();
		
		String label();
		
		String mainPanel();
		
		String nodePopup();
	}
}
