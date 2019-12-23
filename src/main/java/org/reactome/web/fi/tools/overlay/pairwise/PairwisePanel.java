package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.common.IDGListBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class PairwisePanel extends FlowPanel{

	private String title;
	
	private FocusPanel headerPanel;
	private FlowPanel main;
	
	private IDGListBox sources;
	private ListBox lineStyleListBox;
	
	private DeckPanel optionsDeckPanel;
	private ScrollPanel bottomContainer;
	private Label infoLabel;
	
	private boolean expanded;
	
	private Map<String, List<String>> sourceToTypesMap;
	
	public PairwisePanel(String title) {
		this.title = title;
		expanded = false;
		sourceToTypesMap = new HashMap<>();
		
		this.setStyleName(RESOURCES.getCSS().panel());
		
		headerPanel = new FocusPanel();
		headerPanel.setStyleName(RESOURCES.getCSS().header());
		setHeaderPanel();
		
		main = getMainPanel();
		main.setVisible(false);
		
		this.add(headerPanel);
		this.add(main);
		
	}

	private void setHeaderPanel() {
		headerPanel.add(new InlineLabel(title));
		headerPanel.addClickHandler(e -> toggleCollapse());
	}
	
	private FlowPanel getMainPanel() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().main());
		
		FlowPanel leftContainer = getLeftContainer();
		leftContainer.setStyleName(RESOURCES.getCSS().container());	
		
		FlowPanel rightContainer = getRightContainer();
		rightContainer.setStyleName(RESOURCES.getCSS().container());
		
		FlowPanel container = new FlowPanel();
		container.getElement().getStyle().setHeight(170, Unit.PX);
		container.add(leftContainer);
		container.add(rightContainer);
		result.add(container);
		result.add(bottomContainer = new ScrollPanel());
		bottomContainer.add(getInfoPanel());
		bottomContainer.setStyleName(RESOURCES.getCSS().bottomContainer());
		return result;
	}
	/**
	 * Makes right container with bio soures deck panel and color choice
	 * @return
	 */
	private FlowPanel getRightContainer() {
		FlowPanel result = new FlowPanel();
		result.add(new Label("Select bio sources for data set (0 of 12):"));
		optionsDeckPanel = new DeckPanel();
		getDeck();
		result.add(optionsDeckPanel);
		optionsDeckPanel.showWidget(0);
		
		result.add(new Label("Choose a line color (Enter a hex value):"));
		TextBox colorSelector = new TextBox();
		colorSelector.setStyleName(RESOURCES.getCSS().colorPickerTextBox());
		colorSelector.getElement().setPropertyString("placeholder", "e.g.('#000000') and press Enter");
		result.add(colorSelector);
		return result;
	}

	/**
	 * Adds Widget to deck panel on right options container for 
	 * every source in left container's ListBox.
	 */
	private void getDeck() {
		for(int i=0; i < sources.getItemCount(); i++) {
			IDGListBox box = new IDGListBox();
			box.setStyleName(RESOURCES.getCSS().sourcesSelectBox());
			box.setListItems(new ArrayList<>());
			box.addChangeHandler(e -> onBioSourcesListBoxChanged());
			box.setVisibleItemCount(5);
			box.setMultipleSelect(true);
			box.addItem(i + "");
			optionsDeckPanel.add(box);
		}
	}

	private void onBioSourcesListBoxChanged() {
		List<String> currentListBoxSelected = ((IDGListBox)optionsDeckPanel.getWidget(sources.getSelectedIndex())).getSelectedItemsText();
		sourceToTypesMap.get(sources.getSelectedItemText()).clear();
		sourceToTypesMap.get(sources.getSelectedItemText()).addAll(currentListBoxSelected);
		updateInfoLabel();
	}

	/**
	 * Makes left container with data sources
	 * @return
	 */
	private FlowPanel getLeftContainer() {
		FlowPanel result = new FlowPanel();
		Label lbl = new Label("Select data source:");
		result.add(lbl);
		sources = new IDGListBox();
		sources.setStyleName(RESOURCES.getCSS().sourcesSelectBox());
		sources.addChangeHandler(e -> onSourcesListBoxChanged());
		sources.setVisibleItemCount(5);
		sources.setMultipleSelect(false);
		sources.addItem("Test");
		sourceToTypesMap.put("Test", new ArrayList<>());
		sources.addItem("Test2");
		sourceToTypesMap.put("Test2", new ArrayList<>());
		sources.setSelectedIndex(0); //default the first source as selected
		result.add(sources);
		result.add(new Label("Choose a line style:"));
		result.add(lineStyleListBox = new ListBox());
		lineStyleListBox.setStyleName(RESOURCES.getCSS().sourcesSelectBox());
		return result;
	}
	
	/**
	 * gets info panel that displays info about current selections
	 * @return
	 */
	private FlowPanel getInfoPanel() {
		FlowPanel result = new FlowPanel();
		result.add(new Label("Current sources: "));
		result.add(infoLabel = new Label());
		return result;
	}

	/**
	 * Called to update info label with current state of selections
	 */
	private void updateInfoLabel() {
		String info = "";
		
		for(String key : sourceToTypesMap.keySet()) 
			if(sourceToTypesMap.get(key).size() > 0) 
				info += key + 
						" -> " + 
						String.join(", ", sourceToTypesMap.get(key)) + 
						"\n";
		
		infoLabel.setText(info);
	}
	
	private void onSourcesListBoxChanged() {
		optionsDeckPanel.showWidget(sources.getSelectedIndex());
	}

	private void toggleCollapse() {
		if(expanded)
			main.setVisible(false);
		else
			main.setVisible(true);
		expanded = !expanded;
	}
	
	public static PairwiseResources RESOURCES;
	static {
		RESOURCES = GWT.create(PairwiseResources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface PairwiseResources extends ClientBundle {
		@Source(PairwisePanelCSS.CSS)
		PairwisePanelCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-PairwisePanel")
	public interface PairwisePanelCSS extends CssResource {
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/pairwisePanel.css";
	
		String panel();
		
		String header();
		
		String main();
		
		String container();
		
		String sourcesSelectBox();
				
		String colorPickerTextBox();
		
		String bottomContainer();
	}
}
