package org.reactome.web.idg.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


public class IdgDiagramViewer extends Composite implements IsWidget {

	private Button test;
	
	public IdgDiagramViewer() {
		
		initialise();
		
	}
	
	private void initialise() {
		SimplePanel view = new SimplePanel();
		view.getElement().setId("test div");
		test = new Button("test");
		view.add(test);
		view.add(new Label("Hello world"));
		initWidget(view);	}
 
	@Override
	public Widget asWidget() {
		return this;
	}
	
}
