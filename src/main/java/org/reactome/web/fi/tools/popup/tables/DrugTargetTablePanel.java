package org.reactome.web.fi.tools.popup.tables;

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

import com.google.gwt.user.client.ui.FlowPanel;

public class DrugTargetTablePanel extends FlowPanel{

	private IDGPopup.Resources RESOURCES;
	private List<DrugTargetResult> drugTargetData;
	
	public DrugTargetTablePanel(Set<String> diagramNodes, Resources RESOURCES) {
		this.RESOURCES = RESOURCES;
		fillDrugTargetData(diagramNodes);
	}

	/**
	 * Restructures data from DrugTargets to results for table
	 * @param diagramNodes
	 */
	private void fillDrugTargetData(Set<String> diagramNodes) {
		Map<String, String> uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
		Collection<Drug> drugs = IDGPopupFactory.get().getDrugTargets();
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
