package org.reactome.web.fi.client.visualisers.fiview;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.diagram.context.popups.export.SnapshotTabPanel.Resources;
import org.reactome.web.fi.events.FireGraphObjectSelectedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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
public class EdgeContextPanel extends AbsolutePanel implements ChangeHandler{
	
	private EventBus eventBus;
	private ListBox sourcesOptions;
	private FlowPanel main;
	
	public EdgeContextPanel(EventBus eventBus) {
		this.eventBus = eventBus;
				
	}
	
	public void updateContext(JSONObject fi) {
		sourcesOptions = null;
		main = new FlowPanel();
		main.setStyleName(EDGECONTEXTRESOURCES.getCSS().edgePopup());
		
		if(this.getWidgetCount()>0)
			this.remove(0);

		HTML html = new HTML(new SafeHtmlBuilder()
			.appendEscapedLines("Protein One Name: " + fi.get("source") + "\n"
								+ "Interaction Direction: " + getAnnotationDirection(fi) + "\n"
								+ "Protein Two Name: " + fi.get("target"))
			.toSafeHtml());
				
		List<String> sourcesList = setSourcesList(fi.get("reactomeId"));		
		
		main.add(html);
		main.add(setOptions(sourcesList));
		this.add(main);
				
	}
	
	private FlowPanel setOptions(List<String> sourcesList) {
		ListBox list = new ListBox();
		list.setMultipleSelect(false);
		for(String source : sourcesList) {
			list.addItem(source);
		}
		sourcesOptions = list;
		
		Label lb = new Label("Reactome Source Identifiers: ");
		lb.setStyleName(EDGECONTEXTRESOURCES.getCSS().sourcesLabel());
		
		FlowPanel result = new FlowPanel();
		result.add(lb);
		result.add(sourcesOptions);
		return result;
	}
	
	//call this after calling updateContext and pass in correct reactome source
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

	private String getAnnotationDirection(JSONObject fi) {
		if(fi.get("direction") == null)
			return "-";
		else
			return fi.get("direction").isString().stringValue();
		
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
		
		String sourcesLabel();
	}
}
