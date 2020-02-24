package org.reactome.web.fi.client.popups;


import org.reactome.web.fi.model.FILayoutType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author brunsont
 *
 */
public class FILayoutChangerPanel extends DialogBox implements ChangeHandler {
	
	public interface LayoutChangeHandler{
		void onLayoutChange(FILayoutType type);
	}

	private LayoutChangeHandler handler;
	
	private ListBox layoutSelector;
	private String currentLayout;
	
	public FILayoutChangerPanel(String currentLayout, LayoutChangeHandler handler) {
		this.currentLayout = currentLayout;
		this.handler = handler;
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
		
		initHandlers();
		
		this.add(main);
		
	}

	private void setSelections() {
		for(FILayoutType type : FILayoutType.values()) {
			layoutSelector.addItem(type.getName());
			if(type.getName() == currentLayout) {
				layoutSelector.setSelectedIndex(layoutSelector.getItemCount());
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if(event.getSource()!=layoutSelector) return;
		ListBox lb = (ListBox) event.getSource();
        String aux = lb.getSelectedValue();
    	handler.onLayoutChange(FILayoutType.getType(aux));
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
