package org.reactome.web.test;

import org.reactome.web.idgDiagram.client.IdgDiagramViewer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class WidgetTest implements EntryPoint{

	public void onModuleLoad() {
		IdgDiagramViewer view = new IdgDiagramViewer();
		Window.alert("GOT HERE");
		RootPanel.get().add(view);
	}
	
}
