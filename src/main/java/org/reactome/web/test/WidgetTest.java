package org.reactome.web.test;

import org.reactome.web.idgDiagram.client.IdgDiagramViewer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class WidgetTest implements EntryPoint{

	public void onModuleLoad() {
		IdgDiagramViewer view = new IdgDiagramViewer();
		RootPanel.get().add(view);
	}
	
}
