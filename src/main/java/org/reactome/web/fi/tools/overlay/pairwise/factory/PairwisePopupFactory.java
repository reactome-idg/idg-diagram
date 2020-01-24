package org.reactome.web.fi.tools.overlay.pairwise.factory;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.PairwisePopup;

public class PairwisePopupFactory {

	private static PairwisePopupFactory factory;
	
	private List<String> currentPopups;
	private List<PairwiseOverlayObject> currentPairwiseObjects;
	
	
	private PairwisePopupFactory() {
		currentPopups = new ArrayList<>();
		currentPairwiseObjects = new ArrayList<>();
	}
	
	public static PairwisePopupFactory get() {
		if(factory == null)
			factory = new PairwisePopupFactory();
		return factory;
	}
	
	public void openPopup(GraphObject graphObject) {
		if(!currentPopups.contains(graphObject.getStId()) && currentPairwiseObjects != null) {
			currentPopups.add(graphObject.getStId());
			PairwisePopup popup = new PairwisePopup(graphObject, currentPairwiseObjects);
			popup.show();
		}
	}
	
	public void openPopup(String uniprot, String geneName) {
		if(!currentPopups.contains(uniprot) && currentPairwiseObjects != null) {
			currentPopups.add(uniprot);
			PairwisePopup popup = new PairwisePopup(uniprot, geneName, currentPairwiseObjects);
			popup.show();
		}
	}
	
	public void removePopup(String id) {
		currentPopups.remove(id);
	}
	
	public void setCurrentPairwiseProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.currentPairwiseObjects = pairwiseOverlayObjects;
	}
	
	public List<PairwiseOverlayObject> getCurrentPairwiseProperties(){
		return this.currentPairwiseObjects;
	}
}
