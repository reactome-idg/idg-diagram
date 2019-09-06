package org.reactome.web.test;

import org.reactome.web.idg.client.IdgDiagramViewer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;


public class WidgetTest implements EntryPoint{

	public void onModuleLoad() {
		Window.alert("GOT HERE");
		IdgDiagramViewer view = new IdgDiagramViewer();
		RootPanel.get().add(view);
	}
}
