package org.reactome.web.fi.client.visualisers.fiview.panels;

import org.reactome.web.diagram.common.IconButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * @author brunsont
 *
 */
public class SearchPanel extends FlowPanel{

	private EventBus eventBus;
	private boolean isExpanded = false;
	
	public SearchPanel(EventBus eventBus) {
		setStyleName(RESOURCES.getCSS().launchPanel());
		this.eventBus = eventBus;
		
		initPanel();
	}
	
	
	private void initPanel() {
		IconButton searchBtn = new IconButton(RESOURCES.searchIcon(), RESOURCES.getCSS().launch(),"Search Nodes", e -> expandSearch());
	
	}


	private void expandSearch() {
		if(!isExpanded)
			expandPanel();
		else {
			collapsePanel();
			stopExpandingByDefault();
		}
	}


	private void stopExpandingByDefault() {
		// TODO Auto-generated method stub
		
	}


	private void collapsePanel() {
		// TODO Auto-generated method stub
		
	}


	private void expandPanel() {
		// TODO Auto-generated method stub
		
	}


	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle {
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
		
		@Source("images/search.png")
        ImageResource searchIcon();
		
		@Source("images/cancel.png")
        ImageResource clear();
		
		@Source("images/search_go.png")
        ImageResource searchGo();
	}
	
	@CssResource.ImportedWithPrefix("idg-FIView-Search")
	public interface ResourceCSS extends CssResource {
		String CSS = "org/reactome/web/fi/client/visualisers/fiview/panels/SearchPanel.css";
	
		String launchPanel();
		
		String launch();
	}
	
}
