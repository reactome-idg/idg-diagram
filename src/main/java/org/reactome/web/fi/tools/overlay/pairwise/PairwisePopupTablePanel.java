package org.reactome.web.fi.tools.overlay.pairwise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactome.web.fi.common.IDGListBox;
import org.reactome.web.fi.common.IDGPager;
import org.reactome.web.fi.common.IDGPager.Handler;
import org.reactome.web.fi.common.IDGTextBox;
import org.reactome.web.fi.data.loader.OverlayLoader;
import org.reactome.web.fi.data.loader.PairwiseDataLoader;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.tools.overlay.pairwise.factory.PairwiseOverlayFactory;
import org.reactome.web.fi.tools.overlay.pairwise.model.PairwiseTableEntity;
import org.reactome.web.fi.tools.overlay.pairwise.results.PairwisePopupResultsTable;
import org.reactome.web.gwtCytoscapeJs.util.Console;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * 
 * @author brunsont
 *
 */
public class PairwisePopupTablePanel extends FlowPanel implements Handler{

	public interface PairwiseTableHandler {
		void addInteractions(Set<PairwiseTableEntity> entities);
	}
	
	private PairwiseTableHandler handler;
	
	private NewPairwisePopup.Resources RESOURCES;
	private List<PairwiseOverlayObject> pairwiseOverlayProperties;
	private Set<String> diagramNodes;
	
	private List<PairwiseTableEntity> tableEntities;
	private List<PairwiseTableEntity> filteredTableEntities;
	private PairwisePopupResultsTable resultsTable;
	private ListDataProvider<PairwiseTableEntity> provider;
	private IDGPager pager;
	
	private FlowPanel mainPanel;
	
	//ui for filtering panel
	private IDGTextBox filterGeneNameBox;
	private IDGListBox sourceListBox;
	private CheckBox showPositive;
	private CheckBox showNegative;

	private InlineLabel eTypeAndTissue;
	
	private DataOverlay dataOverlay;
	
	public PairwisePopupTablePanel(List<PairwiseOverlayObject> pairwiseOverlayProperties, Set<String> diagramNodes, NewPairwisePopup.Resources RESOURCES, PairwiseTableHandler handler) {
		this.RESOURCES = RESOURCES;
		this.handler = handler;
		this.pairwiseOverlayProperties = pairwiseOverlayProperties;
		this.diagramNodes = diagramNodes;
		
		this.tableEntities = new ArrayList<>();
		this.filteredTableEntities = new ArrayList<>();
		
		initPanel();
		loadTable();
	}

	private void initPanel() {
		Button infoButton = new Button("Show/Hide info");
		infoButton.setStyleName(RESOURCES.getCSS().infoButton());
		infoButton.addClickHandler(e -> onInfoButtonClicked());
		this.add(infoButton);
		
		mainPanel = new FlowPanel();
		mainPanel.add(getFilterPanel());
		
		createPairwiseTable(); //must create before adding results table to panel;
		mainPanel.add(resultsTable);
		
		mainPanel.add(getPagerPanel());
		
		this.add(mainPanel);
		mainPanel.setVisible(true); //Show by default
	}

	private void createPairwiseTable() {
		
		provider = new ListDataProvider<>();
		pager = new IDGPager(this);
		pager.setStyleName(RESOURCES.getCSS().pager());
		resultsTable = new PairwisePopupResultsTable(filteredTableEntities, provider, pager, new PairwisePopupResultsTable.Handler(){
			@Override
			public void onColumnSorted() {
				//TODO: Check if this is still needed after loading all of the overlay data
			}
		});
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
		
		return result;
	}
	
	private void onInfoButtonClicked() {
		mainPanel.setVisible(!mainPanel.isVisible());
	}
	
	/**
	 * This panel is for filtering table results;
	 * @return
	 */
	private FlowPanel getFilterPanel() {
		
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(RESOURCES.getCSS().filterPanel());
		
		panel.add(new InlineLabel("Filter table results:"));
		
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
				
		return panel;
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
		
		sourceListBox.getElement().getStyle().setMarginLeft(5, Unit.PX);
		sourceListBox.getElement().getStyle().setWidth(225, Unit.PX);
		
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
		onPageChanged();
	}
	
	@Override
	public void onPageChanged() {
		// TODO Auto-generated method stub
		
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
				PairwisePopupTablePanel.this.tableEntities.addAll(tableEntities);
				filteredTableEntities = new ArrayList<>(tableEntities);
				provider.setList(filteredTableEntities);
				provider.refresh();
				getInitialInteractors(); //can add initial interactors only after uniprotToGeneMap and pairwiseOverlayMap are set.
				loadOverlay();
			}
		});
	}

	public void loadOverlay() {
		DataOverlayProperties props = PairwiseOverlayFactory.get().getDataOverlayProperties();
		props.setUniprots(String.join(",", collectUniprots()));
		
		OverlayLoader loader = new OverlayLoader();
		loader.load(props, new OverlayLoader.Handler() {
			@Override
			public void onDataOverlayLoaded(DataOverlay dataOverlay) {
				PairwisePopupTablePanel.this.dataOverlay = dataOverlay;
				updateTableData();
			}
			@Override
			public void onOverlayLoadedError(Throwable exception) {
				exception.printStackTrace();
			}
		});
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
		
	
	private void updateETypeAndTissueLabel() {
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
		Set<String> darkProteins = PairwiseOverlayFactory.get().getTDarkSet();
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
//			for(int i=counter; i< 10; i++) {
//				if(i >= tableEntities.size())break;
//				PairwiseTableEntity entity = tableEntities.get(i); //offset by the number of diagram edges present in the array of table entities
//				if(entity.getSourceId() == diagramNode) {
//					initialEntities.add(entity);
//				}
//			}
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
		this.pairwiseOverlayProperties = PairwiseOverlayFactory.get().getCurrentPairwiseProperties();
		tableEntities.clear();
		filteredTableEntities.clear();
		setSourceListBox();
		loadTable();
	}
}
