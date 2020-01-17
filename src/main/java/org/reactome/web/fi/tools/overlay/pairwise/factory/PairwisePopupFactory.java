package org.reactome.web.fi.tools.overlay.pairwise.factory;

import java.util.List;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.fi.tools.overlay.pairwise.PairwisePopup;

public class PairwisePopupFactory {

	private static PairwisePopupFactory factory;
	
	private List<String> currentPopups;
	
	
	private PairwisePopupFactory() {/*Nothing Here*/}
	
	public static PairwisePopupFactory get() {
		if(factory == null)
			factory = new PairwisePopupFactory();
		return factory;
	}
	
	public void openPopup(GraphObject graphObject) {
		if(!currentPopups.contains(graphObject.getStId())) {
			currentPopups.add(graphObject.getStId());
			PairwisePopup popup = new PairwisePopup(graphObject);
			popup.show();
		}
	}
	
	public void openPopup(String uniprot, String geneName) {
		if(!currentPopups.contains(uniprot)) {
			currentPopups.add(uniprot);
			PairwisePopup popup = new PairwisePopup(uniprot, geneName);
			popup.show();
		}
	}
	
	public void removePopup(String id) {
		currentPopups.remove(id);
	}
}
