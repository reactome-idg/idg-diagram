package org.reactome.web.fi.client.visualisers.fiview;

import java.util.List;

import org.reactome.web.fi.events.CytoscapeLayoutChangedEvent;
import org.reactome.web.fi.model.FILayoutType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class FILayoutChangerPanel extends AbsolutePanel implements ChangeHandler {

	private EventBus eventBus;
	
	private ListBox layoutSelector;
	
	public FILayoutChangerPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		this.setStyleName(FICONTEXTRESOURCES.getCSS().fipopup());
		FlowPanel main = new FlowPanel();
		
		layoutSelector = new ListBox();
		
		main.add(getLayoutsWidget("Choose Layout:", layoutSelector, FILayoutType.getLayouts()));
		
		setSelection(layoutSelector, "force directed");
		initHandlers();
		
		this.add(main);
		
	}

	private void setSelection(ListBox layoutListBox, String selection) {
		if(selection==null)
			return;
		for(int i=0; i<layoutListBox.getItemCount(); i++) {
			if(layoutListBox.getValue(i).equals(selection))
				layoutListBox.setSelectedIndex(i);
		}
		
	}

	private Widget getLayoutsWidget(String title, ListBox layoutListBox, List<String> layouts) {
		layoutListBox.setMultipleSelect(false);
		for(String layout: layouts) {
			layoutListBox.addItem(layout);
		}
		Label lb = new Label(title);
		lb.setStyleName(FICONTEXTRESOURCES.getCSS().layoutLabel());
		
		FlowPanel result = new FlowPanel();
		result.add(lb);
		result.add(layoutListBox);
		
		return result;
	}

	@Override
	public void onChange(ChangeEvent event) {
		ListBox lb = (ListBox) event.getSource();
        String aux = lb.getSelectedValue();
        if(lb.equals(layoutSelector)) {
        	eventBus.fireEventFromSource(new CytoscapeLayoutChangedEvent(aux), this);
        	setSelection(layoutSelector, aux);
        }
	}
	
	private void initHandlers() {
		layoutSelector.addChangeHandler(this);
	}
	
	public static FIContextResources FICONTEXTRESOURCES;
	static {
		FICONTEXTRESOURCES = GWT.create(FIContextResources.class);
		FICONTEXTRESOURCES.getCSS().ensureInjected();
	}
	
	public interface FIContextResources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-FILayoutChangerPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/FIContextPanel.css";
		
		String fipopup();
		
		String layoutLabel();
	}
}
