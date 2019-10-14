package org.reactome.web.fi.client.visualisers.fiview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;


public class NodeContextPanel extends AbsolutePanel {
	
	private FlowPanel main;
	private final String URL = "https://www.uniprot.org/uniprot/";
	
	public NodeContextPanel() {};

	public NodeContextPanel(String name, String id) {
		
		main = new FlowPanel();
		main.setStyleName(NODECONTEXTRESOURCES.getCSS().nodePopup());
		
		
		Label lbl = new Label("Protein name: " + name);
		Hyperlink link = new Hyperlink(id, URL + id);
		Label idLbl = new Label("Uniprot Identifier: " + link);
		main.add(lbl);
		main.add(idLbl);
		
		this.add(main);
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
		 
	}
}
