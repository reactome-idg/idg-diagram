package org.reactome.web.fi.client.visualisers.fiview;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.diagram.context.popups.export.SnapshotTabPanel.Resources;

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
	private HTML htmlLabel;
	private ListBox sourcesOptions;
	private FlowPanel sourcesFlowPanel;
	
	public EdgeContextPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		FlowPanel main = new FlowPanel();
		main.setStyleName(EDGECONTEXTRESOURCES.getCSS().edgePopup());
		sourcesFlowPanel = new FlowPanel();
		htmlLabel = new HTML();
		sourcesOptions = new ListBox();
		
		main.add(htmlLabel);
		main.add(sourcesFlowPanel);
		
		this.add(main);
		
	}
	
	public void updateContext(JSONObject fi) {
		htmlLabel = null;
		sourcesOptions = null;
		sourcesFlowPanel = null;
		
		HTML html = new HTML(new SafeHtmlBuilder()
			.appendEscapedLines("Protein One Name: " + fi.get("source") + "\n"
								+ "Interaction Direction: " + getAnnotationDirection(fi) + "\n"
								+ "Protein Two Name: " + fi.get("target"))
			.toSafeHtml());
		this.htmlLabel = html;
		
		List<String> sourcesList = setSourcesList(fi.get("reactomeId"));
		
		sourcesFlowPanel = setOptions(sourcesList);
		
	}
	
	private FlowPanel setOptions(List<String> sourcesList) {
		ListBox list = new ListBox();
		list.setMultipleSelect(false);
		for(String source : sourcesList) {
			list.addItem(source);
		}
		sourcesOptions = list;
		
		Label lb = new Label("Reactome Sources List: ");
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
			GWT.log("source selected: " + aux);
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
