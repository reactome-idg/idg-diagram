package org.reactome.web.fi.tools.overlay.pairwise;

import org.reactome.web.fi.common.MultiSelectListBox;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;

public class PairwisePanel extends FlowPanel{

	private String title;
	
	private FocusPanel headerPanel;
	private FlowPanel main;
	
	private MultiSelectListBox sources;
	private ListBox lineStyleListBox;
	private FlowPanel properties;
	
	DeckPanel optionsDeckPanel;
	
	private boolean expanded;
	
	public PairwisePanel(String title) {
		this.title = title;
		expanded = false;
		
		this.setStyleName(RESOURCES.getCSS().panel());
		
		headerPanel = new FocusPanel();
		headerPanel.setStyleName(RESOURCES.getCSS().header());
		setHeaderPanel();
		
		main = getMainPanel();
		main.setVisible(false);
		
		//initializing here for use later
		properties = new FlowPanel();
		
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
		
		
		result.add(leftContainer);
		result.add(rightContainer);
		return result;
	}

	private FlowPanel getRightContainer() {
		FlowPanel result = new FlowPanel();
		result.add(new Label("Other Options:"));
		optionsDeckPanel = new DeckPanel();
		optionsDeckPanel.setStyleName(RESOURCES.getCSS().optionsDeckPanel());
		getDeck();
		result.add(optionsDeckPanel);
		
		result.add(new Label("Choose a line color (Enter a hex value):"));
		TextBox colorSelector = new TextBox();
		colorSelector.setStyleName(RESOURCES.getCSS().colorPickerTextBox());
		colorSelector.getElement().setPropertyString("placeholder", "e.g.('#000000') and press Enter");
		result.add(colorSelector);
		return result;
	}

	//Override to provide extra options to a PairwisePanel
	private void getDeck() {
		for(int i=0; i < sources.getItemCount(); i++) {
			optionsDeckPanel.add(new Label(sources.getItemText(i)));
		}
	}

	private FlowPanel getLeftContainer() {
		FlowPanel result = new FlowPanel();
		Label lbl = new Label("Select Sources (Hold Ctrl for multiple):");
		result.add(lbl);
		sources = new MultiSelectListBox();
		sources.setStyleName(RESOURCES.getCSS().sourcesSelectBox());
		sources.addChangeHandler(e -> onListBoxChanged());
		sources.setVisibleItemCount(5);
		sources.setMultipleSelect(false);
		result.add(sources);
		result.add(new Label("Choose a line style:"));
		result.add(lineStyleListBox = new ListBox());
		lineStyleListBox.setStyleName(RESOURCES.getCSS().sourcesSelectBox());
		return result;
	}

	private Object onListBoxChanged() {
		// TODO Auto-generated method stub
		return null;
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
		
		String optionsDeckPanel();
		
		String colorPickerTextBox();
	}
}
