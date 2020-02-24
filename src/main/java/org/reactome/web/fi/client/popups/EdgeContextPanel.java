package org.reactome.web.fi.client.popups;

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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
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
public class EdgeContextPanel extends DialogBox implements ChangeHandler, ReactomeSourcesLoader.Handler{
	
	private EventBus eventBus;
	private ListBox sourcesOptions;
	private FlowPanel main;
	private ReactomeSourcesLoader loader;
	
	public EdgeContextPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		setAutoHideEnabled(true);
		setModal(false);
		this.setStyleName(EDGECONTEXTRESOURCES.getCSS().edgePopup());

		loader = new ReactomeSourcesLoader(this);
		
		main = new FlowPanel();		
		setWidget(main);
				
	}

	public void updateContext(JSONObject fi) {
		main.clear();
				
		List<String> sourcesList = getSourcesList(fi.get("reactomeId"));		
		
		loader.load(sourcesList);
		
	}
	
	private FlowPanel setOptions(List<String> sourcesList) {
		
		Label lb = new Label("Reactome Source Identifiers: ");
		lb.setStyleName(EDGECONTEXTRESOURCES.getCSS().titleLabel());
		FlowPanel result = new FlowPanel();
		
		//for case where there is only one source
		if(sourcesList.size() ==1) {
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
		list.addChangeHandler(this);
		for(String source : sourcesList) {			
			list.addItem(source);
		}
		sourcesOptions = list;
		
		result.add(lb);
		result.add(sourcesOptions);
		return result;
	}

	private List<String> getSourcesList(JSONValue jsonValue) {
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
		String CSS = "org/reactome/web/fi/client/popups/EdgeContextPanel.css";
		
		String edgePopup();
		
		String titleLabel();
		
		String sourceLabel();
		
		String listBox();
	}
}
