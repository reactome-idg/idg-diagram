package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.web.fi.common.IDGListBox;
import org.reactome.web.fi.data.loader.IdgPairwiseLoader;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Window;
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
	
	private List<PairwiseDescriptionEntity> entityList;
	private List<String> dataTypesList;
	private boolean includeTertiary = false;
	
	private ListBox dataType;
	private ListBox provenanceListBox;
	private IDGListBox bioSourcesListBox;
	private IDGListBox tertiaryOptionsListBox;
	private ListBox lineStyleListBox;
	private ListBox lineColorListBox;
	
	public PairwiseFormPanel(Handler handler) {
		this.handler = handler;
		dataTypesList = new ArrayList<>();
		
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
				onDataTypeChanged();
				onProvenanceListBoxChanged();
				onBioSourcesListBoxChanged();
			}
		});
	}

	protected void updateDesData() {
		for(PairwiseDescriptionEntity entity: entityList)
			if(!dataTypesList.contains(entity.getDataType()))
				dataTypesList.add(entity.getDataType());
	}

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
		bioSourcesListBox = new IDGListBox();
		bioSourcesListBox.setStyleName(RESOURCES.getCSS().multiSelectListBox());
		bioSourcesListBox.addChangeHandler(e -> onBioSourcesListBoxChanged());
		result.add(bioSourcesListBox);
		
		return result;
	}

	private void onBioSourcesListBoxChanged() {
		String currentDataType = dataType.getSelectedItemText();
		String currentProvenance = provenanceListBox.getSelectedItemText();
		String currentBioSource = bioSourcesListBox.getSelectedItemText();
		Set<String> tertiaryOptions = new HashSet<>();
		for(PairwiseDescriptionEntity entity: entityList) {
			if(entity.getDataType() == currentDataType && entity.getProvenance() == currentProvenance && entity.getBioSource() == currentBioSource && entity.getOrigin() !=null)
				tertiaryOptions.add(entity.getOrigin());
		}
				
		tertiaryOptionsListBox.clear();
		if(tertiaryOptions.size() > 0) {
			tertiaryOptionsListBox.setListItems(tertiaryOptions);
			includeTertiary = true;
			
		}
		else{
			tertiaryOptionsListBox.addItem("No options to select...");
			includeTertiary = false;
		}
	}

	private void onProvenanceListBoxChanged() {
		
		String currentDataType = dataType.getSelectedItemText();
		String currentProvenance = provenanceListBox.getSelectedItemText();
		Set<String> bioSources = new HashSet<>();
		for(PairwiseDescriptionEntity entity : entityList) {
			if(entity.getDataType() == currentDataType && entity.getProvenance() == currentProvenance)
				bioSources.add(entity.getBioSource());
		}
		
		bioSourcesListBox.clear();
		bioSourcesListBox.setListItems(bioSources);
		onBioSourcesListBoxChanged();
	}

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

	private FlowPanel getRightContainer() {
		FlowPanel result = new FlowPanel();
		result.setStyleName(RESOURCES.getCSS().rightContainer());
		
		result.add(new Label("Select Options:"));
		tertiaryOptionsListBox = new IDGListBox();
		tertiaryOptionsListBox.setStyleName(RESOURCES.getCSS().multiSelectListBox());
		result.add(tertiaryOptionsListBox);
		
		result.add(new Label("Choose Line Style:"));
		lineStyleListBox = new ListBox();
		lineStyleListBox.setStyleName(RESOURCES.getCSS().dataTypeListBox());
		result.add(lineStyleListBox);
		
		result.add(new Label("Choose Line Color:"));
		lineColorListBox = new ListBox();
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
	
	/**
	 * Sets Data Type list box that directs fill of other list boxes
	 */
	private void setDataTypeListBox() {
		for(String type : dataTypesList)
			dataType.addItem(type);
			
	}

	private void onAddButtonClicked() {
		String relationship = provenanceListBox.getSelectedItemText() + "|"
							  +bioSourcesListBox.getSelectedItemText() + "|"
							  + dataType.getSelectedItemText();
		if(includeTertiary) relationship += "|" + tertiaryOptionsListBox.getSelectedItemText();
				
		handler.onAddClicked(relationship);
		
							  
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
