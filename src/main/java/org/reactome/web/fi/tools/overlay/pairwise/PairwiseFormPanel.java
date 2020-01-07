package org.reactome.web.fi.tools.overlay.pairwise;

import org.reactome.web.fi.common.IDGListBox;
import org.reactome.web.fi.tools.overlay.DataOverlayPanel.Resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class PairwiseFormPanel extends FlowPanel{
	
	/**
	 * interface for passing selected info back to entityOverlayPanel
	 * @author brunsont
	 */
	public interface Handler{
		void onAddClicked(String addClicked);
	}
	
	private Handler handler;
	
	public PairwiseFormPanel(Handler handler) {
		this.handler = handler;
				
		initPanel();
	}

	private void initPanel() {
		//create necessary containers
//		FlowPanel topContainer = getTopContainer();
		FlowPanel leftContainer = getLeftContainer();
		FlowPanel rightContainer = getRightContainer();
		FlowPanel bottomContainer = getBottomContainer();
		
		
		
		//add containers to this
		FlowPanel main = new FlowPanel();
		main.setStyleName(RESOURCES.getCSS().panel());
//		main.add(topContainer);
		main.add(leftContainer);
		main.add(rightContainer);
		main.add(bottomContainer);
		this.add(main);
	}

	/**
	 * Top container for choosing a relationship type
	 * @return
	 */
	private FlowPanel getTopContainer() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().topContainer());
		ListBox dataType = new ListBox();
		dataType.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		
		result.add(new Label("Choose a relationship type:"));
		result.add(dataType);
		
		return result;
	}

	private FlowPanel getLeftContainer() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().leftContainer());
		
		result.add(new Label("Choose a relationship type:"));
		ListBox dataType = new ListBox();
		dataType.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		result.add(dataType);
		
		result.add(new Label("Choose a source:"));
		ListBox sourceType = new ListBox();
		sourceType.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		result.add(sourceType);
		
		result.add(new Label("Choose bioSources:"));
		IDGListBox bioSourcesListBox = new IDGListBox();
		bioSourcesListBox.setStyleName(RESOURCES.getCSS().multiSelectListBox());
		bioSourcesListBox.setVisibleItemCount(5);
		result.add(bioSourcesListBox);
		
		return result;
	}
	
	private FlowPanel getRightContainer() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().rightContainer());
		
		result.add(new Label("Select Options:"));
		IDGListBox tertiaryOptions = new IDGListBox();
		tertiaryOptions.setStyleName(RESOURCES.getCSS().multiSelectListBox());
		tertiaryOptions.setVisibleItemCount(5);
		result.add(tertiaryOptions);
		
		result.add(new Label("Choose Line Style:"));
		ListBox lineStyleListBox = new ListBox();
		lineStyleListBox.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		result.add(lineStyleListBox);
		
		result.add(new Label("Choose Line Color:"));
		ListBox lineColorListBox = new ListBox();
		lineColorListBox.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		result.add(lineColorListBox);
		
		return result;
	}
	
	private FlowPanel getBottomContainer() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().bottomPanel());
		
		Button addButton = new Button("Add");
		addButton.addClickHandler(e -> onAddButtonClicked());
		result.add(addButton);
		return result;
	}

	private void onAddButtonClicked() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * below here for resources
	 */
	public static Resources RESOURCES;
	static {
		RESOURCES = GWT.create(Resources.class);
		RESOURCES.getCSS().ensureInjected();
	}
	
	public interface Resources extends ClientBundle{
		@Source(ResourceCSS.CSS)
		ResourceCSS getCSS();
	}
	
	@CssResource.ImportedWithPrefix("idg-PairwiseFormPanel")
	public interface ResourceCSS extends CssResource{
		String CSS = "org/reactome/web/fi/tools/overlay/pairwise/PairwiseFormPanel.css";
	
		String panel();
		
		String dataTypeListBox();
		
		String topContainer();
		
		String leftContainer();
		
		String rightContainer();
		
		String multiSelectListBox();
		
		String bottomPanel();
	}
}
