package org.reactome.web.fi.client.visualisers.fiview;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.fi.events.FireGraphObjectSelectedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author brunsont
 *
 */
public class EdgeContextPanel extends Composite implements ChangeHandler{
	
	private EventBus eventBus;
	private ListBox sourcesOptions;
	private FlowPanel main;
	
	public EdgeContextPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		main = new FlowPanel();
		main.setStyleName(EDGECONTEXTRESOURCES.getCSS().edgePopup());
		
		initWidget(main);
				
	}

	
	public void updateContext(JSONObject fi) {
		for(int i=0; i<main.getWidgetCount(); i++)
			main.remove(i);
				
		List<String> sourcesList = setSourcesList(fi.get("reactomeId"));		
		
		main.add(setOptions(sourcesList));
	}
	
	private FlowPanel setOptions(List<String> sourcesList) {
		
		Label lb = new Label("Reactome Source Identifiers: ");
		lb.setStyleName(EDGECONTEXTRESOURCES.getCSS().titleLabel());
		FlowPanel result = new FlowPanel();
		
		//for case where there is only one source
		if(sourcesList.size() <=1) {
			lb.setText("Reactome Source:");
			Label sourceLabel = new Label(sourcesList.get(0));
			sourceLabel.setStyleName(EDGECONTEXTRESOURCES.getCSS().sourceLabel());
			result.add(lb);
			result.add(sourceLabel);
			return result;
		}
		
		//for case where there are multiple sources
		ListBox list = new ListBox();
		list.setStyleName(EDGECONTEXTRESOURCES.getCSS().listBox());
		list.setMultipleSelect(false);
		
		for(String source : sourcesList) {
			list.addItem(source);
		}
		sourcesOptions = list;
		
		result.add(lb);
		result.add(sourcesOptions);
		return result;
	}
	
	//call this after calling updateContext and pass in correct reactome sourceFile
	protected void setSelection(String selection) {
		if(selection==null)
			return;
		for(int i=0; i<sourcesOptions.getItemCount(); i++)
			if(sourcesOptions.getValue(i).equals(selection))
				sourcesOptions.setSelectedIndex(i);
	}

	private List<String> setSourcesList(JSONValue jsonValue) {
		List<String> result = new ArrayList<>();
		JSONArray sourcesArray = jsonValue.isArray();
		
		if(sourcesArray == null) {
			result.add(jsonValue.isString().stringValue());
			return result;
		}
		
		for(int i=0; i<sourcesArray.size(); i++) {
			result.add(sourcesArray.get(i).isString().stringValue());
		}
		return result;
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		ListBox lb = (ListBox) event.getSource();
		String aux = lb.getSelectedValue();
		if(lb.equals(sourcesOptions)) {
			eventBus.fireEventFromSource(new FireGraphObjectSelectedEvent(aux), this);
			setSelection(aux);
		}
	}
	
	public static EdgeContextResources EDGECONTEXTRESOURCES;
	static {
		EDGECONTEXTRESOURCES = GWT.create(EdgeContextResources.class);
		EDGECONTEXTRESOURCES.getCSS().ensureInjected();
	}
	
	public interface EdgeContextResources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idgDiagram-EdgeContextPanel")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/visualisers/fiview/EdgeContextPanel.css";
		
		String edgePopup();
		
		String titleLabel();
		
		String sourceLabel();
		
		String listBox();
	}
}
