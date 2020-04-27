package org.reactome.web.fi.client.visualisers.fiview.panels;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.fi.events.SearchFINodesEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author brunsont
 *
 */
public class SearchPanel extends AbsolutePanel{

	private final String OPENING_TEXT = "Search FI nodes";
	private final int SEARCH_QUERY_MINIMUM_LENGTH = 2;
	
	private EventBus eventBus;
	private boolean isExpanded = false;
	
	private TextBox input;
	
	public SearchPanel(EventBus eventBus) {
		this.eventBus = eventBus;
		
		initPanel();	
	}

	private void initPanel() {
		this.setStyleName(RESOURCES.getCSS().launchPanel());
		IconButton searchBtn = new IconButton(RESOURCES.searchIcon(), RESOURCES.getCSS().launch(),"Search Nodes", e -> expandSearch());
		this.add(searchBtn);
		
		this.input = new TextBox();
		this.input.setStyleName(RESOURCES.getCSS().input());
		this.input.getElement().setPropertyString("placeholder", OPENING_TEXT);
		this.input.getElement().setPropertyBoolean("spellcheck", false);
		this.input.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER)
					executeBtnClicked();
			}
		});
		this.add(input);
		
		IconButton clearBtn = new IconButton("", RESOURCES.clear());
		clearBtn.setStyleName(RESOURCES.getCSS().clearBtn());
		clearBtn.setVisible(false);
		clearBtn.setTitle("Clear search");
		clearBtn.addClickHandler(e -> onClearButtonClicked());
		this.add(clearBtn);
		
		IconButton executeBtn = new IconButton("", RESOURCES.searchGo());
		executeBtn.setStyleName(RESOURCES.getCSS().executeBtn());
		executeBtn.setVisible(true);
		executeBtn.setTitle("Search");
		executeBtn.addClickHandler(e -> executeBtnClicked());
		this.add(executeBtn);
	}

	private void executeBtnClicked() {
		String query = input.getText().replaceAll("\\s", "");
		if(query.length() < SEARCH_QUERY_MINIMUM_LENGTH) return;
		
		Set<String> queryItems = new HashSet<>(Arrays.asList(query.split(",")));
		eventBus.fireEventFromSource(new SearchFINodesEvent(queryItems), this);
	}


	private void onClearButtonClicked() {
		if(!input.getValue().isEmpty()) {
			input.setValue("");
			setFocus(true);
		}
	}

	private void setFocus(boolean focused) {
		this.input.setFocus(focused);
	}

	private void expandSearch() {
		if(!isExpanded)
			expandPanel();
		else {
			collapsePanel();
		}
		isExpanded = !isExpanded;
	}

	private void collapsePanel() {
		removeStyleName(RESOURCES.getCSS().launchPanelExpanded());
		input.removeStyleName(RESOURCES.getCSS().inputActive());
	}


	private void expandPanel() {
		expandPanel(true);
	}


	private void expandPanel(boolean b) {
		addStyleName(RESOURCES.getCSS().launchPanelExpanded());
		input.addStyleName(RESOURCES.getCSS().inputActive());
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
		
		String input();
		
		String clearBtn();
		
		String executeBtn();
		
		String launchPanelExpanded();
		
		String inputActive();
	}
	
}
