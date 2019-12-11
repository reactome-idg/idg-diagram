package org.reactome.web.fi.client.visualisers.fiview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author brunsont
 *
 */
public class NodeContextPanel extends Composite {
	
	private FlowPanel main;
	private final String URL = "https://www.uniprot.org/uniprot/";
	
	public NodeContextPanel() {
		main = new FlowPanel();
		main.setStyleName(NODECONTEXTRESOURCES.getCSS().nodePopup());
		initWidget(main);
	}

	public void updatePanel(String name, String id) {
		main.clear();

		FlowPanel panel = new FlowPanel();
		String link = URL + id;
		
		Label lbl = new Label("Protein name: " +  name);
		lbl.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
		panel.add(lbl);

		if(!id.contains("ENSG")) {
			Anchor linkAnchor = new Anchor(new SafeHtmlBuilder()
					.appendEscapedLines("Uniprote Identifier: " + id).toSafeHtml(),
					link, "_blank");
			linkAnchor.setStyleName(NODECONTEXTRESOURCES.getCSS().linkAnchor());
			panel.add(linkAnchor);
		}
		else {
			Label idLbl = new Label("Identifier: " + id);
			idLbl.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
			panel.add(idLbl);
		}
		
		main.add(panel);
		
	}
	public void updateExpression(Double value) {
		Label lbl = new Label("Overlay Value: " + value);
		lbl.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
		main.add(lbl);
	}
	public void updateExpression(String value) {
		Label lbl = new Label("Overlay Value: " + value);
		lbl.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
		main.add(lbl);
	}
	
	public static NodeContextResources NODECONTEXTRESOURCES;
	static {
		NODECONTEXTRESOURCES = GWT.create(NodeContextResources.class);
		NODECONTEXTRESOURCES.getCSS().ensureInjected();
	}
	
	public interface NodeContextResources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-NodeContextPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/client/visualisers/fiview/NodeContextPanel.css";
		
		String nodePopup();
		
		String label();
		
		String linkAnchor();
		 
	}
}
