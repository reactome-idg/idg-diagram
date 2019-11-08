package org.reactome.web.fi.client.visualisers.fiview;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.fi.data.loader.ReactomeSourcesLoader;
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
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author brunsont
 *
 */
public class EdgeContextPanel extends Composite implements ChangeHandler, ReactomeSourcesLoader.Handler{
	
	private EventBus eventBus;
	private ListBox sourcesOptions;
	private FlowPanel main;
	private ReactomeSourcesLoader loader;
	
	public EdgeContextPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		loader = new ReactomeSourcesLoader(this);
		
		main = new FlowPanel();
		main.setStyleName(EDGECONTEXTRESOURCES.getCSS().edgePopup());
		
		initWidget(main);
				
	}

	
	public void updateContext(JSONObject fi) {
		for(int i=0; i<main.getWidgetCount(); i++)
			main.remove(i);
				
		List<String> sourcesList = setSourcesList(fi.get("reactomeId"));		
		
		loader.load(sourcesList);
		
	}
	
	private FlowPanel setOptions(List<String> sourcesList) {
		
		Label lb = new Label("Reactome Source Identifiers: ");
		lb.setStyleName(EDGECONTEXTRESOURCES.getCSS().titleLabel());
		FlowPanel result = new FlowPanel();
		
		//for case where there is only one source
		if(sourcesList.size() <=1) {
			lb.setText("Reactome Source:");
			Label sourceLabel = new Label();
			String sourceString = sourcesList.get(0);
			if(sourceString.length() > 26) {
				sourceLabel.setTitle(sourceString);
				sourceLabel.setText(sourceString.substring(0, 26) + "...");
			}
			else {
				sourceLabel.setText(sourceString);
				sourceLabel.setTitle(sourceString);
			}
			sourceLabel.setStyleName(EDGECONTEXTRESOURCES.getCSS().sourceLabel());
			result.add(lb);
			result.add(sourceLabel);
			return result;
		}
		
		//for case where there are multiple sources
		ListBox list = new ListBox();
		list.setStyleName(EDGECONTEXTRESOURCES.getCSS().listBox());
		list.setMultipleSelect(false);
		list.addChangeHandler(this);
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
			if(sourcesOptions.getValue(i).substring(0, sourcesOptions.getValue(i).indexOf(" ")).equals(selection))
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
		String aux = lb.getSelectedValue().substring(0, lb.getSelectedValue().indexOf(" "));
		if(lb.equals(sourcesOptions)) {
			eventBus.fireEventFromSource(new FireGraphObjectSelectedEvent(aux), this);
			setSelection(aux);
		}
	}
	
	@Override
	public void onReactomeSourcesLoaded(String json) {
		JSONValue value = JSONParser.parseStrict(json);
		JSONArray array = value.isArray();
		List<String> resultingList = new ArrayList<>();
		if(array != null) {
			for(int i=0; i<array.size(); i++) {
				JSONObject object = array.get(i).isObject();
				String sourceString = object.get("dbId").toString() + " - " + object.get("displayName").isString().stringValue();
				resultingList.add(sourceString);
			}
		}
		main.add(setOptions(resultingList));
	}


	@Override
	public void onReactomeSourcesLoadedError(Throwable exception) {
		GWT.log(exception.getMessage());
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
