package org.reactome.web.fi.tools.popup.tables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.fi.data.loader.PairwiseInfoService;
import org.reactome.web.fi.data.model.drug.Drug;
import org.reactome.web.fi.tools.factory.IDGPopupFactory;
import org.reactome.web.fi.tools.popup.IDGPopup;
import org.reactome.web.fi.tools.popup.IDGPopup.Resources;
import org.reactome.web.fi.tools.popup.tables.models.DrugTargetResult;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ListDataProvider;

public class DrugTargetTablePanel extends FlowPanel{

	private IDGPopup.Resources RESOURCES;
	private List<DrugTargetResult> drugTargetData;
	
	private DrugTargetResultsTable resultsTable;
	private ListDataProvider<DrugTargetResult> provider;
	private SimplePager pager;
	
	public DrugTargetTablePanel() {
		
	}

	public void initialize(Set<String> diagramNodes, Resources RESOURCES) {
		this.RESOURCES = RESOURCES;
		fillDrugTargetData(diagramNodes);
		
		initPanel();
	}
	
	private void initPanel() {
		FlowPanel mainPanel = new FlowPanel();
		
		createDrugTargetTable();
		mainPanel.add(resultsTable);
		
		mainPanel.add(getPagerPanel());
		
		this.add(mainPanel);
		mainPanel.setVisible(true);
	}

	private void createDrugTargetTable() {
		provider = new ListDataProvider<>();
		pager = new SimplePager();
		pager.setStyleName(RESOURCES.getCSS().pager());
		resultsTable = new DrugTargetResultsTable(drugTargetData, provider, pager);
		resultsTable.setStyleName(RESOURCES.getCSS().table());
	}
	
	private FlowPanel getPagerPanel() {
		FlowPanel result = new FlowPanel();
		
		result.getElement().getStyle().setHeight(30, Unit.PX);
		result.setStyleName(RESOURCES.getCSS().pagerPanel());
		result.add(pager);
		
		return result;
	}

	/**
	 * Restructures data from DrugTargets to results for table
	 * @param diagramNodes
	 */
	private void fillDrugTargetData(Set<String> diagramNodes) {
		Map<String, String> uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
		Collection<Drug> drugs = IDGPopupFactory.get().getDrugTargets();
		drugTargetData = new ArrayList<>();
		drugs.forEach(drug -> {
			drug.getDrugInteractions().forEach((k,v) -> {
				if(!diagramNodes.contains(k)) return;
				drugTargetData.add(new DrugTargetResult(k, 
														uniprotToGeneMap.get(k),
														drug.getName(),
														drug.getCompoundChEMBLId(),
														v.getActionType(),
														v.getActivityType(),
														v.getActivityValue()));
			});
		});
	}
}
