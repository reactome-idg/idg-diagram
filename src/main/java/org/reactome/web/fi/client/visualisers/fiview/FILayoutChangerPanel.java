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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author brunsont
 *
 */
public class FILayoutChangerPanel extends DialogBox implements ChangeHandler {

	private EventBus eventBus;
	
	private ListBox layoutSelector;
	
	public FILayoutChangerPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		setAutoHideEnabled(true);
		setModal(false);
		this.setStyleName(FICONTEXTRESOURCES.getCSS().fipopup());
		FlowPanel main = new FlowPanel();
		
		layoutSelector = new ListBox();
		
		Label title = new Label("Choose Layout:");
		title.setStyleName(FICONTEXTRESOURCES.getCSS().layoutLabel());
		main.add(title);
		main.add(layoutSelector = new ListBox());
		layoutSelector.setMultipleSelect(false);
		setSelections();
		
		
//		setSelection(layoutSelector, "force directed");
		initHandlers();
		
		this.add(main);
		
	}

	private void setSelections() {
		for(FILayoutType type : FILayoutType.values())
			layoutSelector.addItem(type.getName());
	}

	@Override
	public void onChange(ChangeEvent event) {
		if(event.getSource()!=layoutSelector) return;
		ListBox lb = (ListBox) event.getSource();
        String aux = lb.getSelectedValue();
    	eventBus.fireEventFromSource(new CytoscapeLayoutChangedEvent(FILayoutType.getType(aux)), this);
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
