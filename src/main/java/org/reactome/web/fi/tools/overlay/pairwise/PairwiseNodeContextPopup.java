package org.reactome.web.fi.tools.overlay.pairwise;

import org.reactome.web.fi.client.popups.NodeContextPanel;
import org.reactome.web.fi.client.popups.NodeContextPanel.ResourceCSS;
import org.reactome.web.fi.common.CommonButton;

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
	private ResourceCSS contextCSS = NodeContextPanel.NODECONTEXTRESOURCES.getCSS();
	
	private Handler handler;
	
	private String accession;
	
	public PairwiseNodeContextPopup(String accession, String name, String dataOverlayValue, Handler handler) {
		this.accession = accession;
		this.handler = handler;
		setStyleName(contextCSS.nodePopup());
		setAutoHideEnabled(true);
		setModal(false);
		
		FlowPanel main = new FlowPanel();
		main.setStyleName(contextCSS.mainPanel());
		
		//Info panel contains the gene symbol, a link to uniprot for the uniprot, and the data overlay value
		FlowPanel infoPanel = new FlowPanel();
		
		Label lbl = new Label("Gene Symbol: " +  name);
		lbl.setStyleName(contextCSS.label());
		infoPanel.add(lbl);

		if(!accession.contains("ENSG")) {
			String link = PROTEIN_URL + accession;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder()
					.appendEscapedLines("Uniprot: " + accession).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(contextCSS.linkAnchor());
			infoPanel.add(linkAnchor);
		}
		
		infoPanel.add(new Label("Overlay value: " + dataOverlayValue));
		
		main.add(infoPanel);
		main.add(getOptionsPanel());
		setTitlePanel(name);
		setWidget(main);
	}

	private void setTitlePanel(String name) {
		FlowPanel fp = new FlowPanel();
		Image img = new Image(NodeContextPanel.NODECONTEXTRESOURCES.entity());
		fp.add(img);
		
		InlineLabel title = new InlineLabel(name);
		fp.add(title);
		
		SafeHtml safe = SafeHtmlUtils.fromTrustedString(fp.toString());
		getCaption().setHTML(safe);
		getCaption().asWidget().setStyleName(contextCSS.header());
	}
	
	/**
	 * Add a row of options buttons for context popups
	 * @return
	 */
	private FlowPanel getOptionsPanel() {
		FlowPanel result = new FlowPanel();
		
		CommonButton removeButton;
		result.add(removeButton = new CommonButton("Remove", e->onRemoveButtonClicked()));
		removeButton.setStyleName(contextCSS.removeButton());
		
		return result;
	}
	
	private void onRemoveButtonClicked() {
		handler.onRemoveButtonClicked(accession);
		this.hide();
	}
}
