package org.reactome.web.idgDiagram.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IdgDiagramViewer extends Composite implements IsWidget {


	
	public IdgDiagramViewer() {
		
		initialise();
		
	}
	
	private void initialise() {
		HTMLPanel view = new HTMLPanel("Hello world");
		initWidget(view);
		GWT.log("this works");
	}
 
	@Override
	public Widget asWidget() {
		return this;
	}
	
}
