package org.reactome.web.fi.client.visualisers.fiview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


public class NodeContextPanel extends Composite {
	
	private FlowPanel main;
	private final String URL = "https://www.uniprot.org/uniprot/";
	
	public NodeContextPanel() {
		main = new FlowPanel();
		main.setStyleName(NODECONTEXTRESOURCES.getCSS().nodePopup());
		initWidget(main);
	}

	public void updatePanel(String name, String id) {
		if(main.getWidgetCount()>0)
			main.remove(0);

		
		String shortName = name;
		String accession = id;
		String link = URL + accession;
		
		Label lbl = new Label("Protein name: " + shortName);
		lbl.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
		Anchor linkAnchor = new Anchor(new SafeHtmlBuilder()
				.appendEscapedLines("Uniprote Identifier: " + accession).toSafeHtml(),
				link, "_blank");
		linkAnchor.setStyleName(NODECONTEXTRESOURCES.getCSS().label());
		
		FlowPanel panel = new FlowPanel();
		panel.add(lbl);
		panel.add(linkAnchor);
		main.add(panel);
		
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
		 
	}
}
