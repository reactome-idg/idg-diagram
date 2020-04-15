package org.reactome.web.fi.tools.popup.tables;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.web.diagram.common.IconButton;
import org.reactome.web.fi.common.IDGListBox;
import org.reactome.web.fi.common.IDGTextBox;
import org.reactome.web.fi.data.loader.TCRDDataLoader;
import org.reactome.web.fi.data.loader.PairwiseDataLoader;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.popup.IDGPopup;
import org.reactome.web.fi.tools.popup.IDGPopupFactory;
import org.reactome.web.fi.tools.popup.IDGPopup.Resources;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseResultsTablePanel extends FlowPanel{

	public interface PairwiseTableHandler {
		void addInteractions(Set<PairwiseTableEntity> entities);
	}
	
	private PairwiseTableHandler handler;
	
	private IDGPopup.Resources RESOURCES;
	private List<PairwiseOverlayObject> pairwiseOverlayProperties;
	private Set<String> diagramNodes;
	
	private List<PairwiseTableEntity> tableEntities;
	private List<PairwiseTableEntity> filteredTableEntities;
	private PairwiseResultsTable resultsTable;
	private ListDataProvider<PairwiseTableEntity> provider;
	private SimplePager pager;
	
	private FlowPanel mainPanel;
	
	//ui for filtering panel
	private DialogBox filterPopup;
	private IDGTextBox filterGeneNameBox;
	private IDGListBox sourceListBox;
	private CheckBox showPositive;
	private CheckBox showNegative;

	private InlineLabel eTypeAndTissue;
	
	private DataOverlay dataOverlay;
	
	public PairwiseResultsTablePanel() {
		
	}

	private void initPanel() {
		mainPanel = new FlowPanel();
		
		createPairwiseTable(); //must create before adding results table to panel;
		mainPanel.add(resultsTable);
		
		mainPanel.add(getPagerPanel());
		
		this.add(mainPanel);
		mainPanel.setVisible(true); //Show by default
	}

	public void initialize(Set<String> diagramNodes, IDGPopup.Resources RESOURCES, PairwiseTableHandler handler) {
		this.RESOURCES = RESOURCES;
		this.handler = handler;
		this.pairwiseOverlayProperties = IDGPopupFactory.get().getCurrentPairwiseProperties();
		this.diagramNodes = diagramNodes;
		
		this.tableEntities = new ArrayList<>();
		this.filteredTableEntities = new ArrayList<>();
		
		initPanel();
		createFilterPopup();
		loadTable();
	}
	
	private void createPairwiseTable() {
		
		provider = new ListDataProvider<>();
		pager = new SimplePager();
		pager.setStyleName(RESOURCES.getCSS().pager());
		resultsTable = new PairwiseResultsTable(filteredTableEntities, provider, pager);
		resultsTable.setStyleName(RESOURCES.getCSS().table());
		
		//Add view button column
		ActionCell<PairwiseTableEntity> actionCell = new ActionCell<>("View", new ActionCell.Delegate<PairwiseTableEntity>() {
			@Override
			public void execute(PairwiseTableEntity object) {
				Set<PairwiseTableEntity> entity = new HashSet<>();
				entity.add(object);
				handler.addInteractions(entity);
			}
		});
		
		IdentityColumn<PairwiseTableEntity> viewColumn = new IdentityColumn<>(actionCell);
		resultsTable.addColumn(viewColumn,"View Relationship");
	}

	private FlowPanel getPagerPanel() {
		FlowPanel result = new FlowPanel();
		
		result.getElement().getStyle().setHeight(30, Unit.PX);
		result.setStyleName(RESOURCES.getCSS().pagerPanel());
		
		result.add(this.eTypeAndTissue = new InlineLabel());
		eTypeAndTissue.setStyleName(RESOURCES.getCSS().smallText());
		eTypeAndTissue.addStyleName(RESOURCES.getCSS().eTypeAndTissueLabel());
		result.add(pager);
		
		IconButton filterBtn = new IconButton(RESOURCES.filterWarning(), RESOURCES.getCSS().filterBtn(), "Filter Table Results", e -> onFilterButtonClicked(e));
		result.add(filterBtn);
		
		return result;
	}
	
	/**
	 * Handler for filterBtn click
	 * @param e 
	 */
	private void onFilterButtonClicked(ClickEvent e) {
		int x = e.getClientX()-150;
		int y = e.getClientY()-120;
		filterPopup.setPopupPosition(x, y);
		filterPopup.show();
	}

	/**
	 * This panel is for filtering table results;
	 * @return
	 */
	private void createFilterPopup() {
		
		filterPopup = new DialogBox();
		filterPopup.setStyleName(RESOURCES.getCSS().popupPanel());
		FlowPanel panel = new FlowPanel();
		filterPopup.setAutoHideEnabled(true);
		filterPopup.setModal(false);
		panel.addStyleName(RESOURCES.getCSS().filter());
		
		panel.add(new Label("Filter table results:"));
		
		filterGeneNameBox = new IDGTextBox();
		filterGeneNameBox.addKeyUpHandler(e -> filterTableEntities());
		filterGeneNameBox.setStyleName(RESOURCES.getCSS().filterTextBox());
		filterGeneNameBox.getElement().setPropertyString("placeholder", "Filter by Gene...");
		panel.add(filterGeneNameBox);
		
		panel.add(getSourceListBox());
		
		showPositive = new CheckBox("pos");
		showPositive.getElement().getStyle().setDisplay(Display.INLINE);
		showPositive.addClickHandler(e -> filterTableEntities());
		showNegative = new CheckBox("neg");
		showNegative.getElement().getStyle().setDisplay(Display.INLINE);
		showNegative.addClickHandler(e -> filterTableEntities());
		panel.add(showPositive);
		panel.add(showNegative);
		
		showPositive.setValue(true, false);
		showNegative.setValue(true, false);
		filterPopup.getElement().getStyle().setZIndex(30000);
		filterPopup.add(panel);
		filterPopup.hide();
	}
	
	/**
	 * Filter list box for filtering by source
	 * @return
	 */
	private IDGListBox getSourceListBox() {
		sourceListBox = new IDGListBox();
		
		setSourceListBox();
		sourceListBox.setMultipleSelect(false);
		sourceListBox.setSelectedIndex(0);
		sourceListBox.addChangeHandler(e -> filterTableEntities());
		
		sourceListBox.setStyleName(RESOURCES.getCSS().sourcesListBox());
		
		return sourceListBox;
	}
	
	private void setSourceListBox() {
		sourceListBox.clear();
		
		List<String> list = new ArrayList<>();
		list.add("Show all sources");
		for(PairwiseOverlayObject obj : pairwiseOverlayProperties) {
			list.add(obj.getId());
		}
		
		sourceListBox.setListItems(list);
	}
	
	/**
	 * Filter tableEntities based on set of filters
	 */
	private void filterTableEntities() {
		List<PairwiseTableEntity> newList = new ArrayList<>();
		
		String filterText = filterGeneNameBox.getText().toUpperCase();
		//sort interactors by data description and interactor name
		for(PairwiseTableEntity entity : tableEntities) {
			if(entity.getInteractorName() == null)continue;
			if(sourceListBox.getSelectedIndex() != 0 && !entity.getDataDesc().equals(sourceListBox.getSelectedItemText())) continue;
			if((!showPositive.getValue() && entity.getPosOrNeg().equals("positive"))||(!showNegative.getValue() && entity.getPosOrNeg().equals("negative"))) continue;
			
			//filter all columns based on filter box
			if(!entity.getInteractorName().toUpperCase().contains(filterText) 
					&& !entity.getSourceName().toUpperCase().contains(filterText)
					&& !entity.getOverlayValue().toUpperCase().contains(filterText)
					&& !entity.getPosOrNeg().toUpperCase().contains(filterText)
					&& !entity.getDataDesc().toUpperCase().contains(filterText)) continue;
			
			newList.add(entity);
		}
		
		provider.getList().clear();
		provider.getList().addAll(newList);
		resultsTable.updateSorter();
	}
	
	private void loadTable() {
		PairwiseDataLoader loader = new PairwiseDataLoader();
		PairwiseOverlayProperties props = new PairwiseOverlayProperties(pairwiseOverlayProperties, String.join(",", diagramNodes));
		loader.loadPairwiseData(props, false, new PairwiseDataLoader.Handler() {
			@Override
			public void onPairwiseLoaderError(Throwable exception) {
				Console.error(exception.getMessage());
			}
			@Override
			public void onPairwiseDataLoaded(List<PairwiseTableEntity> tableEntities) {
				PairwiseResultsTablePanel.this.tableEntities.addAll(tableEntities);
				filteredTableEntities = new ArrayList<>(tableEntities);
				provider.setList(filteredTableEntities);
				provider.refresh();
				resultsTable.updateSorter();
				getInitialInteractors(); //can add initial interactors only after uniprotToGeneMap and pairwiseOverlayMap are set.
				loadOverlay();
			}
		});
	}

	public void loadOverlay() {
		clearOverlayColumn();
		
		DataOverlayProperties props = IDGPopupFactory.get().getDataOverlayProperties();
		props.setUniprots(String.join(",", collectUniprots()));
		
		TCRDDataLoader loader = new TCRDDataLoader();
		loader.load(props, new TCRDDataLoader.Handler() {
			@Override
			public void onDataOverlayLoaded(DataOverlay dataOverlay) {
				PairwiseResultsTablePanel.this.dataOverlay = dataOverlay;
				updateTableData();
			}
			@Override
			public void onOverlayLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
		});
	}

	private void clearOverlayColumn() {
		for(PairwiseTableEntity entity: tableEntities)
			entity.setOverlayValue("Loading...");
		for(PairwiseTableEntity entity: filteredTableEntities)
			entity.setOverlayValue("Loading...");
		
		provider.refresh();
		
	}

	private void updateTableData() {
		this.dataOverlay.updateIdentifierValueMap();
		if(dataOverlay.isDiscrete()) {
			for(PairwiseTableEntity entity: tableEntities) {
				entity.setOverlayValue("");
				if(dataOverlay.getIdentifierValueMap().keySet().contains(entity.getInteractorId()))
					entity.setOverlayValue(dataOverlay.getLegendTypes().get(dataOverlay.getIdentifierValueMap().get(entity.getInteractorId()).intValue()));
			}
		}
		else {
			for(PairwiseTableEntity entity : tableEntities) {
				entity.setOverlayValue("");
				if(dataOverlay.getIdentifierValueMap().keySet().contains(entity.getInteractorId()))
					entity.setOverlayValue(""+dataOverlay.getIdentifierValueMap().get(entity.getInteractorId()));
			}
		}
		filteredTableEntities.clear();
		filteredTableEntities.addAll(tableEntities);
		provider.refresh();
		
		updateETypeAndTissueLabel();
	}
		
	/**
	 * Updates label to the left of the pager with the correct E type and tissue depending on overlay and tissue selection
	 */
	private void updateETypeAndTissueLabel() {
		if(dataOverlay == null) return;
		//Use cytoscape views version of dataOverlay so it still works 
		//if results are filtered to nothing
		if(dataOverlay.getEType().equals("Target Development Level"))
			this.eTypeAndTissue.setText("Overlay data source: " + dataOverlay.getEType());
		else
			this.eTypeAndTissue.setText("Overlay data source: " + dataOverlay.getTissueTypes().get(dataOverlay.getColumn()) + " - " + dataOverlay.getEType());
	
	}

	private Set<String> collectUniprots() {
		Set<String> uniprots = new HashSet<>();
		for(PairwiseTableEntity entity : tableEntities)
			uniprots.add(entity.getInteractorId());
		return uniprots;
	}

	private void getInitialInteractors() {
		Set<String> darkProteins = IDGPopupFactory.get().getTDarkSet();
		Set<PairwiseTableEntity> initialEntities = new HashSet<>();
		for(String diagramNode : diagramNodes) {
			int counter  = 0;
			for(PairwiseTableEntity entity: tableEntities) {
				if(counter == 10) break;
				if(entity.getSourceId() == diagramNode && darkProteins.contains(entity.getInteractorId())) {
					initialEntities.add(entity);
					counter++;
				}
			}
			for(PairwiseTableEntity entity: tableEntities) {
				if(counter == 10) break;
				if(entity.getSourceId() == diagramNode && !initialEntities.contains(entity)) {
					initialEntities.add(entity);
					counter++;
				}
			}
		}
		handler.addInteractions(initialEntities);
	}

	public void updateOverlayColumn(int column) {
		if(dataOverlay != null) {
			dataOverlay.setColumn(column);
			updateTableData();
		}
	}
	
	public void pairwisePropertiesChanged() {
		this.pairwiseOverlayProperties = IDGPopupFactory.get().getCurrentPairwiseProperties();
		tableEntities.clear();
		filteredTableEntities.clear();
		setSourceListBox();
		loadTable();
	}
}
