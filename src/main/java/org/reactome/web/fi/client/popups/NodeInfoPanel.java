package org.reactome.web.fi.client.popups;

import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class NodeInfoPanel extends Composite{

	private final String PROTEIN_URL = "https://www.uniprot.org/uniprot/";
	private final String GENE_URL = "http://www.ensembl.org/Homo_sapiens/geneview?gene=";
	
	Label targetDevLevel;
	
	public NodeInfoPanel(EventBus eventBus, String id, String name) {
		
		FlowPanel infoPanel = new FlowPanel();
		
		Label lbl = new Label("Gene Symbol: " +  name);
		lbl.setStyleName(RESOURCES.getCSS().label());
		infoPanel.add(lbl);

		if(!id.contains("ENSG")) {
			String link = PROTEIN_URL + id;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder()
					.appendEscapedLines("Uniprot: " + id).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(RESOURCES.getCSS().linkAnchor());
			infoPanel.add(linkAnchor);
			loadTargetDevLevel(id); //load target Dev level if not ENSG identifier
		}
		else if(id.contains("ENSG")) {
			String link = GENE_URL + id;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder().appendEscapedLines("Ensembl: " + id).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(RESOURCES.getCSS().linkAnchor());
			infoPanel.add(linkAnchor);
		}
		infoPanel.add(targetDevLevel = new Label("Loading..."));
		targetDevLevel.setStyleName(RESOURCES.getCSS().label());
		
		initWidget(infoPanel);
	}
	
	/**
	 * Loads Target Development Level for passed in uniprot id
	 * @param uniprotId
	 */
	private void loadTargetDevLevel(String uniprotId) {
		TCRDInfoLoader.loadSingleTargetLevelProtein(uniprotId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				NodeInfoPanel.this.targetDevLevel.setText("Target Development Level: " + result);
			}
			@Override
			public void onFailure(Throwable caught) {
				Console.log(caught);
			}
		});
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
	
	@CssResource.ImportedWithPrefix("idg-NodeInfoPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/client/popups/NodeInfoPanel.css";
	
		String label();
		
		String linkAnchor();
	}
}
