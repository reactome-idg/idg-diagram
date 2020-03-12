package org.reactome.web.fi.client.popups;

import org.reactome.web.fi.data.loader.TCRDInfoLoader;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class NodeInfoPanel extends Composite{

	private final String PROTEIN_URL = "https://www.uniprot.org/uniprot/";
	private final String GENE_URL = "http://www.ensembl.org/Homo_sapiens/geneview?gene=";
	private final String PHAROS_URL = "https://pharos.nih.gov/targets/";
	private final String TDL_URL = "https://druggablegenome.net/ProteinFam";
	
	private String targetDevLevel;
	
	private FlexTable table;
	
	public NodeInfoPanel(EventBus eventBus, String id, String name) {
		
		FlowPanel infoPanel = new FlowPanel();
		table = new FlexTable();
		table.setStyleName(RESOURCES.getCSS().table());
		
		table.setText(0, 0, "Gene Symbol: ");
		table.setText(0, 1, name);
		table.getFlexCellFormatter().setColSpan(0, 0, 3);
		
		table.setText(1, 0, "Uniprot Link:");
		table.setWidget(1, 1, getUniprotAnchor(id));
		table.getFlexCellFormatter().setColSpan(1, 0, 3);
		
		targetDevLevel = "Loading...";
		table.setText(2, 0, "Target Development Level:");
		table.setText(2, 1, targetDevLevel);
		table.getFlexCellFormatter().setColSpan(2, 0, 3);
		
		table.setText(3, 0, "Pharos Target Page: ");
		table.setWidget(3, 1, getPharoseLink(id));
		table.getFlexCellFormatter().setColSpan(3, 0, 3);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(RESOURCES.getCSS().scrollPanel());
		scrollPanel.add(table);
		
		infoPanel.add(scrollPanel);
		
		loadTargetDevLevel(id); //load target Dev level if not ENSG identifier

		
		initWidget(infoPanel);
	}

	private Anchor getPharoseLink(String id) {
		String link = PHAROS_URL + id;
		Anchor result = new Anchor(new SafeHtmlBuilder()
				.appendEscapedLines("Go!").toSafeHtml(),
				link,"_blank");
		result.setStyleName(RESOURCES.getCSS().linkAnchor());
		result.getElement().appendChild(new Image(RESOURCES.linkOut()).getElement());
		return result;
	}

	private Anchor getUniprotAnchor(String id) {
		if(!id.contains("ENSG")) {
			String link = PROTEIN_URL + id;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder()
					.appendEscapedLines(id).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(RESOURCES.getCSS().linkAnchor());
			linkAnchor.getElement().appendChild(new Image(RESOURCES.linkOut()).getElement());
			return linkAnchor;
		}
		else{
			String link = GENE_URL + id;
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder().appendEscapedLines(id).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(RESOURCES.getCSS().linkAnchor());
			linkAnchor.getElement().appendChild(new Image(RESOURCES.linkOut()).getElement());
			return linkAnchor;
		}
	}
	
	/**
	 * Loads Target Development Level for passed in uniprot id
	 * @param uniprotId
	 */
	private void loadTargetDevLevel(String uniprotId) {
		TCRDInfoLoader.loadSingleTargetLevelProtein(uniprotId, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				targetDevLevel = result;
				Anchor link = new Anchor(new SafeHtmlBuilder().appendEscapedLines(targetDevLevel).toSafeHtml(),TDL_URL, "_blank");
				link.getElement().appendChild(new Image(RESOURCES.linkOut()).getElement());
				table.setWidget(2, 1, link);
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
		
		@Source("images/external_link_icon.gif")
		ImageResource linkOut();
	}
	
	@CssResource.ImportedWithPrefix("idg-NodeInfoPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/client/popups/NodeInfoPanel.css";
	
		String label();
		
		String linkAnchor();
		
		String table();
		
		String scrollPanel();
		
	}
}
