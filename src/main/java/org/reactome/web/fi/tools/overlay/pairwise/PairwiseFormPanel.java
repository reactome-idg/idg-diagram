package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.web.fi.data.loader.IdgPairwiseLoader;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseFormPanel extends FlowPanel{
	
	/**
	 * interface for passing selected info back to entityOverlayPanel
	 * @author brunsont
	 */
	public interface Handler{
		void onAddClicked(PairwiseOverlayObject obj);
	}
	
	private Handler handler;
	
	private List<PairwiseDescriptionEntity> entityList;
	private List<String> dataTypesList;
	private boolean includeOrigin = false;
	private int lineStyleSelectedIndex = -1;
	
	private ListBox dataType;
	private ListBox provenanceListBox;
	private ListBox bioSourcesListBox;
	private ListBox originListBox;
	private TextBox lineColorTextBox;
	private InlineLabel warningLabel;
	private List<Button> lineStyleButtons;
	
	public PairwiseFormPanel(Handler handler) {
		this.handler = handler;
		dataTypesList = new ArrayList<>();
		lineStyleButtons = new ArrayList<>();
		
		loadDataDesc();
		initPanel();
	}

	/**
	 * loads pairwiseDesc data to populate form. Then causes form panel to initialize
	 */
	private void loadDataDesc() {
		IdgPairwiseLoader.loadDataDesc(new IdgPairwiseLoader.dataDescHandler() {
			@Override
			public void onDataDescLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
			@Override
			public void onDataDescLoaded(PairwiseDescriptionEntities entities) {
				entityList = entities.getPairwiseDescriptionEntities();
				updateDesData();
				setDataTypeListBox();
				cascadeFormUpdate();
			}
		});
	}

	/**
	 * updates list of dataTypes
	 */
	protected void updateDesData() {
		for(PairwiseDescriptionEntity entity: entityList)
			if(!dataTypesList.contains(entity.getDataType()))
				dataTypesList.add(entity.getDataType());
	}

	/**
	 * initializes gui
	 */
	private void initPanel() {
		//create necessary containers
		FlowPanel leftContainer = getLeftContainer();
		FlowPanel rightContainer = getRightContainer();
		FlowPanel bottomContainer = getBottomContainer();
		
		//add containers to this
		FlowPanel main = new FlowPanel();
		main.setStyleName(RESOURCES.getCSS().panel());
		main.add(leftContainer);
		main.add(rightContainer);
		main.add(bottomContainer);
		this.add(main);
	}

	/**
	 * Generates left hand panel options
	 * @return
	 */
	private FlowPanel getLeftContainer() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().leftContainer());
		
		result.add(new Label("Choose a relationship type:"));
		dataType = new ListBox();
		dataType.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		dataType.addChangeHandler(e -> onDataTypeChanged());
		result.add(dataType);
		
		result.add(new Label("Choose a provenance:"));
		provenanceListBox = new ListBox();
		provenanceListBox.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		provenanceListBox.addChangeHandler(e -> onProvenanceListBoxChanged());
		result.add(provenanceListBox);
		
		result.add(new Label("Choose bioSources:"));
		bioSourcesListBox = new ListBox();
		bioSourcesListBox.setStyleName(RESOURCES.getCSS().multiSelectListBox());
		bioSourcesListBox.addChangeHandler(e -> onBioSourcesListBoxChanged());
		result.add(bioSourcesListBox);
		
		result.add(new Label("Select Options:"));
		originListBox = new ListBox();
		originListBox.setStyleName(RESOURCES.getCSS().multiSelectListBox());
		result.add(originListBox);
		
		return result;
	}

	/**
	 * updates origin options
	 */
	private void onBioSourcesListBoxChanged() {
		String currentDataType = dataType.getSelectedItemText();
		String currentProvenance = provenanceListBox.getSelectedItemText();
		String currentBioSource = bioSourcesListBox.getSelectedItemText();
		Set<String> tertiaryOptions = new HashSet<>();
		for(PairwiseDescriptionEntity entity: entityList) {
			if(entity.getDataType() == currentDataType && entity.getProvenance() == currentProvenance && entity.getBioSource() == currentBioSource && entity.getOrigin() !=null)
				tertiaryOptions.add(entity.getOrigin());
		}
				
		originListBox.clear();
		if(tertiaryOptions.size() > 0) {
			for(String item : tertiaryOptions)
				originListBox.addItem(item);
			includeOrigin = true;
			
		}
		else{
			originListBox.addItem("No options to select...");
			includeOrigin = false;
		}
	}

	/**
	 * updates bioSources list box
	 */
	private void onProvenanceListBoxChanged() {
		String currentDataType = dataType.getSelectedItemText();
		String currentProvenance = provenanceListBox.getSelectedItemText();
		Set<String> bioSources = new HashSet<>();
		for(PairwiseDescriptionEntity entity : entityList) {
			if(entity.getDataType() == currentDataType && entity.getProvenance() == currentProvenance)
				bioSources.add(entity.getBioSource());
		}
		
		bioSourcesListBox.clear();
		for(String item : bioSources)
			bioSourcesListBox.addItem(item);
		onBioSourcesListBoxChanged();
	}

	/**
	 * Updates provenance list box
	 */
	private void onDataTypeChanged() {
		// TODO Auto-generated method stub
		String currentDataType = dataType.getSelectedItemText();
		Set<String> provenances = new HashSet<>();
		for(PairwiseDescriptionEntity entity : entityList)
			if(entity.getDataType() == currentDataType)
				provenances.add(entity.getProvenance());
		
		provenanceListBox.clear();
		for(String provenance : provenances)
			provenanceListBox.addItem(provenance);
		onProvenanceListBoxChanged();
	}

	/**
	 * Generates right hand panel
	 * @return
	 */
	private FlowPanel getRightContainer() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().rightContainer());
		
		result.add(new Label("Choose Line Style:"));
		FlowPanel lineStylePanel = getLineStyles();
		result.add(lineStylePanel);
		
		result.add(new Label("Choose Line Color:"));
		lineColorTextBox = new TextBox();
		lineColorTextBox.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		result.add(lineColorTextBox);
		
		return result;
	}
	
	private FlowPanel getLineStyles() {
		FlowPanel result = new FlowPanel();
		
		//block adds the top row of line style choices
		FlowPanel top = new FlowPanel();
		Image solidImg = new Image(RESOURCES.solidLine());
		SafeHtml solidSafe = SafeHtmlUtils.fromTrustedString(solidImg.toString());
		Button solid = new Button(solidSafe);
		solid.setStyleName(RESOURCES.getCSS().lineStyleButton());
		lineStyleButtons.add(solid);
		top.add(solid);
		Image closeImg = new Image(RESOURCES.closeDashed());
		SafeHtml closeSafe = SafeHtmlUtils.fromTrustedString(closeImg.toString());
		Button close = new Button(closeSafe);
		close.setStyleName(RESOURCES.getCSS().lineStyleButton());
		lineStyleButtons.add(close);
		top.add(close);
		Image mediumImg = new Image(RESOURCES.mediumDashed());
		SafeHtml mediumSafe = SafeHtmlUtils.fromTrustedString(mediumImg.toString());
		Button medium = new Button(mediumSafe);
		medium.setStyleName(RESOURCES.getCSS().lineStyleButton());
		lineStyleButtons.add(medium);
		top.add(medium);
		
		//block adds bottom row of line style choices
		Image farImg = new Image(RESOURCES.farDashed());
		SafeHtml farSafe = SafeHtmlUtils.fromTrustedString(farImg.toString());
		Button far = new Button(farSafe);
		far.setStyleName(RESOURCES.getCSS().lineStyleButton());
		lineStyleButtons.add(far);
		top.add(far);
		Image dottedImg = new Image(RESOURCES.dotted());
		SafeHtml dottedSafe = SafeHtmlUtils.fromTrustedString(dottedImg.toString());
		Button dotted = new Button(dottedSafe);
		dotted.setStyleName(RESOURCES.getCSS().lineStyleButton());
		lineStyleButtons.add(dotted);
		top.add(dotted);
		Image farDottedImg = new Image(RESOURCES.farDotted());
		SafeHtml farDottedSafe = SafeHtmlUtils.fromTrustedString(farDottedImg.toString());
		Button farDotted = new Button(farDottedSafe);
		farDotted.setStyleName(RESOURCES.getCSS().lineStyleButton());
		lineStyleButtons.add(farDotted);
		top.add(farDotted);
		
		for(Button btn : lineStyleButtons)
			btn.addClickHandler(e -> onLineStyleButtonSelected(e));
		
		result.add(top);
		return result;
	}
	
	private void onLineStyleButtonSelected(ClickEvent e) {
		Button selected = (Button) e.getSource();
		selectLineStyleButton(selected);
	}

	/**
	 * Selects line style button based on passed in button
	 * @param selected
	 */
	private void selectLineStyleButton(Button selected) {
		for(Button btn : lineStyleButtons)
			btn.removeStyleName(RESOURCES.getCSS().lineStyleButtonSelected());
		selected.addStyleName(RESOURCES.getCSS().lineStyleButtonSelected());
		lineStyleSelectedIndex = lineStyleButtons.indexOf(selected);
	}

	/*
	 * Generates bottom panel for add button
	 */
	private FlowPanel getBottomContainer() {
		FlowPanel result = new FlowPanel();
		
		Button addButton = new Button("Add");
		addButton.setStyleName(RESOURCES.getCSS().addButton());
		addButton.addClickHandler(e -> onAddButtonClicked());
		result.add(addButton);
		
		warningLabel = new InlineLabel();
		warningLabel.setStyleName(RESOURCES.getCSS().warningLabel());
		result.add(warningLabel);
		
		
		return result;
	}
	
	/**
	 * Sets Data Type list box that directs fill of other list boxes
	 */
	private void setDataTypeListBox() {
		for(String type : dataTypesList)
			dataType.addItem(type);
	}

	/**
	 * Fires when add button is clicked to push selection back to parent panel.
	 */
	private void onAddButtonClicked() {
		warningLabel.setText(""); //reset warning label to empy string every time add button is clicked
		
		String relationship = provenanceListBox.getSelectedItemText() + "|"
							  +bioSourcesListBox.getSelectedItemText() + "|"
							  + dataType.getSelectedItemText();
		if(includeOrigin) relationship += "|" + originListBox.getSelectedItemText();
		
		//check to make sure a line style and color are selected
		String color = lineColorTextBox.getText();
		if(color.length() != 3 && color.length() !=6) {
			warningLabel.setText("Please Select a line color");
			return;
		}
		else if(lineStyleSelectedIndex == -1) {
			warningLabel.setText("Please Select a line style");
			return;
		}
		
		PairwiseOverlayObject obj = new PairwiseOverlayObject(relationship, lineStyleSelectedIndex, lineColorTextBox.getText());
				
		handler.onAddClicked(obj);					  
	}
	
	/**
	 * Sets all options based on passed in instance of PairwiseOverlayObject
	 * @param obj
	 */
	public void insertData(PairwiseOverlayObject obj) {
		List<String> idList = Arrays.asList(obj.getId().split("\\|"));
		
		//set list boxes
		for(int i=0; i<dataType.getItemCount(); i++)
			if(dataType.getItemText(i) == idList.get(2)) {
				dataType.setSelectedIndex(i);
				onDataTypeChanged();
			}
		for(int i=0; i<provenanceListBox.getItemCount(); i++)
			if(provenanceListBox.getItemText(i) == idList.get(0)) {
				provenanceListBox.setSelectedIndex(i);
				onProvenanceListBoxChanged();
			}
		for(int i=0; i<bioSourcesListBox.getItemCount(); i++)
			if(bioSourcesListBox.getItemText(i) == idList.get(1)) {
				bioSourcesListBox.setSelectedIndex(i);
				onBioSourcesListBoxChanged();
			}
		if(idList.size() == 4)
			for(int i=0; i<originListBox.getItemCount(); i++)
				if(originListBox.getItemText(i) == idList.get(3)) {
					originListBox.setSelectedIndex(i);
				}
		
		//set line style
		selectLineStyleButton(lineStyleButtons.get(obj.getLineStyleIndex()));
		
		//set text of lineColorTextBox
		lineColorTextBox.setText(obj.getLineColorHex());
	}
	
	private void cascadeFormUpdate() {
		onDataTypeChanged();
		onProvenanceListBoxChanged();
		onBioSourcesListBoxChanged();
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
		
		@Source("images/solid_line.jpg")
		ImageResource solidLine();
		
		@Source("images/close_dashed.jpg")
		ImageResource closeDashed();
		
		@Source("images/medium_dashed.jpg")
		ImageResource mediumDashed();
		
		@Source("images/far_dashed.jpg")
		ImageResource farDashed();
		
		@Source("images/close_dotted.jpg")
		ImageResource dotted();
		
		@Source("images/far_dotted.jpg")
		ImageResource farDotted();
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
				
		String addButton();
		
		String lineStyleButton();
		
		String lineStyleButtonSelected();
		
		String warningLabel();
	}
}
