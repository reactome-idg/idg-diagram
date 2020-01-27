package org.reactome.web.fi.tools.overlay.pairwise.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.diagram.data.graph.model.GraphObject;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.tools.overlay.pairwise.PairwisePopup;

public class PairwisePopupFactory{

	private static PairwisePopupFactory factory;
	
	private Map<String, PairwisePopup> popupMap;
	private List<PairwiseOverlayObject> currentPairwiseObjects;
	
	private DataOverlayProperties dataOverlayProperties;
	
	private PairwisePopupFactory() {
		popupMap = new HashMap<>();
		currentPairwiseObjects = new ArrayList<>();
	}
	
	public static PairwisePopupFactory get() {
		if(factory == null)
			factory = new PairwisePopupFactory();
		return factory;
	}
	
	public void openPopup(GraphObject graphObject) {
		if(!popupMap.keySet().contains(graphObject.getStId()) && currentPairwiseObjects.size() > 0) {
			PairwisePopup popup = new PairwisePopup(graphObject, currentPairwiseObjects, dataOverlayProperties);
			popupMap.put(graphObject.getStId(), popup);
			
			popup.show();
		}
	}
	
	public void openPopup(String uniprot, String geneName) {
		if(!popupMap.keySet().contains(uniprot) && currentPairwiseObjects.size() > 0) {
			PairwisePopup popup = new PairwisePopup(uniprot, geneName, currentPairwiseObjects, dataOverlayProperties);
			popupMap.put(uniprot, popup);
			popup.show();
		}
	}
	
	public void removePopup(String id) {
		popupMap.remove(id);
	}
	
	public void setCurrentPairwiseProperties(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.currentPairwiseObjects = pairwiseOverlayObjects;
	}
	
	public List<PairwiseOverlayObject> getCurrentPairwiseProperties(){
		return this.currentPairwiseObjects;
	}

	public void setDataOverlayProperties(DataOverlayProperties dataOverlayProperties) {
		this.dataOverlayProperties = dataOverlayProperties;
		for(PairwisePopup popup : popupMap.values())
			popup.loadOverlay(this.dataOverlayProperties);
	}

}
